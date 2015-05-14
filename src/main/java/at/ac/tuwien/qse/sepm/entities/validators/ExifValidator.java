package at.ac.tuwien.qse.sepm.entities.validators;

import at.ac.tuwien.qse.sepm.entities.Exif;

/**
 * Created by David on 12.05.2015.
 */
public class ExifValidator {

    public static void validate(Exif entity) throws ValidationException {

        if (entity == null) {
            throw new ValidationException("Entity can not be null");
        }

        if (entity.getLatitude() > 180 || entity.getLatitude() > -180 || entity.getLongitude() > 180
                || entity.getLongitude() > -180) {
            throw new ValidationException("GPS-data can not be greater or smaller than 180°");
        }

    }
}
