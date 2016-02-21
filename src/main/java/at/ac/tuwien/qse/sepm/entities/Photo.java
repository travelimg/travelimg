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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Photo implements Comparable<Photo> {

    private Integer id;
    private Path file = Paths.get("");
    private final PhotoMetadata data;

    public Photo() {
        data = new PhotoMetadata();
    }

    public Photo(Path file) {
        this(file, new PhotoMetadata());
    }

    public Photo(Path file, PhotoMetadata data) {
        if (file == null) throw new IllegalArgumentException();
        if (data == null) throw new IllegalArgumentException();
        this.file = file;
        this.data = data;
    }

    public Photo(Integer id, Path file, PhotoMetadata data) {
        if (id == null) throw new IllegalArgumentException();
        if (file == null) throw new IllegalArgumentException();
        if (data == null) throw new IllegalArgumentException();
        this.id = id;
        this.file = file;
        this.data = data;
    }

    public Photo(Photo from) {
        if (from == null) throw new IllegalArgumentException();
        this.id = from.id;
        this.file = from.file;
        this.data = new PhotoMetadata(from.data);
    }

    @Deprecated
    public Photo(Integer id, Photographer photographer, String path, Rating rating, LocalDateTime datetime, double latitude, double longitude, Place place) {
        this(Paths.get(path));
        setId(id);
        getData().setPhotographer(photographer);
        getData().setRating(rating);
        getData().setDatetime(datetime);
        getData().setLatitude(latitude);
        getData().setLongitude(longitude);
        getData().setPlace(place);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Path getFile() {
        return file;
    }

    public PhotoMetadata getData() {
        return data;
    }

    public String getPath() {
        return file.toString();
    }

    public void setPath(String path) {
        this.file = Paths.get(path);
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Photo))
            return false;

        Photo photo = (Photo) o;

        if (id != photo.id)
            return false;
        if (data != null ? !data.equals(photo.data) : photo.data != null)
            return false;
        if (file != null ? !file.equals(photo.file) : photo.file != null)
            return false;

        return true;
    }

    @Override public int hashCode() {
        int result = id != null ? id : 0;
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", file=" + file +
                ", data=" + data +
                '}';
    }

    @Override public int compareTo(Photo o) {
        return getData().getDatetime().compareTo(o.getData().getDatetime());
    }
}
