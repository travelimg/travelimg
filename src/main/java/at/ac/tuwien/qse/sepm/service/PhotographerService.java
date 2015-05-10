package at.ac.tuwien.qse.sepm.service;


import at.ac.tuwien.qse.sepm.entities.Photographer;

import java.util.List;

public interface PhotographerService {

    /**
     * Retrieve all existing photographers.
     *
     * @return A List of all currently known photographers.
     * @throws ServiceException If the reading all photographers fails.
     */
    List<Photographer> readAll() throws ServiceException;
}
