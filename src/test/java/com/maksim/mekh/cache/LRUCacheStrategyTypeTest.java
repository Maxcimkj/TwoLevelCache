package com.maksim.mekh.cache;

import com.maksim.mekh.cache.cache.InMemoryCache;
import com.maksim.mekh.cache.strategy.IStrategyCache.CacheEntry;
import com.maksim.mekh.cache.strategy.LRUStrategyCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LRUCacheStrategyTypeTest {
    private static final int maxSize = 3;
    private static LRUStrategyCache<Integer, TestObject> lruStrategyCache;

    @Before
    public void initializeCache() throws Exception {
        lruStrategyCache = new LRUStrategyCache<>(new InMemoryCache<>(maxSize));
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
            lruStrategyCache.put(object.id, object);
        }
        // using cached objects
        lruStrategyCache.get(3);
        lruStrategyCache.get(1);
        lruStrategyCache.get(2);
        // put to cache object over size
        CacheEntry<Integer, TestObject> extrudedCacheEntry = lruStrategyCache.put(lastCachingObject.id, lastCachingObject);
        // assert that cache extruded last update object
        Assert.assertEquals(extrudedCacheEntry.getValue(), firstCachingObjects[2]);
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
            lruStrategyCache.put(object.id, object);
        }
        // get objects
        lruStrategyCache.get(2);
        lruStrategyCache.get(3);
        // put to cache object over size
        CacheEntry<Integer, TestObject> extrudedCacheEntry = lruStrategyCache.put(lastCachingObject.id, lastCachingObject);
        // assert that cache extruded unused object
        Assert.assertEquals(extrudedCacheEntry.getValue(), firstCachingObjects[0]);
    }

    @Test
    public void overflowTest() throws Exception {
        TestObject[] objects = TestObject.generate(maxSize * 2);
        // cache filling over max size
        for (TestObject object : objects) {
            lruStrategyCache.put(object.id, object);
        }
        // no exception test
        Assert.assertTrue(true);
    }

    @After
    public void clearCache() throws Exception {
        lruStrategyCache.clear();
    }
}
