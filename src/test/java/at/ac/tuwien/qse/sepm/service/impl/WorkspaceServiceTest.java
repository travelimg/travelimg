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

import at.ac.tuwien.qse.sepm.dao.repo.impl.PollingFileWatcher;
import at.ac.tuwien.qse.sepm.service.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertEquals;

public class WorkspaceServiceTest extends ServiceTestBase {

    @Autowired WorkspaceService workspaceService;
    @Autowired PollingFileWatcher watcher;

    private static final Path sourceDir1 = Paths
            .get(System.getProperty("os.name").contains("indow") ?
                    WorkspaceService.class.getClassLoader().getResource("db/testimages/directory1").getPath()
                            .substring(1) :
                    WorkspaceService.class.getClassLoader().getResource("db/testimages/directory1")
                            .getPath());

    private static final Path sourceDir2 = Paths
            .get(System.getProperty("os.name").contains("indow") ?
                    WorkspaceService.class.getClassLoader().getResource("db/testimages/directory2").getPath()
                            .substring(1) :
                    WorkspaceServiceTest.class.getClassLoader().getResource("db/testimages/directory2")
                            .getPath());

    @Before
    public void setUp() throws ServiceException{
        watcher.getExtensions().add("jpg");
        workspaceService.addDirectory(sourceDir1);
        watcher.refresh();
    }

    /**
     * Test whether the setup led to the wanted state, i.e. 3 photos should be watched.
     */
    @Test
    public void testSetup() throws ServiceException {
        int photoPathsSize = watcher.index().size();

        assertEquals(3, photoPathsSize);
    }

    @Test
    public void testAddDirectory_shouldPersist() throws ServiceException {
        int initiallyWatched = watcher.index().size();
        int watchedAfterAdd;
        int initialDirectories = workspaceService.getDirectories().size();
        int directoriesAfterAdd;

        workspaceService.addDirectory(sourceDir2);
        watcher.refresh();
        watchedAfterAdd = watcher.index().size();
        directoriesAfterAdd = workspaceService.getDirectories().size();

        assertEquals(initiallyWatched + 2, watchedAfterAdd);
        assertEquals(initialDirectories + 1, directoriesAfterAdd);
    }

    @Test
    public void testRemoveDirectory_shouldPersist() throws ServiceException {
        int initiallyWatched = watcher.index().size();
        int watchedAfterRemove;
        int initialDirectories = workspaceService.getDirectories().size();
        int directoriesAfterRemove;

        workspaceService.removeDirectory(sourceDir1);
        watcher.refresh();
        watchedAfterRemove = watcher.index().size();
        directoriesAfterRemove = workspaceService.getDirectories().size();

        assertEquals(initiallyWatched - 3, watchedAfterRemove);
        assertEquals(initialDirectories - 1, directoriesAfterRemove);
    }

    @Test (expected = ServiceException.class)
    public void testAddDirectoryWithNull_shouldThrowServiceException() throws ServiceException {
        workspaceService.addDirectory(null);
    }

    @Test (expected = ServiceException.class)
    public void testAddDirectoryWithNonExistingPath_shouldThrowServiceException()
            throws ServiceException {
        String nonExistingPathString = sourceDir1.toString() + "lalala";
        Path nonExistingPath = Paths.get(nonExistingPathString);

        workspaceService.addDirectory(nonExistingPath);
    }

    @Test (expected = ServiceException.class)
    public void testAddDirectoryWithInvalidPath_shouldThrowServiceException()
            throws ServiceException {
        Path invalidPath = Paths.get("lalalaThisIsInvalid");

        workspaceService.addDirectory(invalidPath);
    }

    @Test (expected = ServiceException.class)
    public void testAddDirectoryWithPathThatLeadsToAFile_shouldThrowServiceException()
            throws ServiceException {
        String filePathString = sourceDir1.toString() + "/6.jpg";
        Path filePath = Paths.get(filePathString);

        workspaceService.addDirectory(filePath);
    }

    @Test (expected = ServiceException.class)
    public void testRemoveDirectoryWithNull_shouldThrowServiceException() throws ServiceException {
        workspaceService.removeDirectory(null);
    }

    @Test
    public void testRemoveDirectoryWhichHasntBeenAddedBefore_shouldHaveNoEffect()
            throws ServiceException {
        //Existing directory which, however, has not been added before
        Path sourceDir3 = Paths.get(System.getProperty("os.name").contains("indow") ?
                        WorkspaceServiceTest.class.getClassLoader().
                                getResource("db/testimages/directory3").getPath()
                                .substring(1) :
                        WorkspaceServiceTest.class.getClassLoader().getResource("db/testimages/directory3")
                                .getPath());

        int initiallyWatched = watcher.index().size();
        int watchedAfterRemove;
        int initialDirectories = workspaceService.getDirectories().size();
        int noOfDirectoriesAfterCall = -1;

        workspaceService.removeDirectory(sourceDir3);
        watchedAfterRemove = watcher.index().size();
        noOfDirectoriesAfterCall = workspaceService.getDirectories().size();

        assertEquals(initiallyWatched, watchedAfterRemove);
        assertEquals(initialDirectories, noOfDirectoriesAfterCall);
    }
}
