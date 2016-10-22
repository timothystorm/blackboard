package org.storm.papyrus.core;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Soft reference cache which allows elements to be gc'ed if memory is low. This is not a replacement for a robust
 * caching framework. Instead this is a simplified caching strategy that will not cause memory issues when creating
 * large caches. If a client needs more control like pruning strategies or time to live entries then a full cache
 * framework should be used instead - memcache.
 * 
 * @author Timothy Storm
 * @param <K>
 * @param <V>
 */
public class SoftCache<K, V> {
  /** keep a soft reference cache to allow gc if memory gets low **/
  private final transient Map<K, SoftReference<CacheEntry<K, V>>> _cache    = new ConcurrentHashMap<>();
  private final ReferenceQueue<CacheEntry<K, V>>                  _refQueue = new ReferenceQueue<>();

  private class Pruner extends Thread {
    Pruner() {
      setName("SoftCache:Pruner");
      setDaemon(true);
    }

    @Override
    public void run() {
      while (true) {
        try {
          Reference<? extends CacheEntry<K, V>> ref = _refQueue.remove();
          CacheEntry<K, V> entry = ref.get();
          if (entry != null) _cache.remove(entry._key);
        } catch (InterruptedException ignore_and_return) {}
      }
    }
  }

  public SoftCache() {
    new Pruner().start();
  }

  private static class CacheEntry<K, V> {
    final K _key;
    final V _value;

    CacheEntry(K key, V value) {
      _key = key;
      _value = value;
    }
  }

  public V get(K key) {
    SoftReference<CacheEntry<K, V>> ref = _cache.get(key);
    if (ref == null /* no ref */) return null;

    CacheEntry<K, V> entry = ref.get();
    if (entry == null /* gc'ed value */) _cache.remove(key);
    return entry == null ? null : entry._value;
  }

  public V put(K key, V value) {
    // create new cache entry
    CacheEntry<K, V> newEntry = new CacheEntry<K, V>(key, value);
    SoftReference<CacheEntry<K, V>> newRef = new SoftReference<>(newEntry, _refQueue);

    // put new entry and capture previous entry
    SoftReference<CacheEntry<K, V>> prevRef = _cache.put(key, newRef);
    if (prevRef == null /* no prev ref */) return null;

    // clear previous cache entry and return value
    CacheEntry<K, V> prevEntry = prevRef.get();
    prevRef.clear();
    return prevEntry == null ? null : prevEntry._value;
  }

  public V remove(K key) {
    SoftReference<CacheEntry<K, V>> prevRef = _cache.remove(key);
    if (prevRef == null /* no prev ref */) return null;

    CacheEntry<K, V> prevEntry = prevRef.get();
    prevRef.clear();
    return prevEntry == null ? null : prevEntry._value;
  }

  public boolean contains(K key) {
    return _cache.containsKey(key);
  }
}
