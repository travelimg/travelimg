package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Tag;
import sun.security.validator.ValidatorException;

public class TagValidator {

    /**
     * Validate <tt>tag</tt>.
     *
     * @param tag must not be null; field <tt>tag.name</tt> must not be null and must contain
     *            something other than whitespaces;
     * @throws ValidatorException if any precondition is violated
     */
    public static void validate(Tag tag) throws ValidationException {
        if (tag == null) {
            throw new ValidationException("Entity must not be null");
        }
        if (tag.getName() == null) {
            throw new ValidationException("Name must not be null");
        }
        if ((tag.getName().isEmpty())) {
            throw new ValidationException("Name must not be empty");
        }
        if (tag.getName().trim().length() == 0) {
            throw new ValidationException("Name must not only contain Whitespaces");
        }
    }

    /**
     * Validates ID of Tag <tt>tag</tt>.
     *
     * @param tag must not be null;  <tt>tag.id</tt> must not be null and non-negative
     * @throws ValidationException if any precondition is violated
     */
    public static void validateID(Tag tag) throws ValidationException {
        if (tag == null) {
            throw new ValidationException("Entity must not be null");
        }
        if (tag.getId() == null) {
            throw new ValidationException("ID must not be null");
        }
        if (tag.getId() < 0) {
            throw new ValidationException("ID must be non-negative");
        }
    }
}
