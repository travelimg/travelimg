package at.ac.tuwien.qse.sepm.dao.repo;

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
import at.ac.tuwien.qse.sepm.entities.PhotoMetadata;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for reading and writing photo metadata.
 */
public interface PhotoSerializer {

    /**
     * Reads a photo data from a stream.
     *
     * @param is stream containing a photo
     * @return metadata read from the stream
     * @throws DAOException failed to perform operation
     * @throws FormatException data in the stream is invalid
     */
    public PhotoMetadata read(InputStream is) throws DAOException;

    /**
     * Rewrites the meta data of a photo.
     *
     * The written data is readable with this serializer instance.
     *
     * @param is stream containing the original photo
     * @param os stream to which the updated photo should be written
     * @param metadata data that should be written
     * @throws DAOException failed to perform operation
     * @throws FormatException data in the input stream is invalid
     */
    public void update(InputStream is, OutputStream os, PhotoMetadata metadata) throws DAOException;
}
