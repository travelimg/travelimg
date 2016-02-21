package at.ac.tuwien.qse.sepm.dao.repo.impl;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class MockFileManager implements FileManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private final HashSet<Path> directories = new HashSet<>();
    private final Map<Path, ByteArrayOutputStream> files = new HashMap<>();
    private final Map<Path, FileTime> modified = new HashMap<>();

    @Override public Stream<Path> list(Path directory) throws IOException {
        LOGGER.debug("listing {}", directory);
        Set<Path> result = new HashSet<>();
        for (Path file : files.keySet()) {
            if (file.getParent().equals(directory)) {
                result.add(file);
            }
        }
        for (Path dir : directories) {
            if (dir.getParent().equals(dir)) {
                result.add(dir);
            }
        }
        LOGGER.debug("listed {} entries in {}", result.size(), directory);
        return result.stream();
    }

    @Override public boolean isFile(Path path) {
        LOGGER.debug("check is file {}", path);
        return files.containsKey(path);
    }

    @Override public boolean isDirectory(Path path) {
        LOGGER.debug("check is directory {}", path);
        if (isFile(path)) {
            LOGGER.debug("path is actually file {}", path);
            return false;
        }

        for (Path dir : directories) {
            if (dir.startsWith(path)) {
                LOGGER.debug("path is directory {}", path);
                return true;
            }
        }
        for (Path file : files.keySet()) {
            if (file.startsWith(path)) {
                LOGGER.debug("path is directory {}", path);
                return true;
            }
        }
        LOGGER.debug("path is not directory {}", path);
        return false;
    }

    @Override public FileTime getLastModifiedTime(Path file) throws IOException {
        LOGGER.debug("getting last modification time for file {}", file);
        if (!isFile(file)) {
            LOGGER.warn("path is not a file {}", file);
            throw new IOException();
        }
        FileTime result = modified.get(file);
        LOGGER.warn("got last modification time {} for file {}", result, file);
        return result;
    }

    @Override public void createDirectories(Path path) throws IOException {
        LOGGER.debug("creating directories for {}", path);
        directories.add(path.getParent());
    }

    @Override public void createFile(Path file) throws IOException {
        LOGGER.debug("creating file {}", file);
        if (exists(file)) {
            LOGGER.warn("found existing file or directory at {}", file);
            throw new IOException();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        files.put(file, output);
        modified.put(file, FileTime.from(Instant.now()));
        LOGGER.debug("at {} created file {}", modified.get(file), file);
    }

    @Override public OutputStream newOutputStream(Path file) throws IOException {
        LOGGER.debug("creating new output stream for {}", file);
        if (!isFile(file)) {
            LOGGER.warn("no file at {}", file);
            throw new IOException();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        files.put(file, output);
        LOGGER.debug("created new output stream for {}", file);
        return output;
    }

    @Override public InputStream newInputStream(Path file) throws IOException {
        LOGGER.debug("creating new input stream for {}", file);
        if (!isFile(file)) {
            LOGGER.warn("no file at {}", file);
            throw new IOException();
        }
        LOGGER.debug("created new input stream of {} bytes for file {}", files.get(file).size(), file);
        return new ByteArrayInputStream(files.get(file).toByteArray());
    }

    @Override public boolean exists(Path path) {
        LOGGER.debug("checking exists {}", path);
        boolean result = isFile(path) || isDirectory(path);
        LOGGER.debug("checking exists is {} for {}", result, path);
        return result;
    }

    @Override public void delete(Path file) throws IOException {
        LOGGER.debug("deleting file {}", file);
        if (!isFile(file)) {
            LOGGER.warn("not file at {}", file);
            throw new IOException();
        }
        files.remove(file);
        modified.remove(file);
        LOGGER.debug("deleted file {}", file);
    }

    @Override public void copy(Path source, Path sink) throws IOException {
        LOGGER.debug("copying {} bytes from {} -> {}",  sizeOf(source), source, sink);
        if (!isFile(source)) {
            LOGGER.warn("no source file found at {}", source);
            throw new IOException();
        }
        if (!isFile(sink)) {
            LOGGER.warn("no destination file found at {}", sink);
            throw new IOException();
        }
        newOutputStream(sink).write(files.get(source).toByteArray());
        modified.put(sink, FileTime.from(Instant.now()));
        LOGGER.debug("copied {} bytes from {} -> {}", sizeOf(sink), source, sink);
    }

    private int sizeOf(Path file) {
        return files.get(file).size();
    }
}
