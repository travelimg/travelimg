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

import java.io.*;
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
        return new BufferedOutputStream(Files.newOutputStream(file));
    }

    @Override public InputStream newInputStream(Path file) throws IOException {
        return new BufferedInputStream(Files.newInputStream(file));
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
