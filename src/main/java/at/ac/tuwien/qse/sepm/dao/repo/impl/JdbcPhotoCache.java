package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.*;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoNotFoundException;
import at.ac.tuwien.qse.sepm.entities.*;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Photo cache that stores photo instances in an SQLite database.
 */
public class JdbcPhotoCache implements PhotoCache {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private PhotoDAO photoDAO;

    @Autowired
    private TagDAO tagDAO;

    @Autowired
    private PhotoTagDAO photoTagDAO;

    @Autowired
    private PlaceDAO placeDAO;

    @Autowired
    private JourneyDAO journeyDAO;

    @Autowired
    private PhotographerDAO photographerDAO;

    @Override public synchronized void put(Photo photo) throws DAOException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("putting {}", photo);

        // Save sub-entities.
        photo.getData().setPhotographer(save(photo.getData().getPhotographer()));
        photo.getData().setPlace(save(photo.getData().getPlace()));
        photo.getData().setJourney(save(photo.getData().getJourney()));
        Set<Tag> tags = new HashSet<>(photo.getData().getTags());
        photo.getData().getTags().clear();
        photoTagDAO.deleteAllEntriesOfSpecificPhoto(photo);
        for (Tag t : tags) {
            Tag tag = save(t);
            photo.getData().getTags().add(tag);
        }

        // Save photo
        if (isNew(photo)) {
            try {
                LOGGER.debug("creating photo {}", photo);
                photo = photoDAO.create(photo);
            } catch (DAOException ex) {
                LOGGER.warn("failed creating photo {}", photo);
                throw new DAOException(ex);
            }
        } else {
            try {
                LOGGER.debug("updating existing photo {}", photo);
                photoDAO.update(photo);
            } catch (DAOException ex) {
                LOGGER.warn("failed updating photo {}", photo);
                throw new DAOException(ex);
            }
        }

        // Link tags to pho
        for (Tag tag : photo.getData().getTags()) {
            try {
                photoTagDAO.createPhotoTag(photo, tag);
            } catch (ValidationException ex) {
                LOGGER.warn("ignoring invalid tag {} or photo {}", tag, photo);
            }
        }

        LOGGER.debug("put {}", photo);
    }

    @Override public synchronized void remove(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("removing {}", file);
        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        Photo photo;
        try {
            photo = photoDAO.getByFile(file);
        } catch (DAOException ex) {
            throw new PhotoNotFoundException(this, file);
        }

        photoDAO.delete(photo);
        LOGGER.debug("removed {}", file);
    }



    @Override public Collection<Path> index() throws DAOException {
        LOGGER.debug("indexing");
        Collection<Path> result = photoDAO.readAllPaths();
        LOGGER.debug("indexed {}", result.size());
        return result;
    }

    @Override public Photo read(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);
        if (!contains(file)) {
            LOGGER.warn("photo not found with file {}", file);
            throw new PhotoNotFoundException(this, file);
        }
        try {
            Photo photo = photoDAO.getByFile(file);
            LOGGER.debug("read {}", photo);
            return photo;
        } catch (DAOException ex) {
            throw new DAOException(ex);
        }
    }

    private boolean isNew(Photo photo) {
        try {
            Photo cached = photoDAO.getByFile(photo.getFile());
            photo.setId(cached.getId());
            return false;
        } catch (DAOException ex) {
            return true;
        }
    }

    private Photographer save(Photographer photographer) throws DAOException {
        if (photographer != null) {
            try {
                LOGGER.debug("reading photographer {}", photographer);
                photographer = photographerDAO.getByName(photographer.getName());
                LOGGER.debug("read photographer {}", photographer);
                return photographer;
            } catch (DAOException ex) {
                try {
                    LOGGER.debug("creating photographer {}", photographer);
                    photographer = photographerDAO.create(photographer);
                    LOGGER.debug("created photographer {}", photographer);
                    return photographer;
                } catch (DAOException | ValidationException exx) {
                    LOGGER.warn("failed creating photographer {}", photographer);
                }
            }
        }
        return null;
    }

    private Journey save(Journey journey) throws DAOException {
        if (journey != null) {
            try {
                LOGGER.debug("reading journey {}", journey);
                journey = journeyDAO.getByName(journey.getName());
                LOGGER.debug("read journey {}", journey);

                return journey;
            } catch (DAOException ex) {
                try {
                    LOGGER.debug("creating journey {}", journey);
                    journey = journeyDAO.create(journey);
                    LOGGER.debug("created journey {}", journey);
                    return journey;
                } catch (DAOException | ValidationException exx) {
                    LOGGER.warn("failed creating journey {}", journey);
                }
            }
        }
        return null;
    }

    private Place save(Place place) throws DAOException {
        if (place != null) {
            try {
                LOGGER.debug("reading place {}", place);
                place = placeDAO.readByCountryCity(place.getCountry(), place.getCity());
                LOGGER.debug("read place {}", place);
                return place;
            } catch (DAOException ex) {
                try {
                    LOGGER.debug("creating place {}", place);
                    place = placeDAO.create(place);
                    LOGGER.debug("created place {}", place);
                    return place;
                } catch (DAOException | ValidationException exx) {
                    LOGGER.warn("failed creating place {}", place);
                }
            }
        }
        return null;
    }

    private Tag save(Tag tag) throws DAOException {
        try {
            LOGGER.debug("reading tag {}", tag);
            tag = tagDAO.readName(tag);
            LOGGER.debug("read tag {}", tag);
            return tag;
        } catch (DAOException ex) {
            try {
                LOGGER.debug("creating tag {}", tag);
                tag = tagDAO.create(tag);
                LOGGER.debug("created tag {}", tag);
                return tag;
            } catch (DAOException | ValidationException exx) {
                LOGGER.warn("failed creating tag {}", tag);
                throw new DAOException(exx);
            }
        }
    }
}
