package org.storm.papyrus.core;

import static java.lang.Runtime.getRuntime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.storm.papyrus.core.SoftCache;

public class SoftCacheTest {

  SoftCache<Object, Object> cache;

  @Before
  public void setUp() throws Exception {
    cache = new SoftCache<>();
  }

  @After
  public void tearDown() throws Exception {
    cache = null;
  }

  @Test
  public void exceedMemory() throws Exception {
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
  public void put() throws Exception {
    assertNull(cache.put("myKey", "myFirstValue"));
    assertEquals("myFirstValue", cache.put("myKey", "mySecondValue"));
  }
  
  @Test
  public void remove() throws Exception {
    assertNull(cache.put("myKey", "myValue"));
    assertEquals("myValue", cache.remove("myKey"));
    assertNull(cache.get("myKey"));
  }
}
