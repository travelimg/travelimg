package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.PhotoMetadata;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import com.flickr4java.flickr.photosets.Photoset;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.*;

public class SlideshowServiceTest extends ServiceTestBase {

    @Autowired
   private SlideshowService slideshowService;

    @Autowired
    private PhotoService photo;

    private Slideshow createAmerika() throws ServiceException {

        return slideshowService.create(new Slideshow(1,"Amerika",5.0));
    }

    @Test
    public void test_create() throws ServiceException {

        Slideshow slideshow = new Slideshow(6,"Testslideshow",5.0);



        assertThat(slideshowService.getAllSlideshows(), not(hasItem(slideshow)));

        slideshowService.create(slideshow);

        assertThat(slideshowService.getAllSlideshows(),hasItem(slideshow));

    }

    @Test(expected = ServiceException.class)
    public void test_create_null_slideshow() throws ServiceException{

        slideshowService.create(null);

    }

    @Test
    public void test_update_SlideshowName() throws ServiceException{
        Slideshow slideshow = createAmerika();


        slideshow.setName("Amerika2");
        slideshowService.update(slideshow);

        //List<Slideshow> slideshows = slideshowService.getAllSlideshows();
        assertThat(slideshowService.getAllSlideshows(), contains(slideshow));

    }

    @Test(expected = ServiceException.class)
    public void test_update_with_null_should_fail() throws ServiceException {
        Slideshow slideshow = createAmerika();
        slideshow.setName(null);
        slideshowService.update(slideshow);
    }

    @Test(expected = ServiceException.class)
    public void test_update_id_should_throws() throws ServiceException {
        Slideshow slideshow = createAmerika();
        slideshow.setId(null);
        slideshowService.update(slideshow);

    }

    @Test
    public void test_return_all_created_slideshows () throws ServiceException {
        Slideshow s1 = slideshowService
                .create(new Slideshow(-1, "Amerika", 5.0));
        Slideshow s2 = slideshowService
                .create(new Slideshow(-1, "Europa", 10.0));
        Slideshow s3 = slideshowService
                .create(new Slideshow(-1, "China", 1.0));

        assertThat(slideshowService.getAllSlideshows(), containsInAnyOrder(s1,s2,s3));
    }

    @Test
    public void test_return_all_slideshows_should_persist() throws ServiceException {

        assertEquals(slideshowService.getAllSlideshows().size(), 0);

    }
    @Test
    public void add_slides_to_slideshow_should_persist() throws ServiceException {

        Photo p = new Photo();
        List<Photo> photos = new ArrayList<>();
        photos.add(p);
        System.out.println(photos.size());
        Slideshow s1 = new Slideshow(-1,"TestSlideshow",5.0);
        slideshowService.create(s1);

       slideshowService.addPhotosToSlideshow(photos,s1);


    }

    @Test(expected = ServiceException.class)
    public void add_slides_to_slideshow_with_empty_list_should_fail() throws ServiceException {

        Slideshow s1 = new Slideshow(8,"Testlsideshow",5.0);

        List<Photo> photos = photo.getAllPhotos();

        slideshowService.addPhotosToSlideshow(photos,s1).size();

    }

    @Test
    public void delete_slideshow_should_persist() throws ServiceException {

        Slideshow s1 = new Slideshow(99,"SlideShow99",5.0);
        slideshowService.create(s1);

        slideshowService.delete(s1);
    }






}
