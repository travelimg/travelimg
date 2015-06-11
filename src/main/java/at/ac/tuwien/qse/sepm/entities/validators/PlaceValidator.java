package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Place;


public class PlaceValidator {

    public static void validate(Place entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("Entity can not be null");

        if (entity.getCity() == null)
            throw new ValidationException("City can not be null");

        if (entity.getCountry() == null)
            throw new ValidationException("Country can not be null");

        if (entity.getJourney() == null ) {
            throw new ValidationException("Journey can't be null");
        }
    }

    /**
     * Validate ID of journey <tt>entity</tt>.
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
