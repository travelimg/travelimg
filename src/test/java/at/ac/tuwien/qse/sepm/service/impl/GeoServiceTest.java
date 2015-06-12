package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeoServiceTest extends ServiceTestBase {

    private GeoServiceImpl geoService;

    @Before
    public void setUp() throws ServiceException {
        geoService = Mockito.mock(GeoServiceImpl.class);
        Mockito.when(geoService.getPlaceByGeoData(Mockito.anyDouble(),Mockito.anyDouble())).thenCallRealMethod();
    }

    @Test(expected = ServiceException.class)
    public void testMalformedJsonFileShouldThrow() throws ServiceException {
        switchArgumentOfReadUrlMethod("malformed.json");
        geoService.getPlaceByGeoData(43.325430, 15.028193);
    }

    @Test
    public void testStatusNotOkShouldReturnUnknownCityAndUnknownCountry() throws ServiceException {
        switchArgumentOfReadUrlMethod("not_ok.json");
        Place p = geoService.getPlaceByGeoData(43.325430, 15.028193);
        assertThat(p.getCity(), equalTo("Unknown city"));
        assertThat(p.getCountry(), equalTo("Unknown country"));
    }

    @Test
    public void testOnlyCountryAvailable() throws ServiceException {
        switchArgumentOfReadUrlMethod("ok_nocity_noadmarealvl1_country.json");
        Place p = geoService.getPlaceByGeoData(41.5042718,19.5180115);
        assertThat(p.getCity(), equalTo("Unknown city"));
        assertThat(p.getCountry(), equalTo("Albanien"));
    }

    @Test
    public void testStatusOkCityNotAvailableButAdministrativeAreaLevel1AndCountryAvailable() throws ServiceException {
        switchArgumentOfReadUrlMethod("ok_nocity_admarealvl1_country.json");
        Place p = geoService.getPlaceByGeoData(35.102218,24.959834);
        assertThat(p.getCity(), equalTo("Kreta"));
        assertThat(p.getCountry(), equalTo("Griechenland"));
    }

    @Test
    public void testStatusOkCityAndCountryAvailable() throws ServiceException {
        switchArgumentOfReadUrlMethod("ok_city_country.json");
        Place p = geoService.getPlaceByGeoData(48.224904,16.214717);
        assertThat(p.getCity(), equalTo("Wien"));
        assertThat(p.getCountry(), containsString("sterreich"));
    }

    /**
     * Set some fixed url as an argument for the readUrl() method inorder to make it read from self specified JSON files.
     * @param filename
     * @throws ServiceException
     */
    private void switchArgumentOfReadUrlMethod(String filename) throws ServiceException {
        File file = new File("target/test-classes/geocoding/"+filename);
        String url = "file:///"+file.getAbsolutePath().replace("\\", "/");
        Mockito.when(geoService.readUrl(Mockito.anyString())).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                // switch the argument
                invocation.getArguments()[0] = url;
                // then call the real method
                return (String) invocation.callRealMethod();
            }
        });

    }
}
