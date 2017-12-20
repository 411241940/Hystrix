package com.netflix.hystrix.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility to have 'intern' - like functionality, which holds single instance of wrapper for a given key
 */
public class InternMap<K, V> {
    private final ConcurrentMap<K, V> storage = new ConcurrentHashMap<K, V>();
    private final ValueConstructor<K, V> valueConstructor;

    public interface ValueConstructor<K, V> {
        V create(K key);
    }

    public InternMap(ValueConstructor<K, V> valueConstructor) {
        this.valueConstructor = valueConstructor;
    }

    public V interned(K key) {
        V existingKey = storage.get(key);
        V newKey = null;

        // 判断key对应的value是否存在
        if (existingKey == null) {
            // 不存在，则通过ValueConstructor实现类的create方法产生新的value,并存入storage中
            newKey = valueConstructor.create(key);
            existingKey = storage.putIfAbsent(key, newKey); // 保证方法原子性
        }
        return existingKey != null ? existingKey : newKey;
    }

    public int size() {
        return storage.size();
    }
}
