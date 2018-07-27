package com.maksim.mekh.cache.cache;

import com.maksim.mekh.cache.strategy.IStrategyCache;

import java.io.IOException;
import java.io.Serializable;

/**
 * Cache base interface
 *
 * @param <K> - key type
 * @param <V> - value type
 */
public interface ICache<K extends Serializable, V extends Serializable> {
    /**
     * Return cache value
     *
     * @param key - cache value key
     * @return cached object or null, if object don't find
     */
    V get(K key) throws IOException, ClassNotFoundException;

    /**
     * Put value in cache with key
     *
     * @param key   - cache key
     * @param value - cache value
     * @throws IllegalStateException  - call exception, if cache full
     */
    /**
     * Put value in cache with key
     *
     * @param key   - cache key
     * @param value - cache value
     * @throws IllegalStateException  - call exception, if cache full
     * @throws IOException - exception read/ write data in cache
     * @return always null for cache.
     * Returned value necessary for {@link IStrategyCache} realization
     */
    Object put(K key, V value) throws IllegalStateException, IOException, ClassNotFoundException;

    /**
     * Remove value from cache
     *
     * @param key - cache key
     * @return removed value - if exists,
     * or null - if don't find
     */
    V remove(K key) throws IOException, ClassNotFoundException;;

    /**
     * Check cached value exists
     *
     * @param key - cache key
     * @return false/ true - if exists or not exists
     */
    boolean contains(K key);

    /**
     * Calc count cached values
     *
     * @return count cached values
     */
    int size();

    /**
     * Remove all cached values
     */
    void clear() throws IOException;

    /**
     * Check cache fullness
     *
     * @return true/ false - if cache fulled or not
     */
    boolean isFull();
}
