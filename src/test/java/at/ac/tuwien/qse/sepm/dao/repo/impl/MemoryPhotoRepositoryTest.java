package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PhotoRepository;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoRepositoryTest;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MemoryPhotoRepositoryTest extends PhotoRepositoryTest {

    private static final Path PREFIX = Paths.get("test/path");

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return PREFIX.resolve("some/file.jpg");
            }

            @Override public Path getFile2() {
                return PREFIX.resolve("other/image.jpg");
            }
        };
    }

    @Override protected PhotoRepository getObject() {
        return new MemoryPhotoRepository(getContext().getSerializer(), PREFIX);
    }
}
