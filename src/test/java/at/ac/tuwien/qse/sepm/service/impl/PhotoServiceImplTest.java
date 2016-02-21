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

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.WithData;
import at.ac.tuwien.qse.sepm.dao.repo.AsyncPhotoRepository;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.service.ImportService;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import at.ac.tuwien.qse.sepm.util.Cancelable;
import at.ac.tuwien.qse.sepm.util.ErrorHandler;
import at.ac.tuwien.qse.sepm.util.TestIOHandler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class PhotoServiceImplTest extends ServiceTestBase {

    @Autowired
    private PhotoService photoService;
    @Autowired
    private AsyncPhotoRepository photoRepository;

    private Photographer defaultPhotographer = new Photographer(1, "Test Photographer");
    private static final Place defaultPlace = new Place(1, "Unkown place", "Unknown place", 0.0, 0.0);

    private List<Photo> photos = Arrays.asList(
            new Photo(1, Paths.get("1.jpg"),
                    new PhotoMetadata(LocalDateTime.of(2015, 3, 6, 0, 0, 0), 41.5, 19.5, Rating.NONE, defaultPhotographer, defaultPlace, null)),
            new Photo(2, Paths.get("2.jpg"),
                    new PhotoMetadata(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9, Rating.NONE, defaultPhotographer, defaultPlace, null)),
            new Photo(3, Paths.get("3.jpg"),
                    new PhotoMetadata(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9, Rating.NONE, defaultPhotographer, defaultPlace, null)),
            new Photo(4, Paths.get("4.jpg"),
                new PhotoMetadata(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9, Rating.NONE, defaultPhotographer, defaultPlace, null)),
            new Photo(5, Paths.get("5.jpg"),
                new PhotoMetadata(LocalDateTime.of(2015, 3, 4, 0, 0, 0), 12.0, 12.0, Rating.NONE, defaultPhotographer, defaultPlace, null))
    );
    
    @Test
    @WithData
    public void test_deletePhotos_persists() throws ServiceException, DAOException {
        assertThat(photoService.getAllPhotos(), containsInAnyOrder(photos.toArray()));
        assertThat(photoRepository.readAll(), containsInAnyOrder(photos.toArray()));

        photoService.deletePhotos(photos.subList(0, 2));

        assertThat(photoService.getAllPhotos(), containsInAnyOrder(photos.subList(2, 5).toArray()));
        assertThat(photoService.getAllPhotos(), not(contains(photos.get(0))));
        assertThat(photoService.getAllPhotos(), not(contains(photos.get(1))));

        assertThat(photoRepository.readAll(), containsInAnyOrder(photos.subList(2, 5).toArray()));
        assertThat(photoRepository.readAll(), not(contains(photos.get(0))));
        assertThat(photoRepository.readAll(), not(contains(photos.get(1))));
    }
}
