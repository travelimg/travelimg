package at.ac.tuwien.qse.sepm.util;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Utility methods for file system interactions.
 *
 * Eases some common file operations and can be mocked by tests which involve IO.
 */
public interface IOHandler {

    void copyFromTo(Path source, Path dest) throws IOException;
    void delete(Path path) throws IOException;
}
