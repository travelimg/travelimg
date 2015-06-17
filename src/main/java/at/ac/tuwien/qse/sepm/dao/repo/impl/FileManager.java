package at.ac.tuwien.qse.sepm.dao.repo.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.stream.Stream;

public interface FileManager {

    Stream<Path> list(Path directory) throws IOException;
    boolean isFile(Path path);
    boolean isDirectory(Path path);
    FileTime getLastModifiedTime(Path path) throws IOException;
    void createDirectories(Path directory) throws IOException;
    void createFile(Path file) throws IOException;
    OutputStream newOutputStream(Path file) throws IOException;
    InputStream newInputStream(Path file) throws IOException;
    boolean exists(Path path);
    void delete(Path file) throws IOException;
    void copy(Path source, Path dest) throws IOException;
}
