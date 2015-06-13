package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.entities.Slide;

import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public interface SlideService {

    /**
     * Create a slide
     * @param slide
     * @throws ServiceException
     */
    void create(Slide slide) throws ServiceException;

    /**
     * Delete a slide
     * @param slide
     * @throws ServiceException
     */
    void delete(Slide slide) throws ServiceException;

    Slide update(Slide slide) throws ServiceException;

    /**
     * Return all Slides
     * @return
     * @throws ServiceException
     */
    List<Slide> readAll() throws ServiceException;
}
