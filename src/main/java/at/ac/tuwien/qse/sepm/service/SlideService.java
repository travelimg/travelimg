package at.ac.tuwien.qse.sepm.service;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.entities.MapSlide;
import at.ac.tuwien.qse.sepm.entities.PhotoSlide;
import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.TitleSlide;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;

import java.util.List;

/**
 * Created by mb on 08.06.15.
 */
public interface SlideService {

    PhotoSlide create(PhotoSlide slide) throws ServiceException;
    MapSlide create(MapSlide slide) throws ServiceException;
    TitleSlide create(TitleSlide slide) throws ServiceException;

    PhotoSlide update(PhotoSlide slide) throws ServiceException;
    MapSlide update(MapSlide slide) throws ServiceException;
    TitleSlide update(TitleSlide slide) throws ServiceException;

    void delete(PhotoSlide slide) throws ServiceException;
    void delete(MapSlide slide) throws ServiceException;
    void delete(TitleSlide slide) throws ServiceException;
}
