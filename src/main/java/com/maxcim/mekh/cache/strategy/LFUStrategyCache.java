package com.maxcim.mekh.cache.strategy;

import com.maxcim.mekh.cache.cache.ICache;

import java.io.Serializable;
import java.util.Comparator;

public class LFUStrategyCache<K extends Serializable, V extends Serializable>
        extends AbstractScoreExtrudingStrategyCache<K, V> {
    public LFUStrategyCache(ICache<K, V> cache) {
        super(cache, Comparator.reverseOrder());
    }

    /**
     * Method returned number of accesses to the object
     *
     * @param lastScore - last number of accesses
     * @param key       - cache key
     * @return number of accesses
     */
    @Override
    Long score(Long lastScore, K key) {
        return lastScore != null ? ++lastScore : 1;
    }
}
