package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class SlideServiceTest extends ServiceTestBase {

    @Autowired
    private SlideServiceImpl slideService;

    @Test
    public void test_create() throws ServiceException {
        PhotoSlide p = new PhotoSlide(-1,1,1,"Test",null);


        slideService.create(p);
    }


    @Test
    public void test_update_should_persist() throws ServiceException {
        PhotoSlide p = new PhotoSlide(-1,1,1,"Test",null);


        slideService.create(p);
        p.setId(2);

        slideService.update(p);
    }
    @Test
    public void test_delete_should_persist() throws ServiceException {

        slideService.delete(new MapSlide(4,4,4,"test2",4.555,5.33,5));
    }
}
