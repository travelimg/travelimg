package at.ac.tuwien.qse.sepm.service;


import at.ac.tuwien.qse.sepm.entities.Photographer;

import java.util.List;

/**
 * Service for managing photographers.
 */
public interface PhotographerService {

    /**
     * Create an photographer.
     *
     * @param photographer Photographer to be created.
     * @return The newly created photographer object.
     * @throws ServiceException If an error occurs creating the photographer.
     */
    Photographer create(Photographer photographer) throws ServiceException;

    /**
     * Update given photographer.
     *
     * @param photographer Photographer to be updated.
     * @throws ServiceException If an error occurs during the update.
     */
    void update(Photographer photographer) throws ServiceException;

    /**
     * Retrieve all existing photographers.
     *
     * @return A List of all currently known photographers.
     * @throws ServiceException If the reading all photographers fails.
     */
    List<Photographer> readAll() throws ServiceException;
}
