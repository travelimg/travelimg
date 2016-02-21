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

import at.ac.tuwien.qse.sepm.dao.PhotoDAO;
import at.ac.tuwien.qse.sepm.dao.WithData;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.service.PhotoService;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.ServiceTestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class SlideServiceTest extends ServiceTestBase {

    @Autowired
    private PhotoService photoService;
    @Autowired
    private SlideServiceImpl slideService;
    @Autowired
    private SlideshowServiceImpl slideshowService;

    private PhotoSlide createPhotoSlide() throws ServiceException {
        return new PhotoSlide(1, 1, 1, "caption", photoService.getAllPhotos().get(0));
    }
    private MapSlide createMapSlide() throws ServiceException {
        return new MapSlide(1, 1, 1, "caption", 0.0, 0.0, 10);
    }
    private TitleSlide createTitleSlide() throws ServiceException {
        return new TitleSlide(1, 1, 1, "caption", 0);
    }
    private Slideshow getDefaultSlideshow() throws ServiceException {
        return slideshowService.getAllSlideshows().get(0);
    }

    @Test
    @WithData
    public void test_create_photoSlide_persists() throws ServiceException {
        PhotoSlide slide = createPhotoSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getPhotoSlides(), contains(slide));
    }

    @Test
    @WithData
    public void test_create_mapSlide_persists() throws ServiceException {
        MapSlide slide = createMapSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getMapSlides(), contains(slide));
    }

    @Test
    @WithData
    public void test_create_titleSlide_persists() throws ServiceException {
        TitleSlide slide = createTitleSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getTitleSlides(), contains(slide));
    }

    @Test
    @WithData
    public void test_update_photoSlide_persists() throws ServiceException {
        PhotoSlide slide = createPhotoSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getPhotoSlides(), contains(slide));

        slide.setPhoto(photoService.getAllPhotos().get(2));
        slideService.update(slide);

        assertThat(getDefaultSlideshow().getPhotoSlides(), contains(slide));
    }

    @Test
    @WithData
    public void test_update_mapSlide_persists() throws ServiceException {
        MapSlide slide = createMapSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getMapSlides(), contains(slide));

        slide.setLatitude(42);
        slide.setLongitude(30);
        slide.setZoomLevel(15);
        slideService.update(slide);

        assertThat(getDefaultSlideshow().getMapSlides(), contains(slide));
    }

    @Test
    @WithData
    public void test_update_titleSlide_persists() throws ServiceException {
        TitleSlide slide = createTitleSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getTitleSlides(), contains(slide));

        slide.setColor(100);
        slideService.update(slide);

        assertThat(getDefaultSlideshow().getTitleSlides(), contains(slide));
    }

    @Test
    @WithData
    public void test_delete_photoSlide_persists() throws ServiceException {
        PhotoSlide slide = createPhotoSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getPhotoSlides(), contains(slide));

        slideService.delete(slide);

        assertThat(getDefaultSlideshow().getPhotoSlides(), empty());
    }

    @Test
    @WithData
    public void test_delete_mapSlide_persists() throws ServiceException {
        MapSlide slide = createMapSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getMapSlides(), contains(slide));

        slideService.delete(slide);

        assertThat(getDefaultSlideshow().getMapSlides(), empty());
    }

    @Test
    @WithData
    public void test_delete_titleSlide_persists() throws ServiceException {
        TitleSlide slide = createTitleSlide();
        slideService.create(slide);

        assertThat(getDefaultSlideshow().getTitleSlides(), contains(slide));

        slideService.delete(slide);

        assertThat(getDefaultSlideshow().getTitleSlides(), empty());
    }
}
