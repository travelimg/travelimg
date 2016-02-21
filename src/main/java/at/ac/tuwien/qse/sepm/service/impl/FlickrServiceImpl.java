package at.ac.tuwien.qse.sepm.service.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedInputStream;
import java.io.File;
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
    private static final long oneMB = 1048576;
    private static final Logger logger = LogManager.getLogger();
    private static int nrOfPhotosToDownload = 9;
    private AsyncSearcher searcher;
    private Flickr flickr;
    private int i = 0;

    @Autowired
    private ExecutorService executor;

    public FlickrServiceImpl() {
        this.flickr = new Flickr(API_KEY, SECRET, new REST());
    }

    @Override
    public Cancelable searchPhotos(String tags[], double latitude, double longitude,
            boolean useGeoData, Consumer<Photo> callback,
            Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler){
        if (i == 0) {
            searcher = new AsyncSearcher(tags, latitude, longitude, useGeoData, callback, progressCallback, errorHandler);
        }
        executor.submit(searcher);
        return searcher;
    }

    @Override
    public Cancelable downloadPhotos(List<Photo> photos, Consumer<Photo> callback, Consumer<Double> progressCallback,
            ErrorHandler<ServiceException> errorHandler){
        AsyncDownloader downloader = new AsyncDownloader(photos, callback, progressCallback, errorHandler);
        executor.submit(downloader);
        return downloader;
    }

    @Override
    public void reset() {
        i = 0;
        nrOfPhotosToDownload = 9;
    }

    @Override
    public void close() {
        logger.debug("Flickr service closed.");
    }

    /**
     * Downloads a photo from a given url
     *
     * @param url                  the url of the photo
     * @param filename             the filename of the photo
     * @param format               the format of the photo
     * @throws ServiceException    if the photo can't be downloaded.
     */
    public void downloadTempPhoto(String url, String filename, String format) throws ServiceException {
        if(filename == null || filename.trim().isEmpty() || format == null || format.trim().isEmpty()){
            throw new ServiceException("Photo id or format invalid.");
        }
        HttpURLConnection httpConnection = null;
        try {
            httpConnection = (HttpURLConnection) (new URL(url).openConnection());
        } catch (IOException e) {
            logger.debug(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
        try(BufferedInputStream in = new BufferedInputStream((httpConnection.getInputStream()));
                FileOutputStream fout = new FileOutputStream(Paths.get(tmpDir, filename + "." + format).toString()))
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
            logger.debug("Downloaded photo {}", filename+"."+format);
            new File(Paths.get(tmpDir, filename + "." + format).toString()).deleteOnExit();

        } catch (IOException e) {
            logger.debug(e.getMessage());
            throw new ServiceException(e.getMessage(), e);
        }
    }

    /**
     * Get the geo data (latitude and longitude and the accuracy level) for a photo
     * @param id the photo id
     * @return Geo Data, if the photo has it.
     * @throws FlickrException if photo id is invalid, if photo has no geodata or if any other error has been reported in the response.
     */
    public GeoData getLocation(String id) throws FlickrException {
        return flickr.getPhotosInterface().getGeoInterface().getLocation(id);
    }

    /**
     * Get all info for the specified photo. The calling user must have permission to view the photo
     * @param photo the photo (containing id and secret)
     * @return the photo
     * @throws FlickrException
     */
    public Photo getInfoForFlickrPhoto(Photo photo) throws FlickrException {
        return flickr.getPhotosInterface().getInfo(photo.getId(), photo.getSecret());
    }

    /**
     * Search for 250 photos which match the given search parameters.
     * @param searchParameters The search parameters
     * @return A PhotoList
     * @throws FlickrException
     */
    public PhotoList searchPhotosAtFlickr(SearchParameters searchParameters) throws FlickrException {
        logger.debug("Searching photos using the flickr api...");
        return flickr.getPhotosInterface().search(searchParameters, 250, 1);
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
         * Downloads 9 photos( beginning at index i ) from flickr using tags and/or geo data.
         */
        @Override
        protected void execute() {
            Paths.get(tmpDir).toFile().mkdirs();
            try {
                if (i == 0) {
                    SearchParameters searchParameters = new SearchParameters();
                    searchParameters.setTags(tags);

                    logger.debug("Using for search tags {}", tags);
                    if (useGeoData) {
                        logger.debug("Using for search latitude {} and longitude {}", latitude, longitude);
                        searchParameters.setLatitude(String.valueOf(latitude));
                        searchParameters.setLongitude(String.valueOf(longitude));
                        searchParameters.setRadius(5);
                    }
                    searchParameters.setHasGeo(true);
                    list = searchPhotosAtFlickr(searchParameters);
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
                    Photo photoWithOriginalSecret = getInfoForFlickrPhoto(p);
                    p.setOriginalSecret(photoWithOriginalSecret.getOriginalSecret());
                    p.setTags(photoWithOriginalSecret.getTags());
                    if (!p.getOriginalSecret().isEmpty()) {
                        String mediumSizeUrl = "https://farm" + p.getFarm() + ".staticflickr.com/" + p.getServer() + "/" + p.getId() + "_" + p.getSecret() + "_z." + p.getOriginalFormat();
                        downloadTempPhoto(mediumSizeUrl, p.getId(), p.getOriginalFormat());
                        if(!isRunning()){
                            logger.debug("Download interrupted");
                            break;
                        }
                        logger.debug("Downloaded photo id={}, tags={}", p.getId(),p.getTags());
                        callback.accept(p);
                        nrOfDownloadedPhotos++;
                        progressCallback.accept((double) nrOfDownloadedPhotos / (double) nrOfPhotosToDownload);
                    } else {
                        logger.debug("Can't get original secret for photo id {}", p.getId());
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
        private Consumer<Photo> callback;
        private Consumer<Double> progressCallback;
        private ErrorHandler<ServiceException> errorHandler;

        public AsyncDownloader(List<Photo> photos, Consumer<Photo> callback, Consumer<Double> progressCallback, ErrorHandler<ServiceException> errorHandler){
            super();
            this.photos = photos;
            this.callback = callback;
            this.progressCallback = progressCallback;
            this.errorHandler = errorHandler;
        }

        @Override
        protected void execute() {
            try {
                for(int i=0; i<photos.size();i++) {
                    Photo p = photos.get(i);
                    String url = "https://farm" + p.getFarm() + ".staticflickr.com/" + p.getServer()
                            + "/" + p.getId() + "_" + p.getOriginalSecret() + "_o." + p
                            .getOriginalFormat();
                    downloadTempPhoto(url, p.getId() + "_o", p.getOriginalFormat());
                    p.setGeoData(getLocation(p.getId()));
                    logger.debug("Got geodata=[{},{}]", p.getGeoData().getLatitude(),p.getGeoData().getLongitude());

                    if (!isRunning()) {
                        logger.debug("Download interrupted.");
                        return;
                    }

                    callback.accept(p);
                    progressCallback.accept((double) (i + 1) / (double) photos.size());
                }
                progressCallback.accept(1.0);
            }
            catch (FlickrException | ServiceException e) {
                logger.error(e.getMessage());
                errorHandler.propagate(new ServiceException("Failed to download photo", e));
            }
        }
    }
}
