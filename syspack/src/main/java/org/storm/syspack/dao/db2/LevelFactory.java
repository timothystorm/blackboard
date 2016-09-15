package org.storm.syspack.dao.db2;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * Creates {@link Level}s based on local configurations.
 * 
 * @author Timothy Storm
 */
public class LevelFactory {
  /** Lazy singleton - do not access directly. Use {@link #getDb2Props()} instead. */
  private static volatile Properties      DB2_PROPS;

  private static final String             DRIVER_KEY = "db2.driver";
  private static final String             ATTR_KEY   = "db2.att.%s.%s";
  private static final String             UTE_KEY    = "ute";
  private static final String             ENV_KEY    = "env";
  private static final String             URL_KEY    = "db2.url.%s.%s";

  private static final Map<String, Level> _cache     = new WeakHashMap<>();

  /**
   * Creates a level with the specified id and env
   * 
   * @param id
   *          - of the level to create [1-7]
   * @param env
   *          - of the level to create [UTE, ENV]
   * @return Populated Level
   */
  private static Level create(String id, String env) {
    assert id != null;
    assert env != null;

    // cache key
    String key = id + env;

    Level lvl = _cache.get(key);
    if (lvl == null) {
      Properties db2 = getDb2Props();
      Level.Builder builder = Level.builder();
      builder.driver(db2.getProperty(DRIVER_KEY));
      builder.url(db2.getProperty(format(URL_KEY, env, id)));
      builder.attribute(db2.getProperty(format(ATTR_KEY, env, id)));
      _cache.put(key, (lvl = builder.build()));
    }
    return lvl;
  }

  /**
   * Creates a ute {@link Level} which maps to the old (READ_ONLY) databases
   * 
   * @param id
   *          - of the level to create
   */
  public static Level createUte(Object id) {
    assert id != null;
    return create(id.toString(), UTE_KEY);
  }

  /**
   * Creates an environment {@link Level} which maps to the new databases
   * 
   * @param id
   *          - of the level to create
   */
  public static Level createEnv(Object id) {
    assert id != null;
    return create(id.toString(), ENV_KEY);
  }

  /**
   * Load the DB2 properties from ~/.fxf/db2.properties
   * 
   * @return loaded properties
   */
  private static Properties getDb2Props() {
    if (DB2_PROPS == null) {
      synchronized (LevelFactory.class) {
        if (DB2_PROPS == null) {
          try {
            Properties props = new Properties();
            File dbprops = new File(System.getProperty("user.home"), ".fxf/db2.properties");
            props.load(new FileInputStream(dbprops));
            DB2_PROPS = props;
          } catch (IOException e) {
            throw new RuntimeException("Failed to load DB2 properties", e);
          }
        }
      }
    }
    return DB2_PROPS;
  }
}
