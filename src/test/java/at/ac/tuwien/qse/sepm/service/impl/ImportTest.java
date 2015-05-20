package at.ac.tuwien.qse.sepm.service.impl;


import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.PhotographerDAO;
import at.ac.tuwien.qse.sepm.dao.WithData;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.gui.ServiceExceptionHandler;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.CancelableTask;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.TestIOHandler;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-config.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ImportTest {

    private static final Photographer defaultPhotographer = new Photographer(1, "Test Photographer");

    private static final String dataDir = Paths.get(System.getProperty("java.io.tmpdir"), "travelimg").toString();
    private static final String sourceDir = Paths.get(System.getProperty( "os.name" ).contains( "indow" ) ?
            ImportTest.class.getClassLoader().getResource("db/testimages").getPath().substring(1) :
            ImportTest.class.getClassLoader().getResource("db/testimages").getPath()).toString();


    private List<Photo> expectedPhotos = new ArrayList<Photo>() {{
        add(new Photo(6, defaultPhotographer, dataDir + "/2005/09/11/6.jpg", Rating.NONE, LocalDateTime.of(2005, 9, 11, 15, 43, 55), 39.73934166666667, -104.99156111111111));
        add(new Photo(7, defaultPhotographer, dataDir + "/2005/09/11/7.jpg", Rating.NONE, LocalDateTime.of(2005, 9, 11, 15, 44, 8), 39.739336111111115, -104.9916361111111));
        add(new Photo(8, defaultPhotographer, dataDir + "/2005/09/11/8.jpg", Rating.NONE, LocalDateTime.of(2005, 9, 11, 15, 48, 7), 39.73994444444445, -104.98952777777778));
    }};

    List<Photo> inputPhotos = new ArrayList<Photo>() {{
        add(new Photo(6, defaultPhotographer, sourceDir + "/6.jpg", Rating.NONE, null, 0, 0));
        add(new Photo(7, defaultPhotographer, sourceDir + "/7.jpg", Rating.NONE, null, 0, 0));
        add(new Photo(8, defaultPhotographer, sourceDir + "/8.jpg", Rating.NONE, null, 0, 0));
    }};

    @Autowired private ImportService importService;
    @Autowired private TestIOHandler ioHandler;

    public ImportTest() {
        for(Photo photo : expectedPhotos) {
            String path = photo.getPath().replace("/", File.separator);
            photo.setPath(path);
        }

        for(Photo photo : inputPhotos) {
            String path = photo.getPath().replace("/", File.separator);
            photo.setPath(path);
        }
    }

    public List<Photo> duplicateList(List<Photo> photos) {
        return photos
                .stream()
                .map(p -> new Photo(p))
                .collect(Collectors.toList());
    }

    private void awaitCompletion(Cancelable task) {
        int waited = 0;
        int interval = 100;
        int maxTimeout = 5000;
        try {
            while(waited < maxTimeout) {
                if(task.isFinished()) {
                    return;
                }
                Thread.sleep(interval);
                waited += interval;
            }
        } catch (InterruptedException ex) {
            // ignore
        }
    }

    @Test
    @WithData
    public void testImport() throws ServiceException {
        List<Photo> toBeImported = duplicateList(inputPhotos);

        TestPhotoAcceptor acceptor = new TestPhotoAcceptor();
        TestErrorHandler errorHandler = new TestErrorHandler();
        Cancelable task = importService.importPhotos(toBeImported, acceptor, errorHandler);

        awaitCompletion(task);

        // ensure that import worked without errors
        assertThat(errorHandler.exceptionOccured(), is(false));
        assertThat(acceptor.getAccepted().size(), is(3));

        // test that the photos have been imported correctly
        assertThat(acceptor.getAccepted(), hasItem(expectedPhotos.get(0)));
        assertThat(acceptor.getAccepted(), hasItem(expectedPhotos.get(1)));
        assertThat(acceptor.getAccepted(), hasItem(expectedPhotos.get(2)));

        // test that the files have been copied correctly
        List<Pair<Path, Path>> copiedFiles = ioHandler.copiedFiles;

        List<Path> expectedSourcePaths = inputPhotos.stream()
                .map(p -> Paths.get(p.getPath()))
                .collect(Collectors.toList());
        List<Path> expectedDestPaths = expectedPhotos.stream()
                .map(p -> Paths.get(p.getPath()))
                .collect(Collectors.toList());

        for(Pair<Path, Path> copyOp : copiedFiles) {
            Path source = copyOp.getKey();
            Path dest = copyOp.getValue();

            // check that the copy operation is expected
            assertThat(expectedSourcePaths, hasItem(source));
            assertThat(expectedDestPaths, hasItem(dest));
        }
    }

    private class TestPhotoAcceptor implements Consumer<Photo> {
        List<Photo> accepted = new ArrayList<>();

        @Override
        public void accept(Photo photo) {
            accepted.add(photo);
        }

        public List<Photo> getAccepted() {
            return accepted;
        }
    }

    private class TestErrorHandler implements ErrorHandler<ServiceException> {
        public List<ServiceException> exceptions = new ArrayList<>();

        @Override
        public void handle(ServiceException exception) {
            exceptions.add(exception);
        }

        public boolean exceptionOccured() { return exceptions.size() > 0; }
    }
}
