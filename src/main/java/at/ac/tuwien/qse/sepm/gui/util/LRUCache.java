package at.ac.tuwien.qse.sepm.gui.util;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fixed sized associative cache using least recently used replacement strategy
 * @param <K> Type of the key
 * @param <V> Type of the value
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int size;

    public LRUCache(int size) {
        super(size, 0.75f, true);

        this.size = size;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > size;
    }
}
