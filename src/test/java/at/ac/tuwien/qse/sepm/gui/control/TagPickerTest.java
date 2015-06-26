package at.ac.tuwien.qse.sepm.gui.control;

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
