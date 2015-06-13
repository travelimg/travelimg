package at.ac.tuwien.qse.sepm.entities;

import java.util.List;

public class Slideshow {

    private Integer id;
    private String name;
    private Double durationBetweenPhotos;
    private List<Slide> slides;

    public Slideshow() {

    }

    public Slideshow(Integer id, String name, Double durationBetweenPhotos, List<Slide> slides) {
        this.id = id;
        this.name = name;
        this.durationBetweenPhotos = durationBetweenPhotos;
        this.slides = slides;
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

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }
}
