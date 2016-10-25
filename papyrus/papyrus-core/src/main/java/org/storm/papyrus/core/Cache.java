package org.storm.papyrus.core;

public interface Cache<K, V> {

  V get(K key);

  V put(K key, V value);

  V remove(K key);

  boolean contains(K key);
}
