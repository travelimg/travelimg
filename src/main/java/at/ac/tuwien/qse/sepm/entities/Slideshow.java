package at.ac.tuwien.qse.sepm.entities;

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
}
