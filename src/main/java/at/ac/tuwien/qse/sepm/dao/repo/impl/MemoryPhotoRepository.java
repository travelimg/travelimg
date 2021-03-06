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

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.repo.*;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.PhotoMetadata;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Photo repository that stores photo files in memory.
 */
public class MemoryPhotoRepository implements PhotoRepository {

    private static final Logger LOGGER = LogManager.getLogger();

    private final PhotoSerializer serializer;
    private final Path prefix;
    private final Map<Path, Object> files = new HashMap<>();
    private final Collection<Listener> listeners = new LinkedList<>();

    public MemoryPhotoRepository(PhotoSerializer serializer, Path prefix) {
        if (serializer == null) throw new IllegalArgumentException();
        if (prefix == null) throw new IllegalArgumentException();
        this.serializer = serializer;
        this.prefix = prefix;
    }

    public Path getPrefix() {
        return prefix;
    }

    @Override public boolean accepts(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("accepting {}", file);
        boolean result = file.startsWith(getPrefix());
        LOGGER.info("accepts {} is {}", file, result);
        return result;
    }

    @Override public void create(Path file, InputStream source) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        if (source == null) throw new IllegalArgumentException();
        LOGGER.debug("creating {}", file);

        if (!accepts(file)) {
            throw new DAOException("File is not accepted by this repository.");
        }
        if (contains(file)) {
            throw new PhotoAlreadyExistsException(this, file);
        }

        try {
            byte[] array = IOUtils.toByteArray(source);
            files.put(file, array);
            LOGGER.info("created {}", file);
            listeners.forEach(l -> l.onCreate(this, file));
        } catch (IOException ex) {
            throw new DAOException(ex);
        }
    }

    @Override public void update(Photo photo) throws DAOException {
        if (photo == null) throw new IllegalArgumentException();
        LOGGER.debug("updating {}", photo);

        Path file = photo.getFile();
        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        ByteArrayInputStream is = new ByteArrayInputStream((byte[])files.get(file));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        serializer.update(is, os, photo.getData());
        files.put(file, os.toByteArray());
        LOGGER.info("updated {}", photo);
        listeners.forEach(l -> l.onUpdate(this, file));
    }

    @Override public void delete(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("deleting {}", file);

        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        files.remove(file);
        LOGGER.info("deleted {}", file);
        listeners.forEach(l -> l.onDelete(this, file));
    }

    @Override public void addListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.add(listener);
        LOGGER.info("added listener {}", listener);
    }

    @Override public void removeListener(Listener listener) {
        if (listener == null) throw new IllegalArgumentException();
        listeners.remove(listener);
        LOGGER.info("removed listener {}", listener);
    }

    @Override public Collection<Path> index() throws DAOException {
        LOGGER.debug("indexing");
        Collection<Path> result = files.keySet();
        LOGGER.info("indexed {}", result.size());
        return result;
    }

    @Override public boolean contains(Path file) throws DAOException {
        return files.containsKey(file);
    }

    @Override public Photo read(Path file) throws DAOException {
        if (file == null) throw new IllegalArgumentException();
        LOGGER.debug("reading {}", file);

        if (!contains(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        InputStream stream = new ByteArrayInputStream((byte[])files.get(file));
        PhotoMetadata metadata = serializer.read(stream);
        Photo photo = new Photo(file, metadata);
        LOGGER.info("read {}", photo);
        return photo;
    }
}
