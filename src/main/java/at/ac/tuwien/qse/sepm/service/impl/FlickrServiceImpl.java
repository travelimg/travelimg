package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.CancelableTask;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FlickrServiceImpl implements FlickrService {

    private static final String API_KEY ="206f4ffa559e5e48301f84f046bf208b";
    private static final String SECRET ="f58343bd30c130b6";
    private static final String tmpDir ="src/main/resources/tmp/";
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private AsyncDownloader downloader;
    private Flickr flickr;
    private int i = 0;
    private static final Logger logger = LogManager.getLogger();

    public FlickrServiceImpl(){
        this.flickr = new Flickr(API_KEY, SECRET, new REST());
    }

    @Override
    public Cancelable downloadPhotos(String tags[], double latitude, double longitude, boolean useGeoData, Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback,  Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler) throws ServiceException{
        if(i==0){
            downloader = new AsyncDownloader(tags, latitude, longitude, useGeoData, callback, progressCallback, errorHandler);
        }
        executorService.submit(downloader);
        return downloader;
    }

    @Override
    public void reset() {
        i = 0;
    }

    @Override
    public void close() {
        logger.debug("Shutting down executor...");
        executorService.shutdown();
        File directory = new File(tmpDir);
        if(directory.exists()){
            File[] files = directory.listFiles();
            logger.debug("Deleting photos from tmp folder...");
            for(int i=0; i<files.length; i++) {
                files[i].delete();
            }
        }
    }

    private class AsyncDownloader extends CancelableTask {
        private String[] tags;
        private double latitude;
        private double longitude;
        private boolean useGeoData;
        private Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback;
        private Consumer<Double> progressCallback;
        private ErrorHandler<ServiceException> errorHandler;
        private PhotoList<Photo> list;

        public AsyncDownloader(String tags[], double latitude, double longitude, boolean useGeoData, Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback,  Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler) {
            super();
            this.tags = tags;
            this.latitude = latitude;
            this.longitude = longitude;
            this.useGeoData = useGeoData;
            this.callback = callback;
            this.progressCallback = progressCallback;
            this.errorHandler = errorHandler;

        }

        @Override
        protected void execute() {
            Paths.get(tmpDir).toFile().mkdirs();
            try {
                if(i==0){
                    SearchParameters searchParameters = new SearchParameters();
                    searchParameters.setTags(tags);
                    logger.debug("Using for search tags {}",tags);
                    if(useGeoData){
                        logger.debug("Using for search latitude {} and longitude {}",latitude,longitude);
                        searchParameters.setLatitude(String.valueOf(latitude));
                        searchParameters.setLongitude(String.valueOf(longitude));
                        searchParameters.setRadius(2);
                    }
                    searchParameters.setHasGeo(true);
                    logger.debug("Searching photos using the flickr api...");
                    list = flickr.getPhotosInterface().search(searchParameters, 250, 1);
                    logger.debug("Found {} photos.",list.size());
                }
                int nrOfDownloadedPhotos = 0;
                int nrOfPhotosToDownload = 10;
                if(list.size()-i<10){
                    nrOfPhotosToDownload = list.size()-i;
                }
                logger.debug("Start downloading {} photos", nrOfPhotosToDownload);
                for (;i<list.size();i++) {

                    Photo p = list.get(i);
                    if(nrOfDownloadedPhotos==10){
                        break;
                    }
                    if(!isRunning())
                        return;
                    logger.debug("Downloading photo nr {} ...",i+1);
                    String farmId = p.getFarm();
                    String serverId = p.getServer();
                    String id = p.getId();
                    String originalSecret = flickr.getPhotosInterface().getInfo(p.getId(),p.getSecret()).getOriginalSecret();
                    String format = p.getOriginalFormat();
                    String url = "https://farm"+farmId+".staticflickr.com/"+serverId+"/"+id+"_"+originalSecret+"_o."+format;
                    if(!originalSecret.isEmpty()){
                        BufferedInputStream in = null;
                        FileOutputStream fout = null;
                        try {
                            HttpURLConnection httpConnection = (HttpURLConnection) (new URL(url).openConnection());
                            long completeFileSize = httpConnection.getContentLength();
                            in = new BufferedInputStream((httpConnection.getInputStream()));
                            fout = new FileOutputStream(tmpDir+id+"."+format);

                            final byte data[] = new byte[64];
                            int count;
                            long downloadedFileSize = 0;
                            while ((count = in.read(data, 0, 64)) != -1) {
                                fout.write(data, 0, count);
                                downloadedFileSize = downloadedFileSize + count;
                                progressCallback.accept((nrOfDownloadedPhotos/(double)nrOfPhotosToDownload)+((((double)downloadedFileSize) / ((double)completeFileSize))/100.0));
                            }
                            at.ac.tuwien.qse.sepm.entities.Photo downloaded = new at.ac.tuwien.qse.sepm.entities.Photo();
                            GeoData geoData = flickr.getPhotosInterface().getGeoInterface().getLocation(id);
                            downloaded.setPath(tmpDir+id+"."+format);
                            downloaded.setLatitude(geoData.getLatitude());
                            downloaded.setLongitude(geoData.getLongitude());
                            downloaded.setRating(Rating.NONE);
                            logger.debug("Downloaded photo {}",downloaded);
                            callback.accept(downloaded);
                            nrOfDownloadedPhotos++;
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                            if (fout != null) {
                                fout.close();
                            }
                        }
                    }
                    else{
                        logger.debug("Can't get original secret for photo.");
                    }
                }
                progressCallback.accept(1.0);

            } catch (FlickrException e) {
                errorHandler.propagate(new ServiceException("Failed to download photo", e));
                return;
            } catch (IOException e) {
                errorHandler.propagate(new ServiceException("Failed to download photo", e));
                return;
            }
        }
    }
}
