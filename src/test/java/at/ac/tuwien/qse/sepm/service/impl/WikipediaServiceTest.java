package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import at.ac.tuwien.qse.sepm.service.WikipediaService;
import com.hp.hpl.jena.query.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.List;

public class WikipediaServiceTest extends ServiceTestBase {

    private static final Logger LOGGER = LogManager.getLogger(WikipediaServiceTest.class);

    @Autowired
    private WikipediaService wikipediaService;

    /**
     * Call 'getWikiPlaceInfo' with a custom Place, i.e. the city Gleisdorf in Austria.
     * Test general functionality of the method.
     */
    @Test
    public void testGetWikiPlaceInfo_ForGleisdorfAustria() {
        Place gleisdorf = new Place(null, "Gleisdorf", "Österreich", 0.0, 0.0,
                new Journey(null,null,null,null));
        WikiPlaceInfo gleisdorfInfo = null;

        try {
            gleisdorfInfo = wikipediaService.getWikiPlaceInfo(gleisdorf);
        } catch (ServiceException ex) {
            LOGGER.error("ServiceException in 'getWikiPlaceInfo(gleisdorf)' was thrown.");
            ex.printStackTrace();
        }

        assertNotNull(gleisdorfInfo);

        assertNotNull(gleisdorfInfo.getPlaceName());
        assertNotNull(gleisdorfInfo.getCountryName());
        assertNotNull(gleisdorfInfo.getDescription());
        assertNotNull(gleisdorfInfo.getArea());
        assertNotNull(gleisdorfInfo.getCurrency());
        assertNotNull(gleisdorfInfo.getElevation());
        assertNotNull(gleisdorfInfo.getLanguage());
        assertNotNull(gleisdorfInfo.getPopulation());
        assertNotNull(gleisdorfInfo.getUtcOffset());


        assertEquals(gleisdorfInfo.getPlaceName(), "Gleisdorf");
        assertEquals(gleisdorfInfo.getCountryName(), "Österreich");
        assertTrue(Math.abs(gleisdorfInfo.getArea() - 4.76e6) <= 0e-6);
        assertEquals(gleisdorfInfo.getCurrency(), "Euro");
        assertTrue(Math.abs(gleisdorfInfo.getElevation() - 365) <= 0e-6);
        assertEquals(gleisdorfInfo.getLanguage(), "Österreichisches Deutsch");
        assertEquals(gleisdorfInfo.getPopulation().intValue(), 5706);
        assertTrue(gleisdorfInfo.getUtcOffset().contains("+01"));
    }

    @Test(expected = ServiceException.class)
    public void getWikiPlaceInfo_withNullParameter() throws ServiceException {
        wikipediaService.getWikiPlaceInfo(null);
    }

    @Test(expected = ServiceException.class)
    public void getWikiPlaceInfo_withInvalidParameter() throws ServiceException {
        Place invalidPlace = new Place(null, null, null, 0.0, 0.0, null);
        wikipediaService.getWikiPlaceInfo(invalidPlace);
    }
}
