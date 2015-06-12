package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCacheTest;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JdbcPhotoCacheTest extends PhotoCacheTest {

    @Override protected PhotoCache getObject() {
        return new JdbcPhotoCache();
    }

    @Override protected Context getContext() {
        return new Context() {
            @Override public Path getFile1() {
                return Paths.get("test/1.jpg");
            }

            @Override public Path getFile2() {
                return Paths.get("test/1.jpg");
            }
        };
    }
}