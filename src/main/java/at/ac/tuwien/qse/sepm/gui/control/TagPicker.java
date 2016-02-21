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

import at.ac.tuwien.qse.sepm.gui.control.skin.TagPickerSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GUI control for displaying, applying or removing tags from one or more tagged entities.
 *
 * Tagged entities are represented only by their tag sets. Tags are either applied, if all entities
 * contain the tag, or partial, if only some entities contain the tag.
 */
public class TagPicker extends Control {

    private ObservableList<Set<String>> entities = FXCollections.observableArrayList();
    private Runnable onUpdate;

    public TagPicker() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    /**
     * Get the list of tagged entities represented by their tags.
     *
     * @return observable list of tag sets
     */
    public ObservableList<Set<String>> getEntities() {
        return entities;
    }

    /**
     * Set action invoked when tags are applied or removed.
     *
     * @param onUpdate action that is invoked
     */
    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }
    public Runnable getOnUpdate() {
        return onUpdate;
    }

    /**
     * Get all tags.
     *
     * @return distinct collection of tags
     */
    public Set<String> getTags() {
        return getEntities().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Count the sets in which the tag occurs.
     *
     * @param tag tag that should be checked
     * @return number of sets that contain the tag
     */
    public int count(String tag) {
        if (tag == null) throw new IllegalArgumentException();
        int count = 0;
        for (Set<String> set : entities) {
            if (set.contains(tag)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get a value indicating that there is at least one tag.
     *
     * @return true if at least one entity is tagged, otherwise false
     */
    public boolean hasTags() {
        return !getTags().isEmpty();
    }

    /**
     * Check whether at least one entity contains the tag.
     *
     * @param tag tag that should be checked
     * @return true if any entity contains the tag, otherwise false
     */
    public boolean contains(String tag) {
        if (tag == null) throw new IllegalArgumentException();
        return getTags().contains(tag);
    }

    /**
     * Get a value indicating that all entities contain the tag.
     *
     * @param tag tag that should be checked
     * @return true if all entities contain the tag, otherwise false
     */
    public boolean isApplied(String tag) {
        if (tag == null) throw new IllegalArgumentException();
        return count(tag) == entities.size();
    }

    /**
     * Get a value indicating that at least one but not all entities contain the tag.
     *
     * @param tag tag that should be checked
     * @return true if some entities contain the tag, else false
     */
    public boolean isPartial(String tag) {
        if (tag == null) throw new IllegalArgumentException();
        return contains(tag) && !isApplied(tag);
    }

    /**
     * Get all applied tags.
     *
     * @return set of tags
     */
    public Set<String> getApplied() {
        return getTags().stream()
                .filter(this::isApplied)
                .collect(Collectors.toSet());
    }

    /**
     * Get all partial tags.
     *
     * @return set of tags
     */
    public Set<String> getPartial() {
        return getTags().stream()
                .filter(this::isPartial)
                .collect(Collectors.toSet());
    }

    /**
     * Get all tags sorted by their count in descending order.
     *
     * @return list of tags, in which tags that are used often come first
     */
    public List<String> getTagsSorted() {
        return getTags().stream()
                .sorted((a, b) -> count(b) - count(a))
                .collect(Collectors.toList());
    }

    /**
     * Adds the tag to all entities.
     *
     * @param tag tag that should be applied
     */
    public void apply(String tag) {
        if (tag == null) throw new IllegalArgumentException();
        getEntities().forEach(s -> s.add(tag));
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    /**
     * Removes the tag from all entities.
     *
     * @param tag tag that should be removed
     */
    public void remove(String tag) {
        if (tag == null) throw new IllegalArgumentException();
        getEntities().forEach(s -> s.remove(tag));
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    /**
     * Adds all applied tags to the set and removes tags that are not in the picker.
     *
     * @param tags set of tags that should be updated
     */
    public Set<String> filter(Set<String> tags) {
        if (tags == null) throw new IllegalArgumentException();
        Set<String> newTags = new HashSet<>(tags);
        newTags.addAll(getApplied());
        newTags.removeIf(tag -> !contains(tag));
        return newTags;
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new TagPickerSkin(this);
    }

    private static final String DEFAULT_STYLE_CLASS = "tag-picker";
}
