package com.maxcim.mekh.cache.strategy;

import com.maxcim.mekh.cache.cache.ICache;

import java.io.IOException;
import java.io.Serializable;

/**
 * Cached strategy interface released wrapper pattern.
 *
 * @param <K> - cache key type
 * @param <V> - value type
 */
public interface IStrategyCache<K extends Serializable, V extends Serializable> extends ICache<K, V> {
    /**
     * Method put value in cache by certain strategy.
     * if cache is full then the previously cached object is extruded.
     *
     * @param key   - cache key
     * @param value - cache value
     * @return extruded value if the cache is full or null
     * @throws IOException,ClassNotFoundException - exception read/ write data in cache
     */
    @Override
    CacheEntry<K, V> put(K key, V value) throws IOException, ClassNotFoundException;

    /**
     * Cache entry class
     *
     * @param <K> - cache key
     * @param <V> - cache value
     */
    class CacheEntry<K extends Serializable, V extends Serializable> {
        private K key;
        private V value;

        public CacheEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
