package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.service.FlickrService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.CancelableTask;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FlickrServiceImpl implements FlickrService {

    private static final String API_KEY ="206f4ffa559e5e48301f84f046bf208b";
    private static final String SECRET ="f58343bd30c130b6";
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private AsyncDownloader downloader;
    private Flickr flickr;
    private int i = 0;

    public FlickrServiceImpl(){
        this.flickr = new Flickr(API_KEY, SECRET, new REST());
    }

    @Override
    public Cancelable downloadPhotos(String tags[], double latitude, double longitude, boolean useTags, boolean useGeoData, Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback,  Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler) throws ServiceException{
        if(i==0){
            downloader = new AsyncDownloader(tags, latitude, longitude, useTags, useGeoData, callback, progressCallback, errorHandler);
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
        executorService.shutdown();
        //TODO delete photos from the tmp folder
    }

    private class AsyncDownloader extends CancelableTask {
        private String[] tags;
        private double latitude;
        private double longitude;
        private boolean useTags;
        private boolean useGeoData;
        private Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback;
        private Consumer<Double> progressCallback;
        private ErrorHandler<ServiceException> errorHandler;
        private PhotoList<Photo> list;

        public AsyncDownloader(String tags[], double latitude, double longitude, boolean useTags, boolean useGeoData, Consumer<at.ac.tuwien.qse.sepm.entities.Photo> callback,  Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler) {
            super();
            this.tags = tags;
            this.latitude = latitude;
            this.longitude = longitude;
            this.useTags = useTags;
            this.useGeoData = useGeoData;
            this.callback = callback;
            this.progressCallback = progressCallback;
            this.errorHandler = errorHandler;

        }

        @Override
        protected void execute() {
            try {


                if(i==0){
                    SearchParameters searchParameters = new SearchParameters();
                    if(useTags){
                        searchParameters.setTags(tags);
                    }
                    if(useGeoData){
                        searchParameters.setLatitude(String.valueOf(latitude));
                        searchParameters.setLongitude(String.valueOf(longitude));
                        searchParameters.setRadius(2);
                    }

                    searchParameters.setHasGeo(true);
                    list = flickr.getPhotosInterface().search(searchParameters, 250, 1);
                    //System.out.println(list.size());
                }
                int nrOfDownloadedPhotos = 0;
                int nrOfPhotosToDownload = 10;
                if(list.size()<10){
                    nrOfPhotosToDownload = list.size();
                }
                for (;i<list.size();i++) {

                    Photo p = list.get(i);
                    if(nrOfDownloadedPhotos==10){
                        break;
                    }
                    if(!getIsRunning())
                        return;

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
                            fout = new FileOutputStream("src/main/resources/tmp/"+id+"."+format);

                            final byte data[] = new byte[64];
                            int count;
                            long downloadedFileSize = 0;
                            while ((count = in.read(data, 0, 64)) != -1) {
                                fout.write(data, 0, count);
                                downloadedFileSize = downloadedFileSize + count;
                                progressCallback.accept((nrOfDownloadedPhotos/(double)nrOfPhotosToDownload)+((((double)downloadedFileSize) / ((double)completeFileSize))/100.0));
                            }
                            at.ac.tuwien.qse.sepm.entities.Photo downloaded = new at.ac.tuwien.qse.sepm.entities.Photo();
                            downloaded.setPath("src/main/resources/tmp/"+id+"."+format);
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
