package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Exif;
import at.ac.tuwien.qse.sepm.entities.Photo;

import java.util.List;

public interface ExifDAO {
    public Exif create(Exif e) throws DAOException;

    /**
     * Read the exif metadata for a given photo.
     *
     * @param photo The photo for which to read the exif data.
     * @return The found exif data entity.
     * @throws DAOException if an error occurs during the read.
     */
    public Exif read(Photo photo) throws DAOException;

    public void update(Exif e) throws DAOException;
    public void delete(Exif e) throws DAOException;
    public List<Exif> readAll() throws DAOException;
    public Exif importExif(Photo p) throws DAOException;
}
