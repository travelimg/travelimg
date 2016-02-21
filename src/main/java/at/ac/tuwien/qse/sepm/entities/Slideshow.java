package at.ac.tuwien.qse.sepm.entities;

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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Slideshow {

    private Integer id;
    private String name;
    private Double durationBetweenPhotos;

    private Collection<PhotoSlide> photoSlides = new ArrayList<>();
    private Collection<MapSlide> mapSlides = new ArrayList<>();
    private Collection<TitleSlide> titleSlides = new ArrayList<>();

    public Slideshow(Integer id, String name, Double durationBetweenPhotos) {
        this.id = id;
        this.name = name;
        this.durationBetweenPhotos = durationBetweenPhotos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Double getDurationBetweenPhotos() {
        return durationBetweenPhotos;
    }

    public void setDurationBetweenPhotos(Double durationBetweenPhotos) {
        this.durationBetweenPhotos = durationBetweenPhotos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<PhotoSlide> getPhotoSlides() {
        return photoSlides;
    }
    public Collection<MapSlide> getMapSlides() {
        return mapSlides;
    }
    public Collection<TitleSlide> getTitleSlides() {
        return titleSlides;
    }

    public Collection<Slide> getAllSlides() {
        List<Slide> slides = new ArrayList<>();
        slides.addAll(getPhotoSlides());
        slides.addAll(getMapSlides());
        slides.addAll(getTitleSlides());

        return slides;
    }

    @Override
    public String toString() {
        return "Slideshow{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", durationBetweenPhotos=" + durationBetweenPhotos +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Slideshow))
            return false;

        Slideshow slideshow = (Slideshow) o;

        if (id != null ? !id.equals(slideshow.id) : slideshow.id != null)
            return false;
        if (name != null ? !name.equals(slideshow.name) : slideshow.name != null)
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }


}
