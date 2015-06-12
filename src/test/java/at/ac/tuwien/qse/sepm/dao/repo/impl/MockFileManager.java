package at.ac.tuwien.qse.sepm.dao.repo.impl;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class MockFileManager implements FileManager {

    private final HashSet<Path> directories = new HashSet<>();
    private final Map<Path, ByteArrayOutputStream> files = new HashMap<>();
    private final Map<Path, FileTime> modified = new HashMap<>();

    @Override public Stream<Path> list(Path directory) throws IOException {
        Set<Path> result = new HashSet<>();
        for (Path file : files.keySet()) {
            if (file.getParent().endsWith(directory)) {
                result.add(file);
            }
        }
        for (Path dir : directories) {
            if (dir.getParent().equals(dir)) {
                result.add(dir);
            }
        }
        return result.stream();
    }

    @Override public boolean isFile(Path path) {
        return files.containsKey(path);
    }

    @Override public boolean isDirectory(Path path) {
        if (isFile(path)) return false;

        for (Path dir : directories) {
            if (dir.startsWith(path)) {
                return true;
            }
        }
        for (Path file : files.keySet()) {
            if (file.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    @Override public FileTime getLastModifiedTime(Path path) throws IOException {
        if (!isFile(path)) throw new IOException();
        return modified.get(path);
    }

    @Override public void createDirectories(Path directory) throws IOException {
        directories.add(directory);
    }

    @Override public void createFile(Path file) throws IOException {
        if (isFile(file)) throw new IOException();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        files.put(file, output);
        modified.put(file, FileTime.from(Instant.now()));
    }

    @Override public OutputStream newOutputStream(Path file) throws IOException {
        if (!isFile(file)) throw new IOException();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        files.put(file, output);
        return output;
    }

    @Override public InputStream newInputStream(Path file) throws IOException {
        if (!isFile(file)) throw new IOException();
        return new ByteArrayInputStream(files.get(file).toByteArray());
    }

    @Override public boolean exists(Path path) {
        return isFile(path) || isDirectory(path);
    }

    @Override public void delete(Path file) throws IOException {
        if (!isFile(file)) throw new IOException();
        files.remove(file);
        modified.remove(file);
    }

    @Override public void copy(Path source, Path sink) throws IOException {
        if (!isFile(source)) throw new IOException();
        if (!isFile(sink)) throw new IOException();
        newOutputStream(sink).write(files.get(source).toByteArray());
        modified.put(sink, FileTime.from(Instant.now()));
    }
}
