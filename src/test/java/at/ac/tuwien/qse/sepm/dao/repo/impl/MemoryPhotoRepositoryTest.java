package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MemoryPhotoRepositoryTest extends PhotoRepositoryTest {

    private final Path basePath = Paths.get("test/path");

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return basePath.resolve("some/file.jpg");
            }

            @Override public Path getFile2() {
                return basePath.resolve("other/image.jpg");
            }
        };
    }

    @Override protected PhotoRepository getObject() {
        return new MemoryPhotoRepository(basePath) {
            @Override protected Photo read(Path file, InputStream stream) throws IOException {
                return getContext().read(file, stream);
            }

            @Override protected void update(Photo photo, OutputStream stream) throws IOException {
                getContext().update(photo, stream);
            }
        };
    }
}
