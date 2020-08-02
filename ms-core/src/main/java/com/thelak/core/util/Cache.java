package com.thelak.core.util;

public interface Cache<K, V> {

    void put(K key, V value);

    V get(K key);

    void remove(K key);

    int size();

    /**
     * Метод для очистки устаревших в кэше объектов
     * (даже если кэш не переполнен)
     */
    void cleanup();
}
