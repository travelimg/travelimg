package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by David on 20.06.2015.
 */
public class PhotoPathFilter extends PhotoFilter {
    private Path includedPath;

    @Override
    public boolean test(Photo photo) {
        return testPath(photo);
    }

    private boolean testPath(Photo photo) {
        if( photo.getPath() == null) {
            return true;
        }
        return photo.getPath().startsWith((getIncludedPath().toString()));
    }

    public Path getIncludedPath() {
        return includedPath;
    }

    public void setIncludedPath(Path includedPath) {
        this.includedPath = includedPath;
    }
}
