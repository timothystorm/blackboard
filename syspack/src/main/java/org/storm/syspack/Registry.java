package org.storm.syspack;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds values that are configured after the JVM starts and holds thread local values. This is global context for
 * values used by other subsystems and should be populated at the earliest possible opportunity such as the first line
 * of application startup/main.
 * 
 * @see Config
 */
public class Registry {
  private static ThreadLocal<Map<String, Object>> _context = new ThreadLocal<Map<String, Object>>() {
                                                             protected Map<String, Object> initialValue() {
                                                               return new HashMap<String, Object>();
                                                             };
                                                           };
  private static final String                     DB2URL   = "DB2URL";
  private static final String                     PASSWORD = "PASSWORD";
  private static final String                     USERNAME = "USERNAME";

  /**
   * Get an arbitrary value that was added via {@link #put(String, Object)}
   * 
   * @param key
   * @return value or null if not found
   */
  @SuppressWarnings("unchecked")
  private static <T> T get(String key) {
    return (T) _context.get().get(key);
  }

  /**
   * Gets the DB2 url set with {@link #setDb2Url(String)}
   * 
   * @return DB2 url or null if not set
   */
  public static String getDb2Url() {
    return get(DB2URL);
  }

  /**
   * Gets the user's password set with {@link #setPassword(String)}
   * 
   * @return
   */
  public static String getPassword() {
    return get(PASSWORD);
  }

  /**
   * Gets the user's id set with {@link #setUsername(String)}
   * 
   * @return
   */
  public static String getUsername() {
    return get(USERNAME);
  }

  /**
   * Put an arbitrary value in the context
   * 
   * @param key
   * @param value
   */
  private static <T> void put(String key, T value) {
    _context.get().put(key, value);
  }

  /**
   * Sets the user's desired DB2 URL which dictates what DB or region to use for database calls
   * 
   * @param db2Url
   */
  public static void setDb2Url(String db2Url) {
    put(DB2URL, db2Url);
  }

  /**
   * Sets the user's password.
   * 
   * @param password
   */
  public static void setPassword(String password) {
    put(PASSWORD, password);
  }

  /**
   * Sets the user's id
   * 
   * @param username
   */
  public static void setUsername(String username) {
    put(USERNAME, username);
  }
}
