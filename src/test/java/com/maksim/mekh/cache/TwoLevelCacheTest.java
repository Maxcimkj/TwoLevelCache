package com.maksim.mekh.cache;

import com.maksim.mekh.cache.cache.InFileCache;
import com.maksim.mekh.cache.cache.InMemoryCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TwoLevelCacheTest {
    private static final int firstLevelSize = 3;
    private static final int secondLevelSize = 3;
    private static final int cacheMaxSize = firstLevelSize + secondLevelSize;
    private static InMemoryCache<Integer, TestObject> firstLevelCache;
    private static InFileCache<Integer, TestObject> secondLevelCache;
    private static TwoLevelCache<Integer, TestObject> twoLevelCache;

    @Before
    public void initializeCache() throws Exception {
        firstLevelCache = new InMemoryCache<>(firstLevelSize);
        secondLevelCache = new InFileCache<>(secondLevelSize);
        twoLevelCache = new TwoLevelCache<>(firstLevelCache, secondLevelCache, ILevelCache.StrategyType.LRU);
    }

    @Test
    public void putGetTest() throws Exception {
        TestObject object = new TestObject(1, "one");
        // put object into cache
        twoLevelCache.put(object.id, object);
        // assert expected and actual object equals
        TestObject actualObject = twoLevelCache.get(object.id);
        Assert.assertEquals(object, actualObject);
    }

    @Test
    public void removeTest() throws Exception {
        TestObject object = new TestObject(1, "one");
        // put object into cache
        twoLevelCache.put(object.id, object);
        // remove object from cache
        TestObject removedObject = twoLevelCache.remove(object.id);
        // assert expected and actual object equals
        TestObject actualObject = twoLevelCache.get(object.id);
        Assert.assertNull(actualObject);
        Assert.assertEquals(object, removedObject);
    }

    @Test
    public void clearTest() throws Exception {
        TestObject[] objects = TestObject.generate(cacheMaxSize / 2);
        // put object to cache
        for (TestObject object : objects) {
            twoLevelCache.put(object.id, object);
        }
        // clear cache
        twoLevelCache.clear();
        // test cache empty
        Assert.assertTrue(twoLevelCache.size() == 0);
    }


    @Test
    public void isFullTest() throws Exception {
        TestObject[] objects = TestObject.generate(cacheMaxSize);
        // cache filling to max size
        for (TestObject object : objects) {
            twoLevelCache.put(object.id, object);
        }
        // cache full assert
        Assert.assertTrue(twoLevelCache.isFull());
    }

    @Test
    public void containsTest() throws Exception {
        TestObject object = new TestObject(1, "one");
        // put to cache
        twoLevelCache.put(object.id, object);
        // contains assert
        Assert.assertTrue(twoLevelCache.contains(object.id));
    }

    @Test
    public void cacheLevelFillingTest() throws Exception {
        TestObject[] objects = TestObject.generate(cacheMaxSize);
        // filling first level
        for (int i = 0; i < firstLevelSize; i++) {
            twoLevelCache.put(objects[i].id, objects[i]);
        }
        Assert.assertTrue(firstLevelCache.isFull());
        // filling second level
        for (int i = firstLevelSize; i < cacheMaxSize; i++) {
            twoLevelCache.put(objects[i].id, objects[i]);
        }
        Assert.assertTrue(secondLevelCache.isFull());
    }

    @Test
    public void overflowTest() throws Exception {
        TestObject[] objects = TestObject.generate(cacheMaxSize * 2);
        // cache filling over max size
        for (TestObject object : objects) {
            twoLevelCache.put(object.id, object);
        }
        // no exception test
        Assert.assertTrue(true);
    }

    @After
    public void clearCache() throws Exception {
        twoLevelCache.clear();
    }
}
