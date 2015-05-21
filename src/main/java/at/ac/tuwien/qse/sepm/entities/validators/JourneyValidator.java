package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Journey;

import java.io.File;

/**
 * Created by David on 21.05.2015.
 */
public class JourneyValidator {

    public static void validate(Journey entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("Entity can not be null");

        if (entity.getName() == null)
            throw new ValidationException("Name can not be null");

        if (entity.getStartDate() == null)
            throw new ValidationException("StartDate can not be null");

        if (entity.getEndDate() == null)
            throw new ValidationException("EndDate can not be null");

        if(entity.getEndDate().isBefore(entity.getStartDate()))
            throw new ValidationException("EndDate must not be before StartDate");
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
