package at.ac.tuwien.qse.sepm.sevice.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Photographer;
import at.ac.tuwien.qse.sepm.entities.Rating;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.service.impl.PhotoFilter;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhotoFilterTest {

    private Photo createMatchingPhoto() {
        Photo photo = new Photo();
        photo.setRating(Rating.NONE);
        photo.setPhotographer(new Photographer(1, "John"));
        photo.setDatetime(LocalDateTime.of(2015, 12, 1, 0, 0));
        return photo;
    }

    private PhotoFilter createFilter() {
        PhotoFilter filter = new PhotoFilter();
        filter.getIncludedPhotographers().add(new Photographer(1, "John"));
        filter.getIncludedRatings().add(Rating.NONE);
        filter.getIncludedMonths().add(YearMonth.of(2015, 12));
        filter.setUntaggedIncluded(true);
        return filter;
    }

    @Test
    public void filter_untaggedIncludedAndPhotoIsUntagged_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        photo.getTags().clear();
        filter.setUntaggedIncluded(true);
        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_untaggedIncludedAndPhotoIsTagged_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        photo.getTags().add(new Tag(1, "blue"));
        filter.setUntaggedIncluded(true);
        filter.getIncludedCategories().clear();

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_untaggedNotIncludedAndPhotoIsUntagged_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        photo.getTags().clear();
        filter.setUntaggedIncluded(false);

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_categoriesPhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedCategories().clear();
        filter.getIncludedCategories().add(new Tag(1, "blue"));
        filter.getIncludedCategories().add(new Tag(2, "red"));
        photo.getTags().clear();
        photo.getTags().add(new Tag(3, "green"));
        photo.getTags().add(new Tag(3, "pink"));

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_categoriesPhotoHasSame_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedCategories().clear();
        filter.getIncludedCategories().add(new Tag(1, "blue"));
        filter.getIncludedCategories().add(new Tag(2, "red"));
        photo.getTags().clear();
        photo.getTags().add(new Tag(1, "blue"));
        photo.getTags().add(new Tag(2, "red"));

        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_categoriesPhotoHasOverlap_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedCategories().clear();
        filter.getIncludedCategories().add(new Tag(1, "blue"));
        filter.getIncludedCategories().add(new Tag(2, "red"));
        photo.getTags().clear();
        photo.getTags().add(new Tag(2, "red"));
        photo.getTags().add(new Tag(3, "green"));

        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_ratingPhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedRatings().clear();
        filter.getIncludedRatings().add(Rating.GOOD);
        filter.getIncludedRatings().add(Rating.NEUTRAL);
        photo.setRating(Rating.NONE);

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_ratingPhotoHasSame_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedRatings().clear();
        filter.getIncludedRatings().add(Rating.GOOD);
        filter.getIncludedRatings().add(Rating.NEUTRAL);
        photo.setRating(Rating.GOOD);

        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_photographerPhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedPhotographers().clear();
        filter.getIncludedPhotographers().add(new Photographer(1, "John"));
        filter.getIncludedPhotographers().add(new Photographer(2, "Bill"));
        photo.setPhotographer(new Photographer(3, "Tina"));

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_photographerPhotoHasSame_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedPhotographers().clear();
        filter.getIncludedPhotographers().add(new Photographer(1, "John"));
        filter.getIncludedPhotographers().add(new Photographer(2, "Bill"));
        photo.setPhotographer(new Photographer(2, "Bill"));

        assertTrue(filter.test(photo));
    }

    @Test
    public void filter_monthPhotoHasDifferent_noMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedMonths().clear();
        filter.getIncludedMonths().add(YearMonth.of(2014, 3));
        filter.getIncludedMonths().add(YearMonth.of(2015, 12));
        photo.setDatetime(LocalDateTime.of(2012, 10, 1, 0, 0));

        assertFalse(filter.test(photo));
    }

    @Test
    public void filter_monthPhotoHasSame_isMatch() {
        PhotoFilter filter = createFilter();
        Photo photo = createMatchingPhoto();

        filter.getIncludedMonths().clear();
        filter.getIncludedMonths().add(YearMonth.of(2014, 3));
        filter.getIncludedMonths().add(YearMonth.of(2015, 12));
        photo.setDatetime(LocalDateTime.of(2015, 12, 1, 0, 0));

        assertTrue(filter.test(photo));
    }
}
