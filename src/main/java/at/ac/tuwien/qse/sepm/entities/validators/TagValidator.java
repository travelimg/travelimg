package at.ac.tuwien.qse.sepm.entities.validators;

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

import at.ac.tuwien.qse.sepm.entities.Tag;
import sun.security.validator.ValidatorException;

public class TagValidator {

    /**
     * Validate <tt>tag</tt>.
     *
     * @param tag must not be null; field <tt>tag.name</tt> must not be null and must contain
     *            something other than whitespaces;
     * @throws ValidatorException if any precondition is violated
     */
    public static void validate(Tag tag) throws ValidationException {
        if (tag == null) {
            throw new ValidationException("Entity must not be null");
        }
        if (tag.getName() == null) {
            throw new ValidationException("Name must not be null");
        }
        if ((tag.getName().isEmpty())) {
            throw new ValidationException("Name must not be empty");
        }
        if (tag.getName().trim().length() == 0) {
            throw new ValidationException("Name must not only contain Whitespaces");
        }
    }

    /**
     * Validates ID of Tag <tt>tag</tt>.
     *
     * @param tag must not be null;  <tt>tag.id</tt> must not be null and non-negative
     * @throws ValidationException if any precondition is violated
     */
    public static void validateID(Tag tag) throws ValidationException {
        if (tag == null) {
            throw new ValidationException("Entity must not be null");
        }
        if (tag.getId() == null) {
            throw new ValidationException("ID must not be null");
        }
        if (tag.getId() < 0) {
            throw new ValidationException("ID must be non-negative");
        }
    }
}
