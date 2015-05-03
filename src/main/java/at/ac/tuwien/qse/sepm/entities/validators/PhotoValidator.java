package at.ac.tuwien.qse.sepm.entities.validators;


import at.ac.tuwien.qse.sepm.entities.Photo;

import java.io.File;

public class PhotoValidator implements EntityValidator<Photo> {

    @Override
    public void validate(Photo entity) throws ValidationException {
        if(entity == null)
            throw new ValidationException("Entity can not be null");

        if(entity.getPhotographer() == null)
            throw new ValidationException("Photographer can not be null");

        if(entity.getDate() == null)
            throw new ValidationException("Date can not be null");

        if(entity.getRating() < 0 || entity.getRating() > 4)
            throw new ValidationException("Rating must be between 0 and 4");

        if(entity.getPath() == null)
            throw new ValidationException("Photo path can not be null");

        File file = new File(entity.getPath());
        if(!file.exists())
            throw new ValidationException("Photo does not exist");
    }
}
