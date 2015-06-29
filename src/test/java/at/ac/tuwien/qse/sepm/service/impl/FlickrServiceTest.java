package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

public class FlickrServiceTest extends ServiceTestBase {

    private static final String nonExistingId ="dieseidgibtsned";
    private static final GeoData geoData = new GeoData("16.35720443725586","48.210906982421875","1");
    private List<Photo> flickrPhotos = new ArrayList<>();

    @Autowired FlickrServiceImpl flickrService;


    @Before
    public void setUp() throws ServiceException, FlickrException {

        //spy the flickrService so that we can take control over single calls of methods without touching the rest
        flickrService = Mockito.spy(flickrService);

        //add some testdata
        int dummyId = 93838;
        int dummySecret = 45353454;
        int dummyOriSecret = 654567;
        for(int i=0; i<4; i++){
            Photo photo = new Photo();
            photo.setId(String.valueOf(dummyId+i));
            photo.setFarm("1");
            photo.setServer("459");
            photo.setOriginalFormat("jpg");
            photo.setSecret(String.valueOf(dummySecret+i));
            photo.setOriginalSecret(String.valueOf(dummyOriSecret+i));
            Mockito.doNothing().when(flickrService).downloadTempPhoto(eq(buildFlickrUrl(photo)), eq(photo.getId()+"_o"),
                    eq(photo.getOriginalFormat()));
            Mockito.doReturn(geoData).when(flickrService).getLocation(photo.getId());
            flickrPhotos.add(photo);
        }
    }

    @Test
    public void testNoPhotosToDownloadProgressShouldBe100(){
        List<Photo> photos = new ArrayList<>();
        TestPhotoConsumer testPhotoConsumer = new TestPhotoConsumer();
        TestProgressConsumer testProgressConsumer = new TestProgressConsumer();
        TestErrorHandler testErrorHandler = new TestErrorHandler();

        Cancelable downloadTask = flickrService.downloadPhotos(photos, testPhotoConsumer, testProgressConsumer,
                testErrorHandler);
        awaitCompletion(downloadTask);

        //make sure no exceptions occurred
        assertFalse(testErrorHandler.exceptionOccurred());

        //make sure that no photo was downloaded
        assertThat(testPhotoConsumer.getAccepted(),empty());

        //make sure accept method was called only once and the progress value was 1.0
        assertThat(testProgressConsumer.getAccepted().size(),is(1));
        assertThat(testProgressConsumer.getAccepted().get(0), is(1.0));
    }

    @Test
    public void testDownloadPhotosShouldBeOk(){
        TestPhotoConsumer testPhotoConsumer = new TestPhotoConsumer();
        TestProgressConsumer testProgressConsumer = new TestProgressConsumer();
        TestErrorHandler testErrorHandler = new TestErrorHandler();
        Cancelable downloadTask = flickrService.downloadPhotos(flickrPhotos, testPhotoConsumer, testProgressConsumer,
                testErrorHandler);

        awaitCompletion(downloadTask);

        //make sure no exceptions occurred
        assertFalse(testErrorHandler.exceptionOccurred());

        //make sure that all photos were downloaded
        assertThat(testPhotoConsumer.getAccepted().size(),is(4));
        assertEquals(testPhotoConsumer.getAccepted().get(0), flickrPhotos.get(0));
        assertEquals(testPhotoConsumer.getAccepted().get(1), flickrPhotos.get(1));
        assertEquals(testPhotoConsumer.getAccepted().get(2), flickrPhotos.get(2));
        assertEquals(testPhotoConsumer.getAccepted().get(3), flickrPhotos.get(3));

        //make sure accept method was called 5 times and the progress value at the end was 1.0
        assertThat(testProgressConsumer.getAccepted().size(),is(5));
        assertThat(testProgressConsumer.getAccepted().get(0),is(0.25));
        assertThat(testProgressConsumer.getAccepted().get(1),is(0.5));
        assertThat(testProgressConsumer.getAccepted().get(2),is(0.75));
        assertThat(testProgressConsumer.getAccepted().get(3),is(1.0));
        assertThat(testProgressConsumer.getAccepted().get(4), is(1.0));
    }

