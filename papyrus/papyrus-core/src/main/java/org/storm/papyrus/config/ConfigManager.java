package org.storm.papyrus.config;

import java.net.URL;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DatabaseConfiguration;
import org.apache.commons.configuration.JNDIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {
  private static volatile ConfigManager _instance;

  private static final Logger           log = LoggerFactory.getLogger(ConfigManager.class);

  public static ConfigManager getInstance() {
    if (_instance == null) {
      synchronized (ConfigManager.class) {
        if (_instance == null) {
          _instance = new ConfigManager();
        }
      }
    }
    return _instance;
  }

  /** keep a soft reference cache to allow gc if memory gets low **/
  private final SoftCache<String, Configuration> _cache = new SoftCache<>();

  private ConfigManager(/* singleton */) {}

  public Configuration getDatabaseConfiguration(final String name, final DataSource dataSource) {
    Configuration config = _cache.get(name);
    if (config == null) {
      log.debug("loading '{}' database configuration", name);

      config = new DatabaseConfiguration(dataSource, "configurations", "name", "key", "value", name, true);
      _cache.put(name, config);
    }
    return config;
  }

  public Configuration getJndiConfiguration(final String prefix, final Context cntx) {
    Configuration config = _cache.get(prefix);
    if (config == null) {
      log.debug("loading '{}' jndi configuration", prefix);

      config = new JNDIConfiguration(cntx, prefix);
      _cache.put(prefix, config);
    }
    return config;
  }

  public Configuration getPropertyConfiguration(final URL url) {
    String key = url.toString();

    Configuration config = _cache.get(key);
    if (config == null) {
      try {
        log.debug("loading '{}' url configuration", key);

        PropertiesConfiguration propConfig = new PropertiesConfiguration(url);
        propConfig.setAutoSave(true);
        _cache.put(key, (config = propConfig));
      } catch (ConfigurationException e) {
        log.error("loading configuration from {}", key);
      }
    }
    return config;
  }
}
