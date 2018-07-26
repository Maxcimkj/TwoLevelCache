package com.maxcim.mekh.cache;

import com.maxcim.mekh.cache.cache.InMemoryCache;
import com.maxcim.mekh.cache.strategy.IStrategyCache.CacheEntry;
import com.maxcim.mekh.cache.strategy.LFUStrategyCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LFUCacheStrategyTypeTest {
    private static final int maxSize = 3;
    private static LFUStrategyCache<Integer, TestObject> lfuStrategyCache;

    @Before
    public void initializeCache() throws Exception {
        lfuStrategyCache = new LFUStrategyCache<>(new InMemoryCache<>(maxSize));
    }

    @Test
    public void testCacheExtrudedValue() throws Exception {
        TestObject[] firstCachingObjects = new TestObject[]{
                new TestObject(1, "One"),
                new TestObject(2, "Two"),
                new TestObject(3, "Three")
        };
        TestObject lastCachingObject = new TestObject(4, "Four");

        // put objects to cache
        for (TestObject object : firstCachingObjects) {
            lfuStrategyCache.put(object.id, object);
        }
        // using cached objects
        lfuStrategyCache.get(3);
        lfuStrategyCache.get(3);
        lfuStrategyCache.get(2);
        lfuStrategyCache.get(2);
        // put to cache object over size
        CacheEntry<Integer, TestObject> extrudedCacheEntry = lfuStrategyCache.put(lastCachingObject.id, lastCachingObject);

        Assert.assertEquals(extrudedCacheEntry.getValue(), firstCachingObjects[0]);
    }

    @Test
    public void testCacheExtrudedValueWithNullScore() throws Exception {
        TestObject[] firstCachingObjects = new TestObject[]{
                new TestObject(1, "One"),
                new TestObject(2, "Two"),
                new TestObject(3, "Three")
        };
        TestObject lastCachingObject = new TestObject(4, "Four");

        // put objects to cache
        for (TestObject object : firstCachingObjects) {
            lfuStrategyCache.put(object.id, object);
        }
        // using cached objects
        lfuStrategyCache.get(2);
        lfuStrategyCache.get(3);
        // put to cache object over size
        CacheEntry<Integer, TestObject> extrudedCacheEntry = lfuStrategyCache.put(lastCachingObject.id, lastCachingObject);
        // assert that cache extrude seldom using object
        Assert.assertEquals(extrudedCacheEntry.getValue(), firstCachingObjects[0]);
    }

    @Test
    public void overflowTest() throws Exception {
        TestObject[] objects = TestObject.generate(maxSize * 2);
        // cache filling over max size
        for (TestObject object : objects) {
            lfuStrategyCache.put(object.id, object);
        }
        // no exception test
        Assert.assertTrue(true);
    }

    @After
    public void clearCache() throws Exception {
        lfuStrategyCache.clear();
    }
}
