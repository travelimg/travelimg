package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PhotoPathFilter extends PhotoFilter {

    private Path includedPath = Paths.get("");

    @Override
    public boolean test(Photo photo) {
        return testPath(photo);
    }

    private boolean testPath(Photo photo) {
        return photo.getPath().startsWith((getIncludedPath().toString()));
    }

    public Path getIncludedPath() {
        return includedPath;
    }

    public void setIncludedPath(Path includedPath) {
        if (includedPath == null) throw new IllegalArgumentException();
        this.includedPath = includedPath;
    }
}
