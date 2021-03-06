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
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import at.ac.tuwien.qse.sepm.service.TagService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class TagTest extends ServiceTestBase {

    @Autowired private TagService tagService;
    @Autowired private PhotoService photoService;

    private Photo getPhoto(int index) throws ServiceException {
        return photoService.getAllPhotos().get(index);
    }

    private List<Tag> getTags(Photo photo) throws ServiceException {
        return tagService.getTagsForPhoto(photo);
    }

    @SafeVarargs
    private final <T> List<T> toList(T... elements) {
        return Arrays.asList(elements);
    }


    @Test
    public void test_create() throws ServiceException {
        Tag tag = new Tag(1, "Shiny");

        // does not exist
        assertThat(tagService.getAllTags(), not(hasItem(tag)));

        tag = tagService.create(tag);

        // now it does
        assertThat(tagService.getAllTags(), hasItem(tag));
    }

    @Test(expected = ServiceException.class)
    public void test_create_null_tag_throws() throws ServiceException {
        tagService.create(new Tag(1, null));
    }

    @Test(expected = ServiceException.class)
    public void test_create_empty_tag_throws() throws ServiceException {
        tagService.create(new Tag(1, ""));
    }

    @Test(expected = ServiceException.class)
    public void test_delete_null_id_throws() throws ServiceException {
        tagService.delete(new Tag(null, "Shiny"));
    }

    @Test
    @WithData
    public void test_delete() throws ServiceException {
        int initialSize = tagService.getAllTags().size();
        assertThat(tagService.getAllTags().size(), is(3));

        // delete all tags one after the other
        int deleted = 0;

        for (Tag tag : tagService.getAllTags()) {
            tagService.delete(tag);
            deleted++;

            assertThat(tagService.getAllTags(), not(hasItem(tag)));
            assertThat(tagService.getAllTags().size(), is(initialSize - deleted));
        }

        assertThat(tagService.getAllTags().size(), is(0));
    }

    @Test
    public void test_get_all_tags_empty() throws ServiceException {
        assertThat(tagService.getAllTags(), empty());
    }

    @Test
    @WithData
    public void test_get_all_tags() throws ServiceException {
        assertThat(tagService.getAllTags(), containsInAnyOrder(new Tag(1, "Person"),
                new Tag(2, "Essen"), new Tag(3, "Natur")));
    }

    @Test(expected = ServiceException.class)
    @WithData
    public void test_add_non_existing_tag_to_photo_throws() throws ServiceException {
        Photo photo = getPhoto(0);
        Tag nonExisting = new Tag(42, "Ups");

        tagService.addTagToPhotos(toList(photo), nonExisting);
    }

    @Test(expected = ServiceException.class)
    @WithData
    public void test_add_tag_to_non_existing_photo_throws() throws ServiceException {
        Photo photo = getPhoto(0);
        photo.setId(42);
        Tag tag = tagService.getAllTags().get(0);

        tagService.addTagToPhotos(toList(photo), tag);
    }

    @Test
    @WithData
    public void test_add_tag_to_photos() throws ServiceException {
        Photo p0 = getPhoto(0);
        Photo p1 = getPhoto(1);

        Tag t0 = tagService.getAllTags().get(0);
        Tag t1 = tagService.getAllTags().get(1);

        // assert that they don't have any tags attached
        assertThat(getTags(p0), empty());
        assertThat(getTags(p1), empty());

        tagService.addTagToPhotos(toList(p0), t0);
        tagService.addTagToPhotos(toList(p1), t1);

        // assert that tags are now present
        p0 = getPhoto(0);
        p1 = getPhoto(1);

        assertThat(getTags(p0), equalTo(toList(t0)));
        assertThat(getTags(p1), equalTo(toList(t1)));
    }

    @Test
    @WithData
    public void test_add_tag_is_idempotent() throws ServiceException {
        Photo p0 = getPhoto(0);
        Tag t0 = tagService.getAllTags().get(0);

        // assert that no tags are attached
        assertThat(getTags(p0), empty());

        // attach tag
        tagService.addTagToPhotos(toList(p0), t0);

        // assert that tag is set
        p0 = getPhoto(0);
        assertThat(getTags(p0), equalTo(toList(t0)));
        assertThat(getTags(p0).size(), is(1));

        // set same tag again
        tagService.addTagToPhotos(toList(p0), t0);

        // assert that nothing changed
        p0 = getPhoto(0);
        assertThat(getTags(p0), equalTo(toList(t0)));
        assertThat(getTags(p0).size(), is(1));
    }

    @Test
    @WithData
    public void test_remove_tag_from_photo() throws ServiceException {
        Photo p0 = getPhoto(0);
        Photo p1 = getPhoto(1);
        Photo p2 = getPhoto(2);

        Tag t0 = tagService.getAllTags().get(0);
        Tag t1 = tagService.getAllTags().get(1);
        Tag t2 = tagService.getAllTags().get(2);

        // add tags to photos first
        tagService.addTagToPhotos(toList(p0, p1), t0);
        tagService.addTagToPhotos(toList(p1), t1);
        tagService.addTagToPhotos(toList(p2), t2);

        // assert that they have 6he tags attached
        assertThat(getTags(p0), equalTo(toList(t0)));
        assertThat(getTags(p1), containsInAnyOrder(t0, t1));
        assertThat(getTags(p2), equalTo(toList(t2)));

        // remove t0 from both photos
        tagService.removeTagFromPhotos(toList(p0, p1, p2), t0);

        // assert that the tags were removed
        assertThat(getTags(p0), empty());
        assertThat(getTags(p1), equalTo(toList(t1)));

        // assert that p2 was not affected
        assertThat(getTags(p2), equalTo(toList(t2)));
    }
    @Test
    @WithData
    public void test_get_most_frequent() throws ServiceException {
        Photo p0 = getPhoto(0);
        Photo p1 = getPhoto(1);
        Photo p2 = getPhoto(2);

        Tag t1 = tagService.getAllTags().get(0);
        Tag t2 = tagService.getAllTags().get(1);
        Tag t3 = tagService.getAllTags().get(2);
        Tag t4 = new Tag(4, "Test");
        Tag t5 = new Tag(5, "test2");
        Tag t6 = new Tag(6, "test3");
        Tag t7 = new Tag(7, "test4");

        tagService.create(t4);
        tagService.create(t5);
        tagService.create(t6);
        tagService.create(t7);
        tagService.addTagToPhotos(toList(p0, p1), t1);
        tagService.addTagToPhotos(toList(p1, p0), t2);
        tagService.addTagToPhotos(toList(p2, p1), t3);
        tagService.addTagToPhotos(toList(p0, p2), t4);
        tagService.addTagToPhotos(toList(p0, p1, p2), t5);
        tagService.addTagToPhotos(toList(p1), t6);

        List<Tag> frequent = tagService.getMostFrequentTags(toList(p0, p1, p2));
        assertThat(frequent.size(), is(5));

        assertThat(frequent, containsInAnyOrder(t1, t2, t3, t4, t5));
        assertThat(frequent, not(contains(t6)));
        assertThat(frequent, not(contains(t7)));
    }

    @Test(expected = ServiceException.class)
    @WithData
    public void test_get_most_frequent_with_no_tags() throws ServiceException {
        // photos have no tags
        Photo p0 = getPhoto(0);
        Photo p1 = getPhoto(1);
        Photo p2 = getPhoto(2);

        tagService.getMostFrequentTags(toList(p0, p1, p2));
    }
    @Test
    @WithData
    public void test_no_tags_available_for_photos() throws ServiceException {
        for (Photo photo : photoService.getAllPhotos()) {
            assertThat(getTags(photo), empty());
        }
    }

    @Test(expected = ServiceException.class)
    @WithData
    public void test_create_duplicate_throws() throws ServiceException {
        tagService.create(new Tag(1, "Person"));
    }
}
