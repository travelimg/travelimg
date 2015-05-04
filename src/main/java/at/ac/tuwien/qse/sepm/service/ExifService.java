package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

public interface ExifService {
    public void changeExif(Exif e);
    public void importExif(Photo p);
}
