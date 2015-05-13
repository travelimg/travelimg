package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Photographer;

public class PhotographerValidator {
    public static void validate(Photographer entity) throws ValidationException {
        if(entity == null)
            throw new ValidationException("Entity cannot be null");

        if(entity.getName()==null || entity.getName().isEmpty())
            throw new ValidationException("Name cannot be null or empty");

    }
}