package com.maxcim.mekh.cache;

import com.maxcim.mekh.cache.cache.ICache;
import com.maxcim.mekh.cache.strategy.IStrategyCache;
import com.maxcim.mekh.cache.strategy.IStrategyCache.CacheEntry;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Level cache realization. Synchronized
 *
 * @param <K>
 * @param <V>
 */
public class LevelCache<K extends Serializable, V extends Serializable> implements ILevelCache<K, V> {
    private List<IStrategyCache<K, V>> cacheLevels;

    public LevelCache(Collection<ICache<K, V>> caches, StrategyType strategyType) {
        if (caches == null || caches.isEmpty())
            throw new IllegalArgumentException("Caches is empty");

        this.cacheLevels = caches.stream()
                .map(cache -> wrapCacheByStrategy(cache, strategyType))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized V get(K key) throws IOException, ClassNotFoundException {
        V cachedValue = null;
        for (IStrategyCache<K, V> strategyCache : cacheLevels) {
            if (cachedValue == null) {
                cachedValue = strategyCache.get(key);
            }
        }
        return cachedValue;
    }

    /**
     * Method put new value in first cache level. If level is full, then old value extruded to next level
     * and so on. If all level is full, then return old value from last level.
     *
     * @param key   - cache key
     * @param value - cache value
     * @return extruded from all level value
     * @throws IOException
     * @throws IllegalStateException
     * @throws ClassNotFoundException
     */
    @Override
    public synchronized CacheEntry<K, V> put(K key, V value)
            throws IOException, IllegalStateException, ClassNotFoundException {
        boolean valueReplaced = replaceCachedValue(key, value);
        if (valueReplaced)
            return null;

        CacheEntry<K, V> extrudedEntry = null;
        Iterator<IStrategyCache<K, V>> cacheLevelIterator = cacheLevels.iterator();
        IStrategyCache<K, V> firstStrategyCache = cacheLevelIterator.next();
        extrudedEntry = firstStrategyCache.put(key, value);

        if (extrudedEntry != null) {
            while (extrudedEntry != null && cacheLevelIterator.hasNext()) {
                IStrategyCache<K, V> strategyCache = cacheLevelIterator.next();
                extrudedEntry = strategyCache.put(extrudedEntry.getKey(), extrudedEntry.getValue());
            }
        }

        return extrudedEntry;
    }

    /**
     * Method replaced value if cache contains
     *
     * @return cache contains value, and value was replaced
     */
    private boolean replaceCachedValue(K key, V value) throws IOException, ClassNotFoundException {
        IStrategyCache<K, V> containsValueCache = cacheLevels.stream()
                .filter(cache -> cache.contains(key)).findAny().orElse(null);
        if (containsValueCache != null) {
            containsValueCache.put(key, value);
        }
        return containsValueCache != null;
    }

    @Override
    public synchronized V remove(K key) throws IOException, ClassNotFoundException {
        V removedValue = null;
        for (IStrategyCache<K, V> strategyCache : cacheLevels) {
            if ((removedValue = strategyCache.remove(key)) != null)
                break;
        }
        return removedValue;
    }

    @Override
    public boolean contains(K key) {
        return cacheLevels.stream().anyMatch(strategyCache -> strategyCache.contains(key));
    }

    @Override
    public int size() {
        return cacheLevels.stream().mapToInt(IStrategyCache::size).sum();
    }

    @Override
    public synchronized void clear() throws IOException {
        for (IStrategyCache<K, V> strategyCache : cacheLevels) {
            strategyCache.clear();
        }
    }

    @Override
    public boolean isFull() {
        return cacheLevels.stream().allMatch(IStrategyCache::isFull);
    }
}
