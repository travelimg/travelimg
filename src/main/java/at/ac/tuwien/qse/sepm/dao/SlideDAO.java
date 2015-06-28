package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

public interface SlideDAO{

    /**
     * Create a Slide which contains a Photo
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    PhotoSlide create(PhotoSlide slide) throws DAOException, ValidationException;

    /**
     * Create a Slide which contains a Map
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    MapSlide create(MapSlide slide) throws DAOException, ValidationException;

    /**
     * Create a Slide which contains a Title
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    TitleSlide create(TitleSlide slide) throws DAOException, ValidationException;

    /**
     * Update a PhotoSlide
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    PhotoSlide update(PhotoSlide slide) throws DAOException, ValidationException;

    /**
     * Update a MapSlide
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    MapSlide update(MapSlide slide) throws DAOException, ValidationException;

    /**
     * Update a TitleSide
     * @param slide
     * @return
     * @throws DAOException
     * @throws ValidationException
     */
    TitleSlide update(TitleSlide slide) throws DAOException, ValidationException;

    /**
     * Delete a PhotoSlide
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */
    void delete(PhotoSlide slide) throws DAOException, ValidationException;

    /**
     * Delete a MapSlide
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */
    void delete(MapSlide slide) throws DAOException, ValidationException;

    /**
     * Delete a Title Slide
     * @param slide
     * @throws DAOException
     * @throws ValidationException
     */
    void delete(TitleSlide slide) throws DAOException, ValidationException;

    /**
     * Delete all slides which contain the given photo.
     *
     * @param photo The photo for which slides should be deleted.
     * @throws DAOException If the slides can not be deleted.
     */
    void deleteAllSlidesWithPhoto(Photo photo) throws DAOException;

    /**
     * Get all PhotoSlides
     * @param slideshowId
     * @return
     * @throws DAOException
     */
    List<PhotoSlide> getPhotoSlidesForSlideshow(int slideshowId) throws DAOException;

    /**
     * Get all MapSlides
     * @param slideshowId
     * @return
     * @throws DAOException
     */
    List<MapSlide> getMapSlidesForSlideshow(int slideshowId) throws DAOException;

    /**
     * Get all TitleSlides
     * @param slideshowId
     * @return
     * @throws DAOException
     */
    List<TitleSlide> getTitleSlidesForSlideshow(int slideshowId) throws DAOException;
}
