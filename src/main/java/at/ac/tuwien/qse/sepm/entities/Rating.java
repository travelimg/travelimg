package at.ac.tuwien.qse.sepm.entities;

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

public enum Rating {

    /**
     * Photo has not yet been rated.
     */
    NONE,

    /**
     * Photo is of low quality.
     */
    BAD,

    /**
     * Photo was rated and is acceptable.
     */
    NEUTRAL,

    /**
     * Photo is of high quality.
     */
    GOOD;

    /**
     * Converts an integer to a PhotoRating, by interpreting it as the ordinal number of the enum
     * value. If the integer exceeds the range of the ordinals the return value defaults to
     * PhotoRating.NONE.
     *
     * @param index
     * @return enum value
     */
    public static Rating from(int index) {
        if (index < 0 || Rating.values().length <= index) {
            return Rating.NONE;
        }
        return Rating.values()[index];
    }
}
