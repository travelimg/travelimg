package at.ac.tuwien.qse.sepm.entities.validators;

/**
 * Created by mb on 08.06.15.
 */
public class SlideValidator {

    public static void validateID(Integer id) throws ValidationException {
        if (id == null) {
            throw new ValidationException("ID must not be null");
        }
        if (id < 0) {
            throw new ValidationException("ID must be non-negative");
        }
    }

}
