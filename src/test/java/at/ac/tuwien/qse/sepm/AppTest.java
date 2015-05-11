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
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Timestamp;
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
    private ClassPathXmlApplicationContext context;

    /**
     * Gets the exifservice from a Java bean.
     */
    @Before
    public void setUp() {
        logger.info("setUp");
        context = new ClassPathXmlApplicationContext("beans.xml");
        exifservice = (ExifService) context.getBean("exifServiceImpl");
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

    /**
     * Tests the importing of Exif-data and the changing of them.
     * @throws ServiceException
     */
    @Test
    public void testExif() throws ServiceException {
        Photo photo = new Photo(5, null, "C:\\Users\\David\\workspace\\qse-sepm-ss15-18\\src\\test\\resources\\test.jpg", 0);
        Photo photooriginal = new Photo(6, null, "C:\\Users\\David\\workspace\\qse-sepm-ss15-18\\src\\test\\resources\\testoriginal.jpg", 0);
        Exif exif = exifservice.importExif(photo);
        exifservice.importExif(photooriginal);
        exif.setExposure("1/1000");
        exif.setAltitude(2000);
        exif.setAperture(10);
        exif.setIso(28);
        exif.setDate(new Timestamp(new Date().getTime()));
        exif.setFocalLength(24);
        exif.setFlash(true);
        exif.setLongitude(12);
        exif.setLatitude(12);
        exif.setModel("nono");
        exif.setMake("yesyes");
        exifservice.changeExif(photo);
        logger.info(exif);
    }
}
