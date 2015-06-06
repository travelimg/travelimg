package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.Journey;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-config.xml")
@Transactional(propagation = Propagation.REQUIRES_NEW)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@UsingTable("Photo")
public class ClusterServiceTest {

    private static final Logger logger = LogManager.getLogger(ClusterServiceTest.class);

    @Autowired
    ClusterServiceImpl clusterService;
    @Autowired
    PhotoDAO photoDAO;
    @Autowired
    PlaceDAO placeDAO;

    private Journey inputJourneys[] = new Journey[] {
            new Journey(1, "Asien", LocalDateTime.of(2015, 3, 3, 0, 0, 0), LocalDateTime.of(2015, 3, 7, 0, 0, 0)),
            new Journey(2, "Amerika", LocalDateTime.of(2005, 11, 8, 0, 0, 0), LocalDateTime.of(2005, 11, 10, 0, 0, 0)),
            new Journey(3, "Leere Reise", LocalDateTime.of(2000, 3, 6, 0, 0, 0), LocalDateTime.of(2000, 3, 6, 0, 0, 0))
    };


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
}
