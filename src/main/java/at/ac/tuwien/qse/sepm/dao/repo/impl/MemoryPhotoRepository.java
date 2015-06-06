package at.ac.tuwien.qse.sepm.dao.repo.impl;

import at.ac.tuwien.qse.sepm.dao.repo.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Photo repository that stores photo files in memory.
 */
public class MemoryPhotoRepository extends PhotoRepositoryBase {

    private final Path basePath;
    private final Map<Path, Object> files = new HashMap<>();
    private final Map<Path, LocalDateTime> modified = new HashMap<>();

    public MemoryPhotoRepository(Path basePath) {
        super(LogManager.getLogger());
        if (basePath == null) throw new IllegalArgumentException();
        this.basePath = basePath;
    }

    public Path getBasePath() {
        return basePath;
    }

    @Override protected PhotoInfo checkImpl(Path file) throws PersistenceException {
        if (!files.containsKey(file)) {
            return null;
        }

        LocalDateTime date = LocalDateTime.from(modified.get(file));
        return new PhotoInfo(file, date);
    }

    @Override protected Collection<PhotoInfo> checkAllImpl() throws PersistenceException {
        Collection<PhotoInfo> result = new ArrayList<>(files.size());
        for (Path file : files.keySet()) {
            result.add(check(file));
        }
        return result;
    }

    @Override protected Photo readImpl(Path file) throws PersistenceException {
        if (!files.containsKey(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        InputStream stream = new ByteArrayInputStream((byte[])files.get(file));
        try {
            return read(file, stream);
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override protected Collection<Photo> readAllImpl() throws PersistenceException {
        Collection<Photo> result = new ArrayList<>(files.size());
        for (Path file : files.keySet()) {
            result.add(read(file));
        }
        return result;
    }

    @Override protected void createImpl(Path file, InputStream source) throws PersistenceException {
        if (!file.startsWith(getBasePath())) {
            throw new PhotoNotFoundException(this, file);
        }
        if (files.containsKey(file)) {
            throw new PhotoAlreadyExistsException(this, file);
        }

        try {
            byte[] array = IOUtils.toByteArray(source);
            files.put(file, array);
            modified.put(file, LocalDateTime.now());
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override protected void updateImpl(Photo photo) throws PersistenceException {
        Path file = photo.getFile();
        if (!files.containsKey(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            update(photo, stream);
        } catch (IOException ex) {
            throw new PersistenceException(ex);
        }
        files.put(file, stream.toByteArray());
        modified.put(file, LocalDateTime.now());
    }

    @Override protected void deleteImpl(Path file) throws PersistenceException {
        if (!files.containsKey(file)) {
            throw new PhotoNotFoundException(this, file);
        }

        files.remove(file);
        modified.remove(file);
    }

    protected Photo read(Path file, InputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void update(Photo photo, OutputStream stream) throws IOException {
        throw new UnsupportedOperationException();
    }
}
