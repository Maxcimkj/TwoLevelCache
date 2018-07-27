package com.maksim.mekh.cache;

import com.maksim.mekh.cache.cache.InFileCache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InFileCacheTest {
    private static int maxSize = 10;
    private InFileCache<Integer, TestObject> inFileCache;

    @Before
    public void initializeCaches() throws Exception {
        inFileCache = new InFileCache<>(maxSize);
    }

    @Test
    public void putGetTest() throws Exception {
        TestObject object = new TestObject(1, "one");
        // put object into cache
        inFileCache.put(object.id, object);
        // assert expected and actual object equals
        TestObject actualObject = inFileCache.get(object.id);
        Assert.assertEquals(object, actualObject);
    }

    @Test
    public void removeTest() throws Exception {
        TestObject object = new TestObject(1, "one");
        // put object into cache
        inFileCache.put(object.id, object);
        // remove object from cache
        TestObject removedObject = inFileCache.remove(object.id);
        // assert expected and actual object equals
        TestObject actualObject = inFileCache.get(object.id);
        Assert.assertNull(actualObject);
        Assert.assertEquals(object, removedObject);
    }

    @Test
    public void clearTest() throws Exception {
        TestObject[] objects = TestObject.generate(maxSize / 2);
        // put object to cache
        for (TestObject object : objects) {
            inFileCache.put(object.id, object);
        }
        // clear cache
        inFileCache.clear();
        // test cache empty
        Assert.assertTrue(inFileCache.size() == 0);
    }

    @Test(expected = IllegalStateException.class)
    public void overflowTest() throws Exception {
        TestObject[] objects = TestObject.generate(maxSize + 1);
        // cache filling over max size
        for (TestObject object : objects) {
            inFileCache.put(object.id, object);
        }
    }

    @Test
    public void isFullTest() throws Exception {
        TestObject[] objects = TestObject.generate(maxSize);
        // cache filling to max size
        for (TestObject object : objects) {
            inFileCache.put(object.id, object);
        }
        // cache full assert
        Assert.assertTrue(inFileCache.isFull());
    }

    @Test
    public void containsTest() throws Exception {
        TestObject object = new TestObject(1, "one");
        // put to cache
        inFileCache.put(object.id, object);
        // contains assert
        Assert.assertTrue(inFileCache.contains(object.id));
    }

    @After
    public void clearCache() throws Exception {
        inFileCache.clear();
    }
}