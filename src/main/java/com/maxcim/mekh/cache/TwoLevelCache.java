package com.maxcim.mekh.cache;

import com.maxcim.mekh.cache.cache.ICache;

import java.io.Serializable;
import java.util.Arrays;

public class TwoLevelCache<K extends Serializable, V extends Serializable> extends LevelCache<K, V> {
    public TwoLevelCache(ICache<K, V> firstLevelCache, ICache<K, V> secondLevelCache, StrategyType strategyType) {
        super(Arrays.asList(firstLevelCache, secondLevelCache), strategyType);
    }
}
