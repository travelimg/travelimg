package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.service.PhotographerService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

public class PhotographerTest extends ServiceTestBase {

    @Autowired
    private PhotographerService photographerService;

    private Photographer createShelly() throws ServiceException {
        return photographerService.create(new Photographer(-1, "Shelly"));
    }

    @Test(expected = ServiceException.class)
    public void test_create_malformed_throws1() throws ServiceException {
        photographerService.create(new Photographer(1, null));
    }

    @Test(expected = ServiceException.class)
    public void test_create_malformed_throws2() throws ServiceException {
        photographerService.create(new Photographer(1, ""));
    }

    @Test
    public void test_create_persists() throws ServiceException {
        assertThat(photographerService.readAll(), empty());

        Photographer photographer = new Photographer(-1, "Dale");
        Photographer created = photographerService.create(photographer);

        // test that photographer was correctly persisted
        assertThat(photographer.getName(), equalTo(created.getName()));
        assertThat(photographerService.readAll(), contains(created));
    }

    @Test(expected = ServiceException.class)
    public void test_update_malformed_throws1() throws ServiceException {
        Photographer shelly = createShelly();
        shelly.setName(null);
        photographerService.update(shelly);
    }

    @Test(expected = ServiceException.class)
    public void test_update_malformed_throws2() throws ServiceException {
        Photographer shelly = createShelly();
        shelly.setName("");
        photographerService.update(shelly);
    }

    @Test(expected = ServiceException.class)
    public void test_update_malformed_throws3() throws ServiceException {
        Photographer shelly = createShelly();
        shelly.setId(null);
        photographerService.update(shelly);
    }

    @Test
    public void test_readall_returns_created() throws ServiceException {
        Photographer p1 = photographerService.create(new Photographer(-1, "Shelly"));
        Photographer p2 = photographerService.create(new Photographer(-1, "Denise"));
        Photographer p3 = photographerService.create(new Photographer(-1, "Big Ed"));

        assertThat(photographerService.readAll(), containsInAnyOrder(p1, p2, p3));
    }
}
