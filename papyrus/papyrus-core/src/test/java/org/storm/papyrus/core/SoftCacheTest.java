package org.storm.papyrus.core;

import static java.lang.Runtime.getRuntime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class SoftCacheTest {

  @Test
  public void exceedMemory() throws Exception {
    Cache<Object, Object> cache = new SoftCache<>();

    cache.put("myKey", "myValue");
    assertNotNull(cache.get("myKey"));

    // load memory to excess
    try {
      @SuppressWarnings("unused")
      Object[] bigMemoryObject = new Object[(int) getRuntime().maxMemory()];
    } catch (OutOfMemoryError ignore) {}

    assertNull(cache.get("myKey"));
    assertFalse(cache.contains("myKey"));
  }

  @Test
  public void expire() throws Exception {
    SoftCache<Object, Object> cache = new SoftCache<>(100, TimeUnit.MILLISECONDS);
    cache.put("myKey", "myValue", 100, TimeUnit.MILLISECONDS);
    assertNotNull(cache.get("myKey"));

    Thread.sleep(TimeUnit.SECONDS.toMillis(1));

    assertNull(cache.get("myKey"));
    assertFalse(cache.contains("myKey"));
  }

  @Test
  public void put() throws Exception {
    Cache<Object, Object> cache = new SoftCache<>();

    assertNull(cache.put("myKey", "myFirstValue"));
    assertEquals("myFirstValue", cache.put("myKey", "mySecondValue"));
  }

  @Test
  public void remove() throws Exception {
    Cache<Object, Object> cache = new SoftCache<>();

    assertNull(cache.put("myKey", "myValue"));
    assertEquals("myValue", cache.remove("myKey"));
    assertNull(cache.get("myKey"));
  }
}
