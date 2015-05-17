package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Exif;

/**
 * Created by David on 12.05.2015.
 */
/**
 * @deprecated
 */
public class ExifValidator {

    public static void validate(Exif entity) throws ValidationException {

        if (entity == null) {
            throw new ValidationException("Entity can not be null");
        }

    }
}
