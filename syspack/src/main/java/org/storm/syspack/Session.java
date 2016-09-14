package org.storm.syspack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is global session for attributes used by other subsystems and should be populated at the earliest possible
 * opportunity such as the first line
 * of application startup/main.
 * 
 * @see Config
 */
public class Session {
  // singleton instance
  private static Session     _instance;

  public static final String DB2LEVEL = "DB2LEVEL";
  public static final String PASSWORD        = "PASSWORD";
  public static final String USERNAME        = "USERNAME";

  public static Session instance() {
    if (_instance == null) {
      synchronized (Session.class) {
        if (_instance == null) _instance = new Session();
      }
    }
    return _instance;
  }

  private final Map<String, Object> _attributes;

  private Session() {
    _attributes = new ConcurrentHashMap<>();
  }

  /**
   * Get an arbitrary value that was added via {@link #put(String, Object)}
   * 
   * @param key
   * @return value or null if not found
   */
  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    return (T) _attributes.get(key);
  }

  /**
   * Put an arbitrary value in the context
   * 
   * @param key
   * @param value
   */
  public <T> void put(String key, T value) {
    _attributes.put(key, value);
  }
}