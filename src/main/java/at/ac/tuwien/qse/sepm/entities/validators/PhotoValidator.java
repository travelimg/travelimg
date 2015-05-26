package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Photo;

import java.io.File;

public class PhotoValidator {

    public static void validate(Photo entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("Entity can not be null");

        if (entity.getPhotographer() == null)
            throw new ValidationException("Photographer can not be null");

        if (entity.getPath() == null)
            throw new ValidationException("Photo path can not be null");
    }

    /**
     * Validate ID of photo <tt>entity</tt>.
     *
     * @param id must not be null and must be non-negative
     * @throws ValidationException if any precondition is violated
     */
    public static void validateID(Integer id) throws ValidationException {
        if (id == null) {
            throw new ValidationException("ID must not be null");
        }
        if (id < 0) {
            throw new ValidationException("ID must be non-negative");
        }
    }
}
