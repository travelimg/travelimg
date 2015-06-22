package at.ac.tuwien.qse.sepm.service.impl;

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
        Slide s = new Slide();
        s.setId(-1);
        s.setCaption("Test");
        s.setOrder(1);
        s.setSlideshowId(1);

        slideService.create(s);
    }


    @Test
    public void test_update_should_persist() throws ServiceException {
        Slide s= new Slide();
        s.setId(2);

        slideService.update(s);
    }
    @Test
    public void test_delete_should_persist() throws ServiceException {
        Slide s= new Slide();
        s.setId(1);

        slideService.delete(s);
    }
}
