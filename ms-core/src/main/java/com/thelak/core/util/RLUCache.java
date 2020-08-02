package com.thelak.core.util;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;

import java.util.ArrayList;

public class RLUCache<K, V> implements Cache<K, V> {

    private long timeToLive;
    private final LRUMap cacheMap;


    protected class CacheObject {
        private long lastAccessed = System.currentTimeMillis();
        private V value;

        CacheObject(V value) {
            this.value = value;
        }
    }

    public RLUCache(long timeToLive, int maxItems) {
        this.timeToLive = timeToLive * 1000;

        cacheMap = new LRUMap(maxItems);
    }

    @SuppressWarnings("unchecked")
    public void put(K key, V value) {
        synchronized (cacheMap) {
            cacheMap.put(key, new CacheObject(value));
        }
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        synchronized (cacheMap) {
            CacheObject c = (CacheObject) cacheMap.get(key);

            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public void remove(K key) {
        synchronized (cacheMap) {
            cacheMap.remove(key);
        }
    }

    public int size() {
        synchronized (cacheMap) {
            return cacheMap.size();
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanup() {
        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey;

        synchronized (cacheMap) {
            MapIterator itr = cacheMap.mapIterator();

            deleteKey = new ArrayList<>((cacheMap.size() / 2) + 1);
            K key;
            CacheObject cacheObject;

            while (itr.hasNext()) {
                key = (K) itr.next();
                cacheObject = (CacheObject) itr.getValue();

                if (cacheObject != null && (now > (timeToLive + cacheObject.lastAccessed))) {
                    deleteKey.add(key);
                }
            }
        }
        for (K key : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }

            Thread.yield();
        }
    }

}
