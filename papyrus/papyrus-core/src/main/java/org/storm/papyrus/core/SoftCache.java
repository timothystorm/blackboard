package org.storm.papyrus.core;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Soft reference cache which allows elements to be gc'ed if memory is low. This is not a replacement for a robust
 * caching framework. Instead this is a simplified caching strategy that will not cause memory issues when creating
 * large caches. If a client needs more control like pruning strategies or time to live entries then a full cache
 * framework should be used instead - EHCache.
 * 
 * @author Timothy Storm
 * @param <K> key type
 * @param <V> value type
 */
class SoftCache<K, V> implements Cache<K, V> {
  /** keep a soft reference to allow gc if memory gets low **/
  private final transient Map<K, SoftReference<CacheEntry<K, V>>> _cache = new ConcurrentHashMap<>();

  private class Pruner implements Runnable {
    @Override
    public void run() {
      try {
        for (Iterator<Entry<K, SoftReference<CacheEntry<K, V>>>> entryIt = _cache.entrySet()
                                                                                 .iterator(); entryIt.hasNext();) {
          Entry<K, SoftReference<CacheEntry<K, V>>> entry = entryIt.next();
          if (prune(entry.getValue())) _cache.remove(entry.getKey());
        }
      } catch (Exception error) {}
    }

    private boolean prune(SoftReference<CacheEntry<K, V>> ref) {
      if (ref == null) return true;

      CacheEntry<K, V> entry = ref.get();
      if (entry == null) return true;
      if (entry._key == null || entry._value == null) return true;
      return entry.isExpired();
    }
  }

  public SoftCache() {
    this(15, TimeUnit.MINUTES);
  }

  public SoftCache(long prune, TimeUnit unit) {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, (runnable) -> {
      Thread thread = new Thread(runnable, "SoftCache:Pruner");
      thread.setDaemon(true);
      return thread;
    });
    executor.scheduleWithFixedDelay(new Pruner(), prune, prune, unit);
  }

  private static class CacheEntry<K, V> {
    final K    _key;
    final V    _value;
    final long _expire, _epoch;

    CacheEntry(K key, V value, long expire, TimeUnit unit) {
      _key = key;
      _value = value;
      _epoch = System.currentTimeMillis();
      _expire = unit.toMillis(expire);
    }

    boolean isExpired() {
      return (System.currentTimeMillis() - _epoch) >= _expire;
    }
  }

  @Override
  public V get(K key) {
    SoftReference<CacheEntry<K, V>> ref = _cache.get(key);
    if (ref == null /* no ref */) return null;

    CacheEntry<K, V> entry = ref.get();
    if (entry == null /* gc'ed value */) _cache.remove(key);
    return entry == null ? null : entry._value;
  }

  @Override
  public V put(K key, V value) {
    if(key == null) throw new NullPointerException("key required!");
    return put(key, value, 10, TimeUnit.MINUTES);
  }

  public V put(K key, V value, long expire, TimeUnit unit) {
    // create new cache entry
    CacheEntry<K, V> newEntry = new CacheEntry<K, V>(key, value, expire, unit);
    SoftReference<CacheEntry<K, V>> newRef = new SoftReference<>(newEntry);

    // put new entry and capture previous entry
    SoftReference<CacheEntry<K, V>> prevRef = _cache.put(key, newRef);
    if (prevRef == null /* no prev ref */) return null;

    // clear previous cache entry and return value
    CacheEntry<K, V> prevEntry = prevRef.get();
    prevRef.clear();
    return prevEntry == null ? null : prevEntry._value;
  }

  @Override
  public V remove(K key) {
    SoftReference<CacheEntry<K, V>> prevRef = _cache.remove(key);
    if (prevRef == null /* no prev ref */) return null;

    CacheEntry<K, V> prevEntry = prevRef.get();
    prevRef.clear();
    return prevEntry == null ? null : prevEntry._value;
  }

  @Override
  public boolean contains(K key) {
    return _cache.containsKey(key);
  }
}
