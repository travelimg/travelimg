package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.PhotoCache;
import at.ac.tuwien.qse.sepm.dao.repo.PhotoCacheTest;

public class MemoryPhotoCacheTest extends PhotoCacheTest{

    @Override protected PhotoCache getObject() {
        return new MemoryPhotoCache();
    }
}
