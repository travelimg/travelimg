package at.ac.tuwien.qse.sepm.service.impl;

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

import at.ac.tuwien.qse.sepm.entities.Photo;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PhotoPathFilterTest {

    @Test
    public void test_pathMatches() {
        PhotoPathFilter filter = new PhotoPathFilter();
        filter.setIncludedPath(Paths.get("/home/user/images"));

        assertThat(filter.test(new Photo(Paths.get("/home/user/images/"))), is(true));
        assertThat(filter.test(new Photo(Paths.get("/home/user/images/foobar.jpg"))), is(true));
        assertThat(filter.test(new Photo(Paths.get("/home/user/images/subdir/test.jpg"))), is(true));
        assertThat(filter.test(new Photo(Paths.get("/home/user/images/1/2/3/4/5/test.jpg"))), is(true));
    }

    @Test
    public void test_pathDoesNotMatch() {
        PhotoPathFilter filter = new PhotoPathFilter();
        filter.setIncludedPath(Paths.get("/home/user/images"));

        assertThat(filter.test(new Photo(Paths.get("/home/user/"))), is(false));
        assertThat(filter.test(new Photo(Paths.get("/home/user/image/foobar.jpg"))), is(false));
    }
}
