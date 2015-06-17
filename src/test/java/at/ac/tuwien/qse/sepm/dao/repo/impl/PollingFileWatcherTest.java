package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.FileWatcher;
import at.ac.tuwien.qse.sepm.dao.repo.FileWatcherTest;

public class PollingFileWatcherTest extends FileWatcherTest {

    @Override protected FileWatcher getObject() {
        return new PollingFileWatcher(new MockFileManager());
    }
}
