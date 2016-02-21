package at.ac.tuwien.qse.sepm.gui.control;

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

import at.ac.tuwien.qse.sepm.gui.JavaFXThreadingRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TagPickerTest {

    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    private static final String TAG_A = "Architektur";
    private static final String TAG_B = "Landschaft";
    private static final String TAG_C = "Essen";
    
    private Set<String> set(String... tags) {
        return new HashSet<>(Arrays.asList(tags));
    }

    @Test
    public void getTags_someAdded_ReturnsAll() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_B));
        assertEquals(set(TAG_A, TAG_B), object.getTags());
    }

    @Test
    public void count_someAdded_returnsCorrect() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set());
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        assertEquals(2, object.count(TAG_A));
        assertEquals(1, object.count(TAG_B));
        assertEquals(0, object.count(TAG_C));
    }

    @Test
    public void hasTags_empty_returnsFalse() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set());
        object.getEntities().add(set());
        assertFalse(object.hasTags());
    }

    @Test
    public void contains_someAdded_returnsCorrect() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set());
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        assertTrue(object.contains(TAG_A));
        assertTrue(object.contains(TAG_B));
        assertFalse(object.contains(TAG_C));
    }

    @Test
    public void isApplied_someAdded_returnsCorrect() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        assertTrue(object.isApplied(TAG_A));
        assertFalse(object.isApplied(TAG_B));
        assertFalse(object.isApplied(TAG_C));
    }

    @Test
    public void isPartial_someAdded_returnsCorrect() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        assertFalse(object.isPartial(TAG_A));
        assertTrue(object.isPartial(TAG_B));
        assertFalse(object.isPartial(TAG_C));
    }

    @Test
    public void getApplied_someAdded_returnsCorrect() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        assertEquals(set(TAG_A), object.getApplied());
    }

    @Test
    public void getPartial_someAdded_returnsCorrect() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        assertEquals(set(TAG_B), object.getPartial());
    }

    @Test
    public void getTagsSorted_someAdded_returnsCorrect() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set());
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        assertEquals(TAG_A, object.getTagsSorted().get(0));
        assertEquals(TAG_B, object.getTagsSorted().get(1));
    }

    @Test
    public void filter_someAdded_returnsCorrect() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        assertEquals(set(TAG_A), object.filter(set(TAG_C)));
    }

    @Test
    public void apply_nonExistingTag_isApplied() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set());
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        object.apply(TAG_B);
        assertTrue(object.isApplied(TAG_B));
    }

    @Test
    public void apply_partialTag_isApplied() {
        TagPicker object = new TagPicker();
        object.getEntities().add(set());
        object.getEntities().add(set(TAG_A));
        object.getEntities().add(set(TAG_A, TAG_B));
        object.remove(TAG_A);
        assertTrue(!object.contains(TAG_A));
    }
}
