package at.ac.tuwien.qse.sepm.entities;

/**
 * Created by mb on 08.06.15.
 */
public class Slideshow {

    public Slideshow(){

    }

    public Slideshow(Integer id, String name, Double durationBetweenPhotos)
    {
        this.id = id;
        this.name=name;
        this.durationBetweenPhotos = durationBetweenPhotos;
    }

    private Integer id;
    private String name;
    private Double durationBetweenPhotos;

    public Integer getId() {return id;}

    public void setId(Integer id) {this.id = id;}


    public Double getDurationBetweenPhotos() {
        return durationBetweenPhotos;
    }

    public void setDurationBetweenPhotos(Double durationBetweenPhotos) {this.durationBetweenPhotos = durationBetweenPhotos;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}
}
