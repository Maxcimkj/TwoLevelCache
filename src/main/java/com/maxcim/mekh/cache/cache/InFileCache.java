package com.maxcim.mekh.cache.cache;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class InFileCache<K extends Serializable, V extends Serializable> implements ICache<K, V> {
    private static final String CACHE_STORAGE_DIR = "cache";

    private int maxSize;
    private Map<K, Path> cacheFilePathStorage;
    private Path cacheStorage;

    public InFileCache(int maxSize) throws IOException {
        this.maxSize = maxSize;
        this.cacheFilePathStorage = new HashMap<>(maxSize);
        this.cacheStorage = Files.createTempDirectory(CACHE_STORAGE_DIR);
    }

    @Override
    public V get(K key) throws IOException, ClassNotFoundException {
        V cachedValue = null;

        Path cachedFilePath = cacheFilePathStorage.get(key);
        if (cachedFilePath != null) {
            ObjectInputStream objectStream = null;
            try {
                objectStream = new ObjectInputStream(new FileInputStream(cachedFilePath.toFile()));
                cachedValue = (V) objectStream.readObject();

            } finally {
                if (objectStream != null)
                    objectStream.close();
            }
        }
        return cachedValue;
    }

    @Override
    public Object put(K key, V value) throws IllegalStateException, IOException {
        if (value == null)
            return null;

        if (!cacheFilePathStorage.containsKey(key) && cacheFilePathStorage.size() >= maxSize)
            throw new IllegalStateException("Cache full exception. Cache max size : " + maxSize);

        Path cacheFile = Files.createTempFile(cacheStorage, "", "");

        ObjectOutputStream objectStream = null;
        try {
            objectStream = new ObjectOutputStream(new FileOutputStream(cacheFile.toFile()));
            objectStream.writeObject(value);
            objectStream.flush();
            cacheFilePathStorage.put(key, cacheFile);
        } finally {
            if (objectStream != null)
                objectStream.close();
        }

        return null;
    }

    @Override
    public V remove(K key) throws ClassNotFoundException, IOException {
        V cachedValue = null;

        Path cachedFilePath = cacheFilePathStorage.remove(key);
        if (cachedFilePath != null) {
            ObjectInputStream objectStream = null;
            try {
                File cachedFile = cachedFilePath.toFile();

                objectStream = new ObjectInputStream(new FileInputStream(cachedFile));
                cachedValue = (V) objectStream.readObject();

                cachedFile.delete();
            } finally {
                if (objectStream != null)
                    objectStream.close();
            }
        }
        return cachedValue;
    }

    @Override
    public boolean contains(K key) {
        return cacheFilePathStorage.containsKey(key);
    }

    @Override
    public int size() {
        return cacheFilePathStorage.size();
    }

    @Override
    public void clear() {
        cacheFilePathStorage.values()
                .forEach(filePath -> filePath.toFile().delete());
        cacheFilePathStorage.clear();
    }

    @Override
    public boolean isFull() {
        return cacheFilePathStorage.size() >= maxSize;
    }
}
