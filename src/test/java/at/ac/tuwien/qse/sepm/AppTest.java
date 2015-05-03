package at.ac.tuwien.qse.sepm;

import at.ac.tuwien.qse.sepm.gui.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Rigourous Test :-)
     */

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
}
