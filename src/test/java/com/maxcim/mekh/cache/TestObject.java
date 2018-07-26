package com.maxcim.mekh.cache;

import java.io.Serializable;
import java.util.Objects;

public class TestObject implements Serializable {
    int id;
    String content;

    public TestObject(int id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestObject that = (TestObject) o;
        return id == that.id &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    static TestObject[] generate(int count) {
        TestObject[] testObjects = new TestObject[count];
        for (int i = 0; i < count; i++) {
            testObjects[i] = new TestObject(i, "content_" + i);
        }
        return testObjects;
    }
}
