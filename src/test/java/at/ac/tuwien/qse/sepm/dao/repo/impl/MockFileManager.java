package at.ac.tuwien.qse.sepm.dao.repo.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MockFileManager implements PollingFileWatcher.FileManager {

    private final Map<Path, FileTime> files = new HashMap<>();

    public void create(Path file) {
        files.put(file, FileTime.from(Instant.now()));
    }

    public void modify(Path file) {
        files.put(file, FileTime.from(Instant.now()));
    }

    public void delete(Path file) {
        files.remove(file);
    }

    @Override public Stream<Path> list(Path directory) {
        return files.keySet().stream().filter(file -> file.getParent().equals(directory));
    }

    @Override public boolean isFile(Path path) {
        return files.containsKey(path);
    }

    @Override public boolean isDirectory(Path path) {
        return list(path).count() > 0; // path is parent to at least one file
    }

    @Override public FileTime getLastModifiedTime(Path path) throws IOException {
        return null;
    }
}
