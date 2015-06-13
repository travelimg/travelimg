package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Slide;
import at.ac.tuwien.qse.sepm.entities.Slideshow;

/**
 * Created by mb on 08.06.15.
 */
public class SlideValidator {

    public static void validate(Slide entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("Entity can not be null");

        if (entity.getPhoto() == null)
            throw new ValidationException("Photo can not be null");

        if (entity.getOrder() < 0)
            throw new ValidationException("Order must be non-negative");
    }

    public static void validateID(Integer id) throws ValidationException {
        if (id == null) {
            throw new ValidationException("ID must not be null");
        }
        if (id < 0) {
            throw new ValidationException("ID must be non-negative");
        }
    }

}
