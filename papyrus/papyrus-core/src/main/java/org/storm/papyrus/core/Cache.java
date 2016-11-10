package org.storm.papyrus.core;

/**
 * Basic Cache interface to be implemented by classes that will provide cache behavior for {@link PapyrusConfiguration}
 *
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {

  /**
   * Get the value associated with the key from the cache or null if not found
   *
   * @param key - that maps the value
   * @return value mapped to key or null if not found
   */
  V get(K key);

  /**
   * Puts a value mapped to the key into the cache
   *
   * @param key   - that maps the value
   * @param value - value that maps to the key
   * @return any previous value mapped to the key
   * @throws NullPointerException if the key is null
   */
  V put(K key, V value);

  /**
   * Removes the value mapped to the key
   *
   * @param key - that maps the value
   * @return the value removed or null if no value was mapped to the key
   */
  V remove(K key);

  /**
   * Determines if cache holds a value mapped to the key
   *
   * @param key - that maps the value
   * @return true if the key maps to a value, false otherwise
   */
  boolean contains(K key);
}
