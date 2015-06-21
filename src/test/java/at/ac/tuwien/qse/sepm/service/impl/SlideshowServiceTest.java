package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import at.ac.tuwien.qse.sepm.service.SlideshowService;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    private Slideshow createAmerika() throws ServiceException {
        return slideshowService.create(new Slideshow(1,"Amerika",1.0,null));
    }


    @Test(expected = ServiceException.class)
    public void test_create_null_slideshow() throws ServiceException{
        Slideshow slideshow = new Slideshow();
        slideshowService.create(slideshow);

    }

    @Test
    public void test_create() throws ServiceException {


        Slideshow slideshow = new Slideshow();
        slideshow.setName("Testslideshow");
        slideshow.setDurationBetweenPhotos(5.0);
        slideshow.setSlides(null);


        assertEquals(slideshowService.getAllSlideshows().size(),0);

        slideshowService.create(slideshow);

        assertEquals(slideshowService.getAllSlideshows().size(),1);

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



}
