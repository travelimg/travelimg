package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;


public interface SlideService {

    /**
     * Create a Slide which contains a Slide
     * @param slide
     * @return
     * @throws ServiceException
     */
    PhotoSlide create(PhotoSlide slide) throws ServiceException;

    /**
     * Create a Slide which conatains a Map
     * @param slide
     * @return
     * @throws ServiceException
     */
    MapSlide create(MapSlide slide) throws ServiceException;

    /**
     * Create a Slide which contains a Title
     * @param slide
     * @return
     * @throws ServiceException
     */
    TitleSlide create(TitleSlide slide) throws ServiceException;

    /**
     * Update a PhotoSlide
     * @param slide
     * @return
     * @throws ServiceException
     */
    PhotoSlide update(PhotoSlide slide) throws ServiceException;

    /**
     * Update a MapSlide
     * @param slide
     * @return
     * @throws ServiceException
     */
    MapSlide update(MapSlide slide) throws ServiceException;

    /**
     * Update a TitleSlide
     * @param slide
     * @return
     * @throws ServiceException
     */
    TitleSlide update(TitleSlide slide) throws ServiceException;

    /**
     * Delete a PhotoSlide
     * @param slide
     * @throws ServiceException
     */
    void delete(PhotoSlide slide) throws ServiceException;

    /**
     * Delete a MapSlide
     * @param slide
     * @throws ServiceException
     */
    void delete(MapSlide slide) throws ServiceException;

    /**
     * Delete a TitleSlide
     * @param slide
     * @throws ServiceException
     */
    void delete(TitleSlide slide) throws ServiceException;
}
