package org.storm.syspack.dao.db2;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

public class LevelFactory {
  /** Lazy singleton - do not access directly. Use {@link #getDb2Props()} instead. */
  private static volatile Properties      DB2_PROPS;

  private static final String             DRIVER_KEY = "db2.driver";
  private static final String             PROPS_KEY  = "db2.%d.%s.props";
  private static final String             SOURCE_KEY = "source";
  private static final String             TARGET_KEY = "target";
  private static final String             URL_KEY    = "db2.%d.%s.url";

  private static final Map<String, Level> _cache     = new WeakHashMap<>();

  private static Level create(Integer level, String which) {
    assert level != null;
    assert which != null;

    String key = level + which;
    Level lvl = _cache.get(key);
    if (lvl == null) {
      Properties db2 = getDb2Props();
      String driver = db2.getProperty(DRIVER_KEY);
      String url = db2.getProperty(format(URL_KEY, level, which));
      String props = db2.getProperty(format(PROPS_KEY, level, which));

      _cache.put(key, (lvl = new Level(driver, url, props)));
    }
    return lvl;
  }

  /**
   * Creates a source {@link Level} which maps to the old READ_ONLY databases
   */
  public static Level createSource(Integer level) {
    return create(level, SOURCE_KEY);
  }

  /**
   * Creates a source {@link Level} which maps to the old READ_ONLY databases
   */
  public static Level createSource(String level) {
    assert level != null;
    return createSource(Integer.valueOf(level));
  }

  /**
   * Creates a source {@link Level} which maps to the new databases
   */
  public static Level createTarget(Integer level) {
    assert level != null;
    return create(level, TARGET_KEY);
  }

  /**
   * Creates a source {@link Level} which maps to the new databases
   */
  public static Level createTarget(String level) {
    assert level != null;
    return createTarget(Integer.valueOf(level));
  }

  private static Properties getDb2Props() {
    if (DB2_PROPS == null) {
      synchronized (LevelFactory.class) {
        if (DB2_PROPS == null) {
          try {
            Properties props = new Properties();
            props.load(LevelFactory.class.getResourceAsStream("/db2.properties"));
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
