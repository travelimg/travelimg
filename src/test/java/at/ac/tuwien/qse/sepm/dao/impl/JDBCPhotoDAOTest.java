package at.ac.tuwien.qse.sepm.dao.impl;

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

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.util.TestIOHandler;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@UsingTable("Photo")
public class JDBCPhotoDAOTest extends AbstractJDBCDAOTest {

    private static final Photographer defaultPhotographer = new Photographer(1, "Test Photographer");
    private static final Place defaultPlace = new Place(1, "Unkown place", "Unknown place", 0.0, 0.0);
    private static final Journey defaultJourney = new Journey(1, "United States", LocalDateTime.of(2000, 9, 11, 0, 0, 0), LocalDateTime.of(2006, 9, 11, 0, 0, 0));

    @Autowired
    PhotoDAO photoDAO;

    private Photo expectedPhotos[] = new Photo[]{
            new Photo(1, Paths.get("1.jpg"), makeDefaultMeta(LocalDateTime.of(2015, 3, 6, 0, 0, 0), 41.5, 19.5)),
            new Photo(2, Paths.get("2.jpg"), makeDefaultMeta(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9)),
            new Photo(3, Paths.get("3.jpg"), makeDefaultMeta(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9)),
            new Photo(4, Paths.get("4.jpg"), makeDefaultMeta(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9)),
            new Photo(5, Paths.get("5.jpg"), makeDefaultMeta(LocalDateTime.of(2015, 3, 4, 0, 0, 0), 12.0, 12.0)),
            new Photo(6, Paths.get("4.jpg"), makeDefaultMeta(LocalDateTime.of(2005, 9, 11, 0, 0, 0), 39.7, -104.9)),
            new Photo(7, Paths.get("6.jpg"), makeDefaultMeta(
                    LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115)),
            new Photo(8, Paths.get("7.jpg"), makeDefaultMeta(
                    LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115)),
            new Photo(9, Paths.get("8.jpg"), makeDefaultMeta(
                    LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115)),
    };

    private Photo inputPhotos[] = new Photo[]{
            new Photo(7, Paths.get("6.jpg"), makeDefaultMeta(LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115)),
            new Photo(8, Paths.get("7.jpg"), makeDefaultMeta(LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115)),
            new Photo(9, Paths.get("8.jpg"), makeDefaultMeta(LocalDateTime.of(2015, 5, 17, 0, 0, 0), 41.5042718, 19.5180115)),
    };

    private static PhotoMetadata makeMeta(Photographer photographer, Rating rating, LocalDateTime datetime, double lat, double lon, Place place, Journey journey) {
        PhotoMetadata data = new PhotoMetadata();
        data.setJourney(journey);
        data.setPlace(place);
        data.setRating(rating);
        data.setDatetime(datetime);
        data.setLatitude(lat);
        data.setLongitude(lon);
        data.setPhotographer(photographer);
        return data;
    }

    private static PhotoMetadata makeDefaultMeta(LocalDateTime datetime, double lat, double lon) {
        return makeMeta(defaultPhotographer, Rating.NONE, datetime, lat, lon, defaultPlace, defaultJourney);
    }

    private Photo getInputPhoto(int seq) {
        return new Photo(inputPhotos[seq]);
    }

    private Photo getExpectedPhoto(int id) {
        return new Photo(expectedPhotos[id - 1]);
    }

    @Test(expected = IllegalArgumentException.class)
    @WithData
    public void testCreateWithNullThrows() throws DAOException {
        photoDAO.create(null);
    }

    @Test
    @WithData
    public void testCreateResultHasCorrectAttributes() throws DAOException {
        Photo photo = getInputPhoto(0);
        System.out.println(photo);
        Photo expected = getExpectedPhoto(photo.getId());

        Photo value = photoDAO.create(photo);

        expected.setId(value.getId());
        assertThat(expected, equalTo(value));
    }

    @Test
    @WithData
    public void testReadAllRecordsExist() throws DAOException {
        List<Photo> photos = photoDAO.readAll();

        for (Photo photo : photos) {
            Photo expected = getExpectedPhoto(photo.getId());
            assertThat(expected, equalTo(photo));
        }
    }

    @Test
    @WithData
    public void testCreateAddsEntry() throws DAOException {
        int initial = countRows();

        Photo photo = getInputPhoto(0);

        assertThat(photoDAO.readAll(), not(hasItem(photo)));

        int id = photoDAO.create(photo).getId();
        photo.setId(id);

        assertThat(countRows(), is(initial + 1));
        assertThat(photoDAO.readAll(), hasItem(photo));
    }

    @Test
    @WithData
    public void testReadAllReturnsCreated() throws DAOException {
        Photo photo = getInputPhoto(1);
        Photo expected = getExpectedPhoto(photo.getId());

        assertThat(photoDAO.readAll(), not(hasItem(expected)));

        int id = photoDAO.create(photo).getId();
        expected.setId(id);

        assertThat(photoDAO.readAll(), hasItem(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    @WithData
    public void testDeleteWithNullThrows() throws DAOException {
        photoDAO.delete(null);
    }

    @Test
    @WithData
    public void testDeleteWithNonexistingPhotoIsNop() throws DAOException {
        int initial = countRows();

        Photo photo = getExpectedPhoto(1);
        photo.setId(1337);

        photoDAO.delete(photo);

        // ensure that the number of photos did not change
        assertThat(countRows(), is(initial));
    }

    @Test
    @WithData
    public void testDeletePhotoRemovesEntry() throws DAOException {
        int initial = countRows();

        Photo photo = getExpectedPhoto(3);
        assertThat(photoDAO.readAll(), hasItem(photo));

        photoDAO.delete(photo);

        // ensure that entry was deleted
        assertThat(countRows(), is(initial - 1));
        assertThat(photoDAO.readAll(), not(hasItem(photo)));
    }

    @Test(expected = DAOException.class)
    @WithData
    public void testGetByIdNonexistingThrows() throws DAOException {
        photoDAO.getById(1000);
    }

    @Test
    @WithData
    public void testGetByIdExistingReturnsPhoto() throws DAOException {
        Photo expected = getExpectedPhoto(4);
        Photo actual = photoDAO.getById(expected.getId());

        assertThat(actual, equalTo(actual));
    }

    @Test(expected = DAOException.class)
    @WithData
    public void testGetByFileNonexistingThrows() throws DAOException {
        photoDAO.getByFile(Paths.get("/path/to/enlightenment.jpg"));
    }

    @Test(expected = IllegalArgumentException.class)
    @WithData
    public void testGetByFileNullFileThrows() throws DAOException {
        photoDAO.getByFile(null);
    }

    @Test
    @WithData
    public void testGetByFileReturnsPhoto() throws DAOException {
        Photo expected = getExpectedPhoto(3);

        // set prefix before and after fetching
        Path file = Paths.get(expected.getPath());
        Photo actual = photoDAO.getByFile(file);

        assertThat(actual, equalTo(expected));
    }
}
