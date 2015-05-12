package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Photo;

import java.io.File;

public class PhotoValidator {

    public static void validate(Photo entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("Entity can not be null");

        if (entity.getPhotographer() == null)
            throw new ValidationException("Photographer can not be null");

        if (entity.getRating() < 0 || entity.getRating() > 4)
            throw new ValidationException("Rating must be between 0 and 4");

        if (entity.getPath() == null)
            throw new ValidationException("Photo path can not be null");

        File file = new File(entity.getPath());
        if (!file.exists())
            throw new ValidationException("Photo does not exist");
    }

    /**
     * Validate ID of photo <tt>entity</tt>.
     *
     * @param entity must not be null;
     *               entity.id must not be null;
     *               entity.id must be non-negative
     * @throws ValidationException if any precondition is violated
     */
    public static void validateID(Photo entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Entity must not be null");
        }
        if (entity.getId() == null) {
            throw new ValidationException("ID must not be null");
        }
        if (entity.getId() < 0) {
            throw new ValidationException("ID must be non-negative");
        }
    }
}
