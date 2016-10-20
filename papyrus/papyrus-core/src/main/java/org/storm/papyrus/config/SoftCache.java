package org.storm.papyrus.config;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SoftCache<K, V> {
  /** keep a soft reference cache to allow gc if memory gets low **/
  private transient Map<K, SoftReference<V>> _cache = new ConcurrentHashMap<>();

  SoftCache() {}

  V get(K key) {
    SoftReference<V> configRef = _cache.get(key);
    if (configRef == null) return null;
    return configRef.get();
  }

  V put(K key, V value) {
    SoftReference<V> ref = _cache.put(key, new SoftReference<V>(value));
    if (ref == null) return null;
    V prev = ref.get();
    ref.clear();
    return prev;
  }

  V remove(K key) {
    SoftReference<V> ref = _cache.remove(key);
    if (ref == null) return null;
    V prev = ref.get();
    ref.clear();
    return prev;
  }
}
