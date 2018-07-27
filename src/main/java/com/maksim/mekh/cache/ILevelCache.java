package com.maksim.mekh.cache;

import com.maksim.mekh.cache.strategy.LFUStrategyCache;
import com.maksim.mekh.cache.cache.ICache;
import com.maksim.mekh.cache.strategy.IStrategyCache;
import com.maksim.mekh.cache.strategy.MRUStrategyCache;

import java.io.Serializable;

public interface ILevelCache<K extends Serializable, V extends Serializable> extends ICache<K, V> {
    enum StrategyType {
        LRU, MRU, LFU
    }

    default IStrategyCache<K, V> wrapCacheByStrategy(ICache<K, V> cache, StrategyType strategyType) {
        switch (strategyType) {
            case LRU:
                return new LFUStrategyCache<>(cache);
            case MRU:
                return new MRUStrategyCache<>(cache);
            default:
            case LFU:
                return new LFUStrategyCache<>(cache);
        }
    }
}
