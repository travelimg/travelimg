package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Slideshow;

/**
 * Created by mb on 08.06.15.
 */
public class SlideshowValidator {

    public static void validate(Slideshow entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("Entity can not be null");

        if (entity.getName() == null)
            throw new ValidationException("Name can not be null");

        if (entity.getDurationBetweenPhotos() == null)
            throw new ValidationException("Duration between Photos can not be null");

    }

    /**
     * Validate ID of slideshow <tt>entity</tt>.
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
