package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsEmptyCollection.empty;

@UsingTable("Photo")
public class ClusterServiceTest extends ServiceTestBase {

    private static final Logger logger = LogManager.getLogger(ClusterServiceTest.class);

    @Autowired
    ClusterServiceImpl clusterService;
    @Autowired
    PhotoDAO photoDAO;
    @Autowired
    PlaceDAO placeDAO;

    private Journey inputJourneys[] = new Journey[]{
            new Journey(1, "Asien", LocalDateTime.of(2015, 3, 3, 0, 0, 0), LocalDateTime.of(2015, 3, 7, 0, 0, 0)),
            new Journey(2, "Amerika", LocalDateTime.of(2005, 11, 8, 0, 0, 0), LocalDateTime.of(2005, 11, 10, 0, 0, 0)),
            new Journey(3, "Leere Reise", LocalDateTime.of(2000, 3, 6, 0, 0, 0), LocalDateTime.of(2000, 3, 6, 0, 0, 0))
    };

    private Journey getViennaJourney() {
        return new Journey(4, "Vienna", LocalDateTime.of(2010, 8, 10, 0, 0, 0), LocalDateTime.of(2010, 8, 15, 0, 0, 0));
    }


    @WithData
    @Test
    public void testClusteringService() {
        try {
            clusterService.clusterJourney(inputJourneys[0]);
            clusterService.clusterJourney(inputJourneys[1]);
            clusterService.clusterJourney(inputJourneys[2]);
            logger.debug(placeDAO.readAll());

        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }

    /*

    @Override
    public Journey addJourney(Journey journey) throws ServiceException {
        try {
            return journeyDAO.create(journey);
        } catch (DAOException ex) {
            logger.error("Journey-creation for {} failed.", journey);
            throw new ServiceException("Creation of journey failed.", ex);
        } catch (ValidationException e) {
            throw new ServiceException("Failed to validate entity", e);
        }
    }
     */

    @Test(expected = ServiceException.class)
    public void test_create_place_malformed_throws1() throws ServiceException {
        clusterService.addPlace(null);
    }

    @Test(expected = ServiceException.class)
    public void test_create_place_malformed_throws2() throws ServiceException {
        clusterService.addPlace(new Place(1, null, "city", 0, 0, inputJourneys[0]));
    }

    @Test(expected = ServiceException.class)
    public void test_create_place_malformed_throws3() throws ServiceException {
        clusterService.addPlace(new Place(1, "country", null, 0, 0, inputJourneys[0]));
    }

    @Test(expected = ServiceException.class)
    public void test_create_place_malformed_throws4() throws ServiceException {
        clusterService.addPlace(new Place(1, "country", "city", 0, 0, null));
    }

    @Test
    public void test_create_place_persists() throws ServiceException {
        assertThat(clusterService.getAllPlaces(), empty());

        // create a place
        Place place = new Place(-1, "Amerika", "San Francisco", 0, 0, inputJourneys[1]);
        Place created = clusterService.addPlace(place);

        // check that place was added correctly
        place.setId(created.getId());
        assertThat(clusterService.getAllPlaces(), contains(place));
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws1() throws ServiceException {
        clusterService.addJourney(null);
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws2() throws ServiceException {
        Journey journey = getViennaJourney();
        journey.setName(null);
        clusterService.addJourney(journey);
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws3() throws ServiceException {
        Journey journey = getViennaJourney();
        journey.setStartDate(null);
        clusterService.addJourney(journey);
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws4() throws ServiceException {
        Journey journey = getViennaJourney();
        journey.setEndDate(null);
        clusterService.addJourney(journey);
    }

    @Test(expected = ServiceException.class)
    public void test_create_journey_malformed_throws5() throws ServiceException {
        Journey journey = getViennaJourney();
        LocalDateTime start = journey.getStartDate();
        journey.setStartDate(journey.getEndDate());
        journey.setEndDate(start);
        clusterService.addJourney(journey);
    }

    @Test
    public void test_create_journey_persists() throws ServiceException {
        assertThat(clusterService.getAllJourneys(), empty());

        // create a journey
        Journey journey = getViennaJourney();
        Journey created = clusterService.addJourney(journey);

        // check that journey was added correctly
        journey.setId(created.getId());
        assertThat(clusterService.getAllJourneys(), contains(journey));
    }
}
