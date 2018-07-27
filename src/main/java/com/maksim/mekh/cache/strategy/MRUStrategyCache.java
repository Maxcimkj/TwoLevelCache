package com.maksim.mekh.cache.strategy;

import com.maksim.mekh.cache.cache.ICache;

import java.io.Serializable;

public class MRUStrategyCache<K extends Serializable, V extends Serializable>
        extends AbstractScoreExtrudingStrategyCache<K, V> {
    public MRUStrategyCache(ICache<K, V> cache) {
        super(cache);
    }

    /**
     * Method return value access time
     *
     * @param lastScore - last access time
     * @param key       - cache key
     * @return access time
     */
    @Override
    Long score(Long lastScore, K key) {
        return System.nanoTime();
    }
}
