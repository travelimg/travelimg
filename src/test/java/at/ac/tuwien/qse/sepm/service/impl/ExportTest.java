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

import at.ac.tuwien.qse.sepm.dao.WithData;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.TestIOHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;

public class ExportTest extends ServiceTestBase {

    @Autowired
    private PhotoService photoService;
    @Autowired
    private ExportServiceImpl exportService;
    @Autowired
    private TestIOHandler ioHandler;

    private void awaitCompletion(Cancelable task) {
        int waited = 0;
        int interval = 100;
        int maxTimeout = 5000;
        try {
            while (waited < maxTimeout) {
                if (task.isFinished()) {
                    return;
                }
                Thread.sleep(interval);
                waited += interval;
            }
        } catch (InterruptedException ex) {
            // ignore
        }
    }

    @Before
    public void setUp() {
        exportService = Mockito.spy(exportService);
    }

    @Test
    public void test_dropboxAvailable_returnsPath() throws ServiceException {
        Mockito.when(exportService.getDropboxInfoPath()).thenReturn(getDropboxInfoPathVariant(0));

        assertThat(exportService.getDropboxFolder(), equalTo("/home/user/Dropbox"));
    }

    @Test
    public void test_dropboxAvailable_malformed_returnsNull() throws ServiceException {
        Mockito.when(exportService.getDropboxInfoPath()).thenReturn(getDropboxInfoPathVariant(1));

        assertThat(exportService.getDropboxFolder(), nullValue());
    }

    @Test
    public void test_noDropboxAvailable_returnsNull() throws ServiceException {
        Mockito.when(exportService.getDropboxInfoPath()).thenReturn(getDropboxInfoPathVariant(2));

        assertThat(exportService.getDropboxFolder(), nullValue());
    }

    @Test
    @WithData
    public void test_successfulExport() throws ServiceException {
       List<Photo> photos = photoService.getAllPhotos();

        // use an existing directory as export destination
        String destination = System.getProperty("java.io.tmpdir");

        TestPhotoAcceptor acceptor = new TestPhotoAcceptor();
        TestErrorHandler errorHandler = new TestErrorHandler();

        Cancelable task = exportService.exportPhotos(photos, destination, acceptor, errorHandler);

        awaitCompletion(task);

        List<Photo> exported = acceptor.getAccepted();

        // ensure no error occurred
        assertThat(errorHandler.exceptionOccured(), is(false));

        // ensure all photos were exported
        assertThat(photos.size(), is(exported.size()));

        // expected source paths is current photo path
        List<Path> expectedSourcePaths = photos.stream()
                .map(p -> Paths.get(p.getPath()))
                .collect(Collectors.toList());
        // expected dest path is destination/filename
        List<Path> expectedDestPaths = photos.stream()
                .map(p -> Paths.get(destination, p.getFile().getFileName().toString()))
                .collect(Collectors.toList());

        List<TestIOHandler.CopyOperation> copyOperations = ioHandler.copiedFiles;

        // ensure photos were copied to destination folder
        assertThat(photos.size(), is(copyOperations.size()));

        for (TestIOHandler.CopyOperation op : copyOperations) {
            // check that the copy operation is expected
            assertThat(expectedSourcePaths, hasItem(op.source));
            assertThat(expectedDestPaths, hasItem(op.dest));
        }
    }

    @Test
    @WithData
    public void test_exportToBogusDir_throws() throws ServiceException {
        List<Photo> photos = photoService.getAllPhotos();

        // some path which does not exist on the system
        String target = Paths.get(System.getProperty("java.io.tmpdir"), "doesnotexist").toString();

        TestPhotoAcceptor acceptor = new TestPhotoAcceptor();
        TestErrorHandler errorHandler = new TestErrorHandler();

        Cancelable task = exportService.exportPhotos(photos, target, acceptor, errorHandler);

        awaitCompletion(task);

        // assert that no photo was uploaded and an exception was thrown
        assertThat(acceptor.getAccepted(), empty());
        assertTrue(errorHandler.exceptionOccured());
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

        public boolean exceptionOccured() {
            return exceptions.size() > 0;
        }
    }

    private Path getDropboxInfoPathVariant(int variant) {
        switch (variant) {
            case 0: return getResourcePath("export/dropbox_info.json");
            case 1: return getResourcePath("export/dropbox_info_malformed.json");
            default: return Paths.get(System.getProperty("java.io.tmpdir"), "idonotexist");
        }
    }

    private Path getResourcePath(String path) {
        return  System.getProperty("os.name").contains("indow")
                ? Paths.get(ExportTest.class.getClassLoader().getResource(path).getPath().substring(1))
                : Paths.get(ExportTest.class.getClassLoader().getResource(path).getPath());
    }
}
