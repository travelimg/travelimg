package at.ac.tuwien.qse.sepm.service.impl;


import at.ac.tuwien.qse.sepm.dao.WithData;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.DropboxService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.TestIOHandler;
import javafx.util.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-config.xml")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class DropboxExportTest {

    @Autowired
    private PhotoService photoService;
    @Autowired
    private DropboxService dropboxService;
    @Autowired
    private TestIOHandler ioHandler;

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
    public void testOutsideOfDropboxRootThrows() throws ServiceException {
        List<Photo> photos = photoService.getAllPhotos();

        // some path which exists on the system, but is definitely not inside the dropbox folder
        String target = Paths.get(System.getProperty("java.io.tmpdir"), "travelimg").toString();

        TestPhotoAcceptor acceptor = new TestPhotoAcceptor();
        TestErrorHandler errorHandler = new TestErrorHandler();

        Cancelable task = dropboxService.uploadPhotos(photos, target, acceptor, errorHandler);

        awaitCompletion(task);

        // assert that no photo was uploaded and an exception was thrown
        assertThat(acceptor.getAccepted(), empty());
        assertTrue(errorHandler.exceptionOccured());
    }

    @Test
    @WithData
    public void testExport() throws ServiceException {
        List<Photo> photos = photoService.getAllPhotos();

        String root = dropboxService.getDropboxFolder();
        Path target = Paths.get(root, "images", "holiday", "beach");

        TestPhotoAcceptor acceptor = new TestPhotoAcceptor();
        TestErrorHandler errorHandler = new TestErrorHandler();

        Cancelable task = dropboxService.uploadPhotos(photos, target.toString(), acceptor, errorHandler);

        awaitCompletion(task);

        List<Photo> uploaded = acceptor.getAccepted();

        // ensure no error occurred
        assertThat(errorHandler.exceptionOccured(), is(false));

        // ensure all photos were uploaded
        assertEquals(photos.size(), uploaded.size());

        // expected source paths is current path in travelimg directory
        List<Path> expectedSourcePaths = photos.stream()
                .map(p -> Paths.get(p.getPath()))
                .collect(Collectors.toList());
        // expected dest path is selected dropbox directory/filename
        List<Path> expectedDestPaths = photos.stream()
                .map(p -> Paths.get(p.getPath()).getFileName().toString())
                .map(filename -> Paths.get(target.toString(), filename))
                .collect(Collectors.toList());

        List<Pair<Path, Path>> copyOperations = ioHandler.copiedFiles;

        // ensure photos were copied to dropbox folder
        assertEquals(photos.size(), copyOperations.size());
        
        for(Pair<Path, Path> copyOp : copyOperations) {
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
