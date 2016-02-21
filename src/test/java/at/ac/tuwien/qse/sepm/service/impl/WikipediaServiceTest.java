package at.ac.tuwien.qse.sepm.service.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import at.ac.tuwien.qse.sepm.service.WikipediaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class WikipediaServiceTest extends ServiceTestBase {

    private static final Logger LOGGER = LogManager.getLogger(WikipediaServiceTest.class);

    @Autowired private WikipediaService wikipediaService;

    /**
     * Call 'getWikiPlaceInfo' with a custom Place, i.e. the city Gleisdorf in Austria.
     * Test general functionality of the method.
     */
    @Test public void testGetWikiPlaceInfo_ForGleisdorfAustria() throws ServiceException {
        Place gleisdorf = new Place(null, "Gleisdorf", "Österreich", 0.0, 0.0);
        WikiPlaceInfo gleisdorfInfo = null;

            gleisdorfInfo = wikipediaService.getWikiPlaceInfo(gleisdorf);

        assertNotNull(gleisdorfInfo);
        assertEquals(gleisdorfInfo, gleisdorf.getWikiPlaceInfo());

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
        assertTrue(Math.abs(gleisdorfInfo.getArea() - 4.76) <= 0e-6);
        assertEquals(gleisdorfInfo.getCurrency(), "Euro");
        assertTrue(Math.abs(gleisdorfInfo.getElevation() - 365) <= 0e-6);
        assertEquals(gleisdorfInfo.getLanguage(), "Österreichisches Deutsch");
        assertEquals(gleisdorfInfo.getPopulation().intValue(), 5706);
        assertEquals(gleisdorfInfo.getUtcOffset(), "GMT+01");
    }

    /**
     * Call 'getWikiPlaceInfo' with a custom Place, i.e. the city Boston in the US.
     * Test whether result is returned even if no country can be found.
     * Test whether UtcOffset is initialized if there is an entry for city, but not for country.
     */
    @Test public void testGetWikiPlaceInfo_ForBostonUSA() throws ServiceException {
        Place boston = new Place(null, "Boston", "USA", 0.0, 0.0);
        WikiPlaceInfo bostonInfo = null;

        bostonInfo = wikipediaService.getWikiPlaceInfo(boston);

        assertNotNull(bostonInfo);

        assertNotNull(bostonInfo.getUtcOffset());
        assertEquals(bostonInfo, boston.getWikiPlaceInfo());
        assertEquals("GMT-5", bostonInfo.getUtcOffset());
    }

    /**
     * Call 'getWikiPlaceInfo' with a custom Place, i.e. the city Brighton in the UK.
     * Test whether multiple languages are properly saved in <tt>wikiPlaceInfo.language</tt>
     */
    @Test public void testGetWikiPlaceInfo_ForBrightonUK() throws ServiceException {
        Place brighton = new Place(null, "Brighton", "UK", 0.0, 0.0);
        WikiPlaceInfo brightonInfo = null;

        try {
            brightonInfo = wikipediaService.getWikiPlaceInfo(brighton);
        } catch (ServiceException ex) {
            LOGGER.error("ServiceException in 'getWikiPlaceInfo(brighton)' was thrown.");
            ex.printStackTrace();
        }

        assertNotNull(brightonInfo);
        assertEquals(brightonInfo, brighton.getWikiPlaceInfo());
        assertNotNull(brightonInfo.getLanguage());

        String language = brightonInfo.getLanguage();

        assertTrue(language.contains("Scots"));
        assertTrue(language.contains("Englische Sprache"));
        assertTrue(language.contains("Kornische Sprache"));

        System.out.println(language);

        language = language.replaceFirst("Scots", "");

        System.out.println(language);

        assertFalse(language.contains("Scots"));
    }

    @Test(expected = ServiceException.class) public void testGetWikiPlaceInfo_withNullParameter() throws ServiceException {
        wikipediaService.getWikiPlaceInfo(null);
    }

    @Test(expected = ServiceException.class) public void testGetWikiPlaceInfo_withInvalidParameter()
            throws ServiceException {
        Place invalidPlace = new Place(null, null, null, 0.0, 0.0);
        wikipediaService.getWikiPlaceInfo(invalidPlace);
    }

    /**
     * Assert that a default "No data available"-WikiPlaceInfo is returned if no info can
     * be retrieved for the place.
     */
    @Test
    public void testGetWikiPlaceInfo_withNonExistingCityName() throws ServiceException {
        Place noRealPlace = new Place(null, "asdfagasdd", "UK", 0.0, 0.0);
        WikiPlaceInfo noRealPlaceInfo = null;

        noRealPlaceInfo = wikipediaService.getWikiPlaceInfo(noRealPlace);

        assertNotNull(noRealPlaceInfo);
        assertEquals(noRealPlaceInfo, noRealPlace.getWikiPlaceInfo());
        assertEquals("asdfagasdd", noRealPlaceInfo.getPlaceName());
        assertEquals("Keine Information verfügbar.", noRealPlaceInfo.getDescription());
    }


}
