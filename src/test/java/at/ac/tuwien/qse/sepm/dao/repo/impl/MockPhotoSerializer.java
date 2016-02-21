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
import at.ac.tuwien.qse.sepm.entities.PhotoMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MockPhotoSerializer implements PhotoSerializer {

    private final Map<Integer, PhotoMetadata> dataByIndex = new HashMap<>();
    private final Map<PhotoMetadata, Integer> indicesByData = new HashMap<>();

    public void put(int index, PhotoMetadata photo) {
        dataByIndex.put(index, photo);
        indicesByData.put(photo, index);
    }

    @Override public PhotoMetadata read(InputStream is) throws DAOException {
        try {
            int index = is.read();
            if (!dataByIndex.containsKey(index)) {
                throw new FormatException();
            }
            return dataByIndex.get(index);
        } catch (IOException ex) {
            throw new DAOException();
        }
    }

    @Override public void update(InputStream is, OutputStream os, PhotoMetadata metadata) throws DAOException {
        try {
            if (!indicesByData.containsKey(metadata)) {
                throw new FormatException();
            }
            int index = indicesByData.get(metadata);
            os.write(index);
        } catch (IOException ex) {
            throw new DAOException();
        }
    }
}
