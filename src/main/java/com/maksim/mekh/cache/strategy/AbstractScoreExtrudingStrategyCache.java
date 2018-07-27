package com.maksim.mekh.cache.strategy;

import com.maksim.mekh.cache.cache.ICache;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract cache wrapper class for extruding strategy realization.
 * Realized extruding value from cache by calculated object score.
 * Not synchronized.
 *
 * @param <K> - key
 * @param <V> - value
 */
public abstract class AbstractScoreExtrudingStrategyCache<K extends Serializable, V extends Serializable>
        implements IStrategyCache<K, V> {
    private Map<K, Long> scoreStorage;
    private TreeMap<K, Long> sortedScoreStorage;
    private Comparator<Long> scoreComparator;

    protected ICache<K, V> cache;

    @SuppressWarnings("unchecked")
    public AbstractScoreExtrudingStrategyCache(ICache<K, V> cache) {
        this(cache, Comparator.naturalOrder());
    }

    @SuppressWarnings("unchecked")
    public AbstractScoreExtrudingStrategyCache(ICache<K, V> cache, Comparator<Long> scoreComparator) {
        this.cache = cache;
        this.scoreStorage = new HashMap<>();
        this.sortedScoreStorage = new TreeMap<>(new ValueMapComparator(scoreStorage));
        this.scoreComparator = scoreComparator;
    }

    @Override
    public CacheEntry<K, V> put(K key, V value) throws IOException, ClassNotFoundException {
        if (value == null)
            return null;

        V extrudedValue = null;
        K extrudedValueKey = null;
        // extruded cached value by strategy, if cache is full
        if (!cache.contains(key) && cache.isFull()) {
            sortedScoreStorage.clear();
            sortedScoreStorage.putAll(scoreStorage);

            extrudedValueKey = sortedScoreStorage.lastKey();
            scoreStorage.remove(extrudedValueKey);
            extrudedValue = cache.remove(extrudedValueKey);
        }
        // put new object to empty place
        scoreStorage.put(key, 0L);
        cache.put(key, value);

        return extrudedValueKey != null && extrudedValue != null ?
                new CacheEntry<>(extrudedValueKey, extrudedValue) : null;
    }

    @Override
    public V get(K key) throws IOException, ClassNotFoundException {
        Long lastScore = scoreStorage.get(key);
        Long score = score(lastScore, key);
        scoreStorage.put(key, score);
        return cache.get(key);
    }

    @Override
    public V remove(K key) throws IOException, ClassNotFoundException {
        scoreStorage.remove(key);
        return cache.remove(key);
    }

    @Override
    public boolean contains(K key) {
        return cache.contains(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() throws IOException {
        scoreStorage.clear();
        cache.clear();
    }

    @Override
    public boolean isFull() {
        return cache.isFull();
    }

    abstract Long score(Long lastScore, K key);

    @SuppressWarnings("unchecked")
    private class ValueMapComparator implements Comparator<K> {
        private Map<K, Long> map;

        public ValueMapComparator(Map<K, Long> map) {
            this.map = map;
        }

        @Override
        public int compare(K key1, K key2) {
            Long score1 = map.get(key1);
            Long score2 = map.get(key2);

            return scoreComparator.compare(score1, score2);
        }
    }
}
