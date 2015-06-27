package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SlideServiceTest extends ServiceTestBase {

    @Autowired
    private SlideServiceImpl slideService;
    @Autowired
    private SlideshowServiceImpl slideshowService;
    @Test
    public void test_create() throws ServiceException {
        Slideshow s1 =new Slideshow(-1,"test",5.0);
        slideshowService.create(s1);

        slideService.create(new MapSlide(33,slideshowService.getAllSlideshows().get(0).getId(),45,"test",5.77,4.234,6));



    }


    @Test
    public void test_update_should_persist() throws ServiceException {
        Slideshow s1 =new Slideshow(-1,"test",5.0);
        slideshowService.create(s1);
        MapSlide m = new MapSlide(33,slideshowService.getAllSlideshows().get(0).getId(),45,"test3",5.77,4.234,6);


        slideService.create(m);
        m.setCaption("hallo");

        slideService.update(m);
    }
    @Test
    public void test_delete_should_persist() throws ServiceException {

        slideService.delete(new MapSlide(4,4,4,"test2",4.555,5.33,5));
    }
}
