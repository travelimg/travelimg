package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.CancelableTask;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.IOHandler;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class FlickrServiceImpl implements FlickrService {

    private static final String API_KEY = "206f4ffa559e5e48301f84f046bf208b";
    private static final String SECRET = "f58343bd30c130b6";
    private static final String tmpDir = System.getProperty("java.io.tmpdir");
    private static final Logger logger = LogManager.getLogger();
    private static int nrOfPhotosToDownload = 10;
    public static long oneMB = 1048576;
    private AsyncSearcher searcher;
    private Flickr flickr;
    private int i = 0;

    @Autowired
    private PhotographerService photographerService;
    @Autowired
    private ExifService exifService;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private ExecutorService executorService;


    public FlickrServiceImpl() {
        this.flickr = new Flickr(API_KEY, SECRET, new REST());
    }

    @Override
    public Cancelable searchPhotos(String tags[], double latitude, double longitude,
            boolean useGeoData, Consumer<Photo> callback,
            Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler) throws ServiceException {
        if (i == 0) {
            searcher = new AsyncSearcher(tags, latitude, longitude, useGeoData, callback, progressCallback, errorHandler);
        }
        executorService.submit(searcher);
        return searcher;
    }

    @Override
    public Cancelable downloadPhotos(List<Photo> photos, Consumer<Double> progressCallback,
            ErrorHandler<ServiceException> errorHandler){
        AsyncDownloader downloader = new AsyncDownloader(photos, progressCallback, errorHandler);
        executorService.submit(downloader);
        return downloader;
    }

    @Override
    public void reset() {
        i = 0;
        nrOfPhotosToDownload = 10;
    }

    @Override
    public void close() {
        /*File directory = new File(tmpDir);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            logger.debug("Deleting photos from tmp folder...");
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }*/
    }

    /**
     * Downloads a photo from flickr
     *
     * @param url                  the url of the photo
     * @param id                   the id of the photo
     * @param format               the format of the photo
     * @throws ServiceException    if the photo can't be downloaded.
     */
    public void downloadPhotoFromFlickr(String url, String id, String format) throws ServiceException {
        if(id == null || id.trim().isEmpty() || format == null || format.trim().isEmpty()){
            throw new ServiceException("Photo id or format invalid.");
        }
        HttpURLConnection httpConnection = null;
        try {
            httpConnection = (HttpURLConnection) (new URL(url).openConnection());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }

        try(BufferedInputStream in = new BufferedInputStream((httpConnection.getInputStream()));
                FileOutputStream fout = new FileOutputStream(tmpDir + id + "." + format);)
        {
            long completeFileSize = httpConnection.getContentLength();
            logger.debug("Size of the photo is {} MB", (double)completeFileSize/oneMB);
            final byte data[] = new byte[1024];
            int count;
            long downloadedFileSize = 0;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
                downloadedFileSize = downloadedFileSize + count;
                // maybe produces too much output
                // logger.debug("Downloaded {} MB", (double)downloadedFileSize/oneMB);
            }
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private class AsyncSearcher extends CancelableTask {
        private String[] tags;
        private double latitude;
        private double longitude;
        private boolean useGeoData;
        private Consumer<Photo> callback;
        private Consumer<Double> progressCallback;
        private ErrorHandler<ServiceException> errorHandler;
        private PhotoList<Photo> list;

        public AsyncSearcher(String tags[], double latitude, double longitude, boolean useGeoData,
                Consumer<Photo> callback,
                Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler) {
            super();
            this.tags = tags;
            this.latitude = latitude;
            this.longitude = longitude;
            this.useGeoData = useGeoData;
            this.callback = callback;
            this.progressCallback = progressCallback;
            this.errorHandler = errorHandler;
        }

        /**
         * Downloads 10 photos( beginning at index i ) from flickr using tags and/or geo data.
         */
        @Override
        protected void execute() {
            Paths.get(tmpDir).toFile().mkdirs();
            try {
                if (i == 0) {
                    SearchParameters searchParameters = new SearchParameters();
                    searchParameters.setTags(tags);

                    logger.debug("Using for search tags {}", (Object[]) tags);
                    if (useGeoData) {
                        logger.debug("Using for search latitude {} and longitude {}", latitude, longitude);
                        searchParameters.setLatitude(String.valueOf(latitude));
                        searchParameters.setLongitude(String.valueOf(longitude));
                        searchParameters.setRadius(5);
                    }
                    searchParameters.setHasGeo(true);
                    logger.debug("Searching photos using the flickr api...");
                    list = flickr.getPhotosInterface().search(searchParameters, 250, 1);
                    logger.debug("Found {} photos.", list.size());
                }
                int nrOfDownloadedPhotos = 0;
                if (list.size() - i < nrOfPhotosToDownload) {
                    nrOfPhotosToDownload = list.size() - i;
                }
                logger.debug("Start downloading {} photos", nrOfPhotosToDownload);
                for (; i < list.size(); i++) {

                    Photo p = list.get(i);
                    if (nrOfDownloadedPhotos == nrOfPhotosToDownload) {
                        break;
                    }
                    if (!isRunning()){
                        logger.debug("Download interrupted");
                        break;
                    }
                    logger.debug("Downloading photo nr {} ...", i + 1);
                    p.setOriginalSecret(
                            flickr.getPhotosInterface().getInfo(p.getId(), p.getSecret())
                                    .getOriginalSecret());
                    if (!p.getOriginalSecret().isEmpty() && !p.getSecret().isEmpty()) {
                        String mediumSizeUrl = "https://farm" + p.getFarm() + ".staticflickr.com/" + p.getServer() + "/" + p.getId() + "_" + p.getSecret() + "_z." + p.getOriginalFormat();
                        GeoData geoData = flickr.getPhotosInterface().getGeoInterface().getLocation(p.getId());
                        Photo photoWIthTags = flickr.getTagsInterface().getListPhoto(p.getId());
                        p.setTags(photoWIthTags.getTags());
                        p.setGeoData(geoData);
                        downloadPhotoFromFlickr(mediumSizeUrl, p.getId(), p.getOriginalFormat());
                        if(!isRunning()){
                            logger.debug("Download interrupted");
                            break;
                        }
                        logger.debug("Downloaded photo id={}, geodata=[{},{}], tags={}", p.getId(),p.getGeoData().getLatitude(),p.getGeoData().getLongitude(),p.getTags());
                        callback.accept(p);
                        progressCallback.accept((double) nrOfDownloadedPhotos / (double) nrOfPhotosToDownload);
                        nrOfDownloadedPhotos++;
                    } else {
                        logger.debug("Can't get original secret for photo.");
                    }
                }
                progressCallback.accept(1.0);

            } catch (FlickrException | ServiceException e) {
                errorHandler.propagate(new ServiceException("Failed to download photo", e));
            }
        }
    }

    private class AsyncDownloader extends CancelableTask{

        private List<Photo> photos;
        private Consumer<Double> progressCallback;
        private ErrorHandler<ServiceException> errorHandler;

        public AsyncDownloader(List<Photo> photos, Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler){
            this.photos = photos;
            this.progressCallback = progressCallback;
            this.errorHandler = errorHandler;
        }

        @Override
        protected void execute() {

            for(int i=0; i<photos.size();i++){
                try {
                    Photo p = photos.get(i);
                    String url = "https://farm" + p.getFarm() + ".staticflickr.com/" + p.getServer() + "/" + p.getId() + "_" + p.getOriginalSecret() + "_o." + p.getOriginalFormat();
                    downloadPhotoFromFlickr(url,p.getId()+"_o",p.getOriginalFormat());
                    progressCallback.accept((double)(i+1)/(double)photos.size());
                } catch (ServiceException e) {
                    progressCallback.accept(1.0);
                }
            }
            progressCallback.accept(1.0);
        }
    }
}
