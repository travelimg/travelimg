package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.geo.GeoInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

public class FlickrServiceTest extends ServiceTestBase {

    private static final String tmpDir = "src/main/resources/tmp/";
    private static final String existingId = "18147940494";
    private static final String nonExistingId ="dieseidgibtsned";
    private static final GeoData geoData = new GeoData("16.35720443725586","48.210906982421875","1");
    @Autowired  private FlickrServiceImpl flickrService;

    @Before
    public void setUp() throws ServiceException, FlickrException {
        //Mock parts of the flickrapi
        Flickr flickr = Mockito.mock(Flickr.class);
        PhotosInterface photosInterface = Mockito.mock(PhotosInterface.class);
        GeoInterface geoInterface = Mockito.mock(GeoInterface.class);
        Mockito.when(flickr.getPhotosInterface()).thenReturn(photosInterface);
        Mockito.when(photosInterface.getGeoInterface()).thenReturn(geoInterface);
        Mockito.when(geoInterface.getLocation(existingId)).thenReturn(geoData);
        Mockito.when(geoInterface.getLocation(nonExistingId)).thenThrow(new FlickrException(""));
        Mockito.when(geoInterface.getLocation(null)).thenThrow(new FlickrException(""));
    }

    @Test(expected = ServiceException.class)
    public void testCreatePhotoWithGeoDataIdIsNull() throws ServiceException {
        flickrService.createPhotoWithGeoData(null, "jpg");
    }

    @Test(expected = ServiceException.class)
    public void testCreatePhotoWithGeoDataFormatIsNull() throws ServiceException {
        flickrService.createPhotoWithGeoData(existingId, null);
    }

    @Test(expected = ServiceException.class)
    public void testCreatePhotoWithGeoDataIdNotFoundAtFlickr() throws ServiceException {
        flickrService.createPhotoWithGeoData(nonExistingId, "jpg");
    }

    @Test
    public void testCreatePhotoWithGeoDataSuccess() throws ServiceException {
        Photo p = flickrService.createPhotoWithGeoData(existingId, "jpg");
        assertThat(p.getData().getPhotographer().getId(), is(1));
        assertThat(p.getFile(), is(Paths.get(tmpDir + existingId + ".jpg")));
        assertThat(p.getRating(), is(Rating.NONE));
        assertNull(p.getDatetime());
        assertThat(p.getLatitude(), not(0.0));
        assertThat(p.getLongitude(), not(0.0));
        assertThat(p.getPlace().getId(), is(1));
    }

}
