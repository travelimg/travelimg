package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public interface SlideDAO{

    PhotoSlide create(PhotoSlide slide) throws DAOException, ValidationException;
    MapSlide create(MapSlide slide) throws DAOException, ValidationException;
    TitleSlide create(TitleSlide slide) throws DAOException, ValidationException;

    PhotoSlide update(PhotoSlide slide) throws DAOException, ValidationException;
    MapSlide update(MapSlide slide) throws DAOException, ValidationException;
    TitleSlide update(TitleSlide slide) throws DAOException, ValidationException;

    void delete(PhotoSlide slide) throws DAOException, ValidationException;
    void delete(MapSlide slide) throws DAOException, ValidationException;
    void delete(TitleSlide slide) throws DAOException, ValidationException;

    /**
     * Delete all slides which contain the given photo.
     *
     * @param photo The photo for which slides should be deleted.
     * @throws DAOException If the slides can not be deleted.
     */
    void deleteAllSlidesWithPhoto(Photo photo) throws DAOException;

    List<PhotoSlide> getPhotoSlidesForSlideshow(int slideshowId) throws DAOException;
    List<MapSlide> getMapSlidesForSlideshow(int slideshowId) throws DAOException;
    List<TitleSlide> getTitleSlidesForSlideshow(int slideshowId) throws DAOException;
}