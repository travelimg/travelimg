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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PhotoMetadata {

    private LocalDateTime datetime = null;
    private double longitude = 0.0;
    private double latitude = 0.0;
    private Rating rating = Rating.NONE;
    private Photographer photographer = null;
    private Place place = null;
    private Journey journey = null;
    private Set<Tag> tags = new HashSet<>();

    public PhotoMetadata() {
    }

    public PhotoMetadata(LocalDateTime datetime, double latitude, double longitude, Rating rating, Photographer photographer, Place place, Journey journey) {
        this.datetime = datetime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rating = rating;
        this.photographer = photographer;
        this.place = place;
        this.journey = journey;
    }

    public PhotoMetadata(PhotoMetadata from) {
        if (from == null) throw new IllegalArgumentException();
        if (from.datetime != null) {
            this.datetime = LocalDateTime.from(from.datetime);
        }
        this.longitude = from.longitude;
        this.latitude = from.latitude;
        this.rating = from.rating;
        this.photographer = from.photographer;
        this.place = from.place;
        this.journey = from.journey;
        this.tags.addAll(from.getTags());
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime date) {
        this.datetime = date;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        if (rating == null) throw new IllegalArgumentException();
        this.rating = rating;
    }
    
    public Photographer getPhotographer() {
        return photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags){
        this.tags = tags;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PhotoMetadata))
            return false;

        PhotoMetadata that = (PhotoMetadata) o;

        if (Double.compare(that.latitude, latitude) != 0)
            return false;
        if (Double.compare(that.longitude, longitude) != 0)
            return false;
        if (datetime != null ? !datetime.equals(that.datetime) : that.datetime != null)
            return false;
        if (photographer != null ?
                !photographer.equals(that.photographer) :
                that.photographer != null)
            return false;
        if (place != null ? !place.equals(that.place) : that.place != null)
            return false;
        if (rating != that.rating)
            return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = datetime != null ? datetime.hashCode() : 0;
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (photographer != null ? photographer.hashCode() : 0);
        result = 31 * result + (place != null ? place.hashCode() : 0);
        result = 31 * result + (journey != null ? journey.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PhotoMetadata{" +
                "datetime=" + datetime +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", rating=" + rating +
                ", photographer=" + photographer +
                ", place=" + place +
                ", journey=" + journey +
                ", tags=" + tags +
                '}';
    }
}
