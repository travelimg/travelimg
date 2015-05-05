package at.ac.tuwien.qse.sepm;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.gui.App;
import at.ac.tuwien.qse.sepm.service.ExifService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.impl.ExifServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private static final Logger logger = LogManager.getLogger(App.class);
    private ExifService exifservice;

    /**
     * Rigourous Test :-)
     */

    @Before
    public void setUp() {
        try {
            logger.info("setUp");
            exifservice = new ExifServiceImpl();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testApp()
    {
        logger.info("some simple test ;)");
        assertTrue( true );
    }

    @Test
    public void iterator_will_return_hello_world(){
        logger.info("testing the mockito framework");
        //arrange
        Iterator i=mock(Iterator.class);
        when(i.next()).thenReturn("Hello").thenReturn("World");
        //act
        String result=i.next()+" "+i.next();
        //assert
        assertEquals("Hello World", result);
    }

    @Test
    public void testExif() throws ServiceException {
        Photo photo = new Photo(5, null, "C:\\Users\\David\\workspace\\qse-sepm-ss15-18\\src\\test\\resources\\test.jpg", null, 0);
        Exif exif = exifservice.importExif(photo);
       logger.info(exif);
    }
}
