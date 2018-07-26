package com.maxcim.mekh.cache;

import com.maxcim.mekh.cache.cache.InFileCache;
import com.maxcim.mekh.cache.strategy.IStrategyCache.CacheEntry;
import com.maxcim.mekh.cache.strategy.MRUStrategyCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MRUCacheStrategyTypeTest {
    private static final int maxSize = 3;
    private static MRUStrategyCache<Integer, TestObject> mruStrategyCache;

    @Before
    public void initializeCache() throws Exception {
        mruStrategyCache = new MRUStrategyCache<>(new InFileCache<>(maxSize));
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
            mruStrategyCache.put(object.id, object);
        }
        // using cached objects
        mruStrategyCache.get(1);
        mruStrategyCache.get(2);
        mruStrategyCache.get(3);
        // put to cache object over size
        CacheEntry<Integer, TestObject> extrudedCacheEntry = mruStrategyCache.put(lastCachingObject.id, lastCachingObject);

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
            mruStrategyCache.put(object.id, object);
        }
        // using cached objects
        mruStrategyCache.get(1);
        mruStrategyCache.get(3);
        // put to cache object over size
        CacheEntry<Integer, TestObject> extrudedCacheEntry = mruStrategyCache.put(lastCachingObject.id, lastCachingObject);

        Assert.assertEquals(extrudedCacheEntry.getValue(), firstCachingObjects[2]);
    }

    @Test
    public void overflowTest() throws Exception {
        TestObject[] objects = TestObject.generate(maxSize * 2);
        // cache filling over max size
        for (TestObject object : objects) {
            mruStrategyCache.put(object.id, object);
        }
        // no exception test
        Assert.assertTrue(true);
    }

    @After
    public void clearCache() throws Exception {
        mruStrategyCache.clear();
    }
}
