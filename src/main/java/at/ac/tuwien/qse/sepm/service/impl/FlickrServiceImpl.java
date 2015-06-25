package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.*;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class FlickrServiceImpl implements FlickrService {

    private static final String API_KEY = "206f4ffa559e5e48301f84f046bf208b";
    private static final String SECRET = "f58343bd30c130b6";
    private static final String tmpDir = "src/main/resources/tmp/";
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
            boolean useGeoData, Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback,
            Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler) throws ServiceException {
        if (i == 0) {
            searcher = new AsyncSearcher(tags, latitude, longitude, useGeoData, callback, progressCallback, errorHandler);
        }
        executorService.submit(searcher);
        return searcher;
    }

    @Override
    public Cancelable downloadPhotos(List<at.ac.tuwien.qse.sepm.entities.Photo> photos, Consumer<Double> progressCallback,
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
        File directory = new File(tmpDir);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            logger.debug("Deleting photos from tmp folder...");
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
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


    /**
     * Creates photo with geo data. This photo is available at flickr.
     *
     * @param id     the id of the photo at flickr
     * @param format the format of the photo
     * @return the photo
     * @throws ServiceException if the id or format is not correct or if the a photo with that id
     * doesn't exist.
     */
    public at.ac.tuwien.qse.sepm.entities.Photo createPhotoWithGeoData(String id, String format)
            throws ServiceException {
        logger.debug("Creating photo with geo data with id: {} and format: {}", id, format);
        if(id==null || format == null){
            throw new ServiceException("Photo id or format invalid.");
        }
        GeoData geoData = null;
        try {
            geoData = flickr.getPhotosInterface().getGeoInterface().getLocation(id);
        } catch (FlickrException e) {
           throw new ServiceException(e.getMessage());
        }
        at.ac.tuwien.qse.sepm.entities.Photo created = new at.ac.tuwien.qse.sepm.entities.Photo();
        created.setPath(tmpDir + id + "." + format);
        created.getData().setLatitude(geoData.getLatitude());
        created.getData().setLongitude(geoData.getLongitude());
        created.getData().setRating(Rating.NONE);

        // attach flickr photographer
        Photographer photographer = photographerService.readAll()
                .stream()
                .filter(p -> p.getId() == 2)
                .findFirst()
                .orElse(new Photographer(1, null)); // default photographer
        created.getData().setPhotographer(photographer);
        created.getData().setPlace(new Place(1, "Unknown city", "Unknown country", 0.0, 0.0));

        return created;
    }

    private class AsyncSearcher extends CancelableTask {
        private String[] tags;
        private double latitude;
        private double longitude;
        private boolean useGeoData;
        private Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback;
        private Consumer<Double> progressCallback;
        private ErrorHandler<ServiceException> errorHandler;
        private PhotoList<Photo> list;

        public AsyncSearcher(String tags[], double latitude, double longitude, boolean useGeoData,
                Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback,
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
                    if (!isRunning())
                        return;
                    logger.debug("Downloading photo nr {} ...", i + 1);
                    String farmId = p.getFarm();
                    String serverId = p.getServer();
                    String id = p.getId();
                    String originalSecret = flickr.getPhotosInterface().getInfo(p.getId(), p.getSecret()).getOriginalSecret();
                    String secret = p.getSecret();
                    String format = p.getOriginalFormat();
                    if (!originalSecret.isEmpty() && !secret.isEmpty()) {
                        String url = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + originalSecret + "_o." + format;
                        String mediumSizeUrl = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + "_z." + format;
                        downloadPhotoFromFlickr(mediumSizeUrl, id, format);
                        at.ac.tuwien.qse.sepm.entities.Photo downloaded = createPhotoWithGeoData(id, format);
                        logger.debug("Downloaded photo {}", downloaded);
                        callback.accept(downloaded);
                        progressCallback.accept((double)nrOfDownloadedPhotos/(double)nrOfPhotosToDownload);
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

        private List<at.ac.tuwien.qse.sepm.entities.Photo> photos;
        private Consumer<Double> progressCallback;
        private ErrorHandler<ServiceException> errorHandler;

        public AsyncDownloader(List<at.ac.tuwien.qse.sepm.entities.Photo> photos, Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler){
            this.photos = photos;
            this.progressCallback = progressCallback;
            this.errorHandler = errorHandler;
        }

        @Override
        protected void execute() {
            for(at.ac.tuwien.qse.sepm.entities.Photo p: photos){
                try {
                    Path path = Paths.get(p.getPath());
                    String id = path.getFileName()+"_o";
                    String format = new ImageFileFilter().getExtension(p.getPath());
                    downloadPhotoFromFlickr(p.getPath(),id,format);
                    p.setPath(Paths.get(tmpDir + "/" + id + "." + format).toAbsolutePath().toString());
                    exifService.setDateAndGeoData(p);
                    ioHandler.copyFromTo(Paths.get(p.getPath()),Paths.get(System.getProperty("user.home"),"travelimg/"+id+"."+format));
                } catch (ServiceException e) {

                } catch (IOException e) {

                }
            }
        }
    }
}