    @Test
    public void testDownloadPhotosThirdPhotoNoGeoData() throws ServiceException, FlickrException {
        Mockito.doThrow(ServiceException.class).when(flickrService).downloadTempPhoto(anyString(), eq(nonExistingId),
                anyString());
        TestPhotoConsumer testPhotoConsumer = new TestPhotoConsumer();
        TestProgressConsumer testProgressConsumer = new TestProgressConsumer();
        TestErrorHandler testErrorHandler = new TestErrorHandler();

        //suppose the third photo doesn't have geodata
        Mockito.doThrow(FlickrException.class).when(flickrService).getLocation(flickrPhotos.get(2).getId());

        Cancelable downloadTask = flickrService.downloadPhotos(flickrPhotos, testPhotoConsumer, testProgressConsumer,
                testErrorHandler);

        awaitCompletion(downloadTask);

        //make sure that the first two photos were downloaded
        assertThat(testPhotoConsumer.getAccepted().size(),is(2));
        assertEquals(testPhotoConsumer.getAccepted().get(0), flickrPhotos.get(0));
        assertEquals(testPhotoConsumer.getAccepted().get(1), flickrPhotos.get(1));

        //make sure exception occurred
        assertTrue(testErrorHandler.exceptionOccurred());

        //make sure accept method was called 4 times and the progress value at the end was 0.5
        assertThat(testProgressConsumer.getAccepted().size(),is(2));
        assertThat(testProgressConsumer.getAccepted().get(1), is(0.5));
    }

    @Test(expected = ServiceException.class)
    public void testDownloadTempPhotoIdIsNull() throws ServiceException {
        Photo validPhoto = flickrPhotos.get(0);
        flickrService.downloadTempPhoto(buildFlickrUrl(validPhoto), null, validPhoto.getOriginalFormat());
    }

    @Test(expected = ServiceException.class)
    public void testDownloadTempPhotoIdIsEmpty() throws ServiceException {
        Photo validPhoto = flickrPhotos.get(1);
        flickrService.downloadTempPhoto(buildFlickrUrl(validPhoto), "    ", validPhoto.getOriginalFormat());
    }

    @Test(expected = ServiceException.class)
    public void testDownloadTempPhotoFormatIsNull() throws ServiceException {
        Photo validPhoto = flickrPhotos.get(2);
        flickrService.downloadTempPhoto(buildFlickrUrl(validPhoto), validPhoto.getId(), null);
    }

    @Test(expected = ServiceException.class)
    public void testDownloadTempPhotoFormatIsEmpty() throws ServiceException {
        Photo validPhoto = flickrPhotos.get(3);
        flickrService.downloadTempPhoto(buildFlickrUrl(validPhoto), validPhoto.getId(), "    ");
    }

    private void awaitCompletion(Cancelable task) {
        int waited = 0;
        int interval = 100;
        int maxTimeout = 5000;
        try {
            while (waited < maxTimeout) {
                if (task.isFinished()) {
                    return;
                }
                Thread.sleep(interval);
                waited += interval;
            }
        } catch (InterruptedException ex) {
            // ignore
        }
    }

    private String buildFlickrUrl(Photo p){
        return "https://farm" + p.getFarm() + ".staticflickr.com/" + p.getServer()
                + "/" + p.getId() + "_" + p.getOriginalSecret() + "_o." + p
                .getOriginalFormat();
    }

    private class TestPhotoConsumer implements Consumer<Photo> {
        List<Photo> accepted = new ArrayList<>();

        @Override
        public void accept(Photo photo) {
            accepted.add(photo);
        }

        public List<Photo> getAccepted() {
            return accepted;
        }
    }

    private class TestProgressConsumer implements Consumer<Double> {
        List<Double> accepted = new ArrayList<>();

        @Override
        public void accept(Double progress) {
            accepted.add(progress);
        }

        public List<Double> getAccepted() {
            return accepted;
        }
    }

    private class TestErrorHandler implements ErrorHandler<ServiceException> {
        public List<ServiceException> exceptions = new ArrayList<>();

        @Override
        public void handle(ServiceException exception) {
            exceptions.add(exception);
        }

        public boolean exceptionOccurred() {
            return exceptions.size() > 0;
        }
    }

}
