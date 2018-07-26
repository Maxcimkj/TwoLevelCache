package com.maxcim.mekh.cache.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class InMemoryCache<K extends Serializable, V extends Serializable> implements ICache<K, V> {
    private int maxSize;
    private Map<K, V> cacheStorage;

    public InMemoryCache(int maxSize) {
        this.maxSize = maxSize;
        this.cacheStorage = new HashMap<>(maxSize);
    }

    @Override
    public V get(K key) {
        return cacheStorage.get(key);
    }

    @Override
    public Object put(K key, V value) throws IllegalStateException {
        if (value == null)
            return null;

        if (!cacheStorage.containsKey(key) && cacheStorage.size() >= maxSize)
            throw new IllegalStateException("Cache full exception. Cache max size : " + maxSize);

        cacheStorage.put(key, value);
        return null;
    }

    @Override
    public V remove(K key) {
        return cacheStorage.remove(key);
    }

    @Override
    public boolean contains(K key) {
        return cacheStorage.containsKey(key);
    }

    @Override
    public int size() {
        return cacheStorage.size();
    }

    @Override
    public void clear() {
        cacheStorage.clear();
    }

    @Override
    public boolean isFull() {
        return cacheStorage.size() >= maxSize;
    }
}
