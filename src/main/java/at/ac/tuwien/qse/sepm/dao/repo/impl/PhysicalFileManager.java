package at.ac.tuwien.qse.sepm.dao.repo.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.stream.Stream;

public class PhysicalFileManager implements FileManager {

    @Override public Stream<Path> list(Path directory) throws IOException {
        return Files.list(directory);
    }

    @Override public boolean isFile(Path path) {
        return Files.isRegularFile(path);
    }

    @Override public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    @Override public FileTime getLastModifiedTime(Path path) throws IOException {
        return Files.getLastModifiedTime(path);
    }

    @Override public void createDirectories(Path directory) throws IOException {
        Files.createDirectories(directory);
    }

    @Override public void createFile(Path file) throws IOException {
        Files.createFile(file);
    }

    @Override public OutputStream newOutputStream(Path file) throws IOException {
        return Files.newOutputStream(file);
    }

    @Override public InputStream newInputStream(Path file) throws IOException {
        return Files.newInputStream(file);
    }

    @Override public boolean exists(Path path) {
        return Files.exists(path);
    }

    @Override public void delete(Path file) throws IOException {
        Files.delete(file);
    }

    @Override public void copy(Path source, Path dest) throws IOException {
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
