package org.storm.papyrus.core;

import java.net.URL;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.DatabaseConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade that provides logical defaults and simplifies the creation of the different implementations of
 * {@link Configuration}.
 * 
 * @author Timothy Storm
 */
public class ConfigManager {
  /** singleton instance */
  private static volatile ConfigManager _instance;

  private static final Logger           log = LoggerFactory.getLogger(ConfigManager.class);

  /**
   * @return existing intance or creates a new instance if null
   */
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

  /**
   * Gets, or creates if null, a DB configuration with properties in the provided scope
   * 
   * @param scope
   *          - filter of properties
   * @param dataSource
   *          - to connect to the correct DB
   * @return configuration linked to the DB
   */
  public Configuration getDatabaseConfiguration(final String scope, final DataSource dataSource) {
    Configuration config = _cache.get(scope);
    if (config == null) {
      log.debug("loading '{}' database configuration", scope);

      try {
        BasicConfigurationBuilder<DatabaseConfiguration> builder = new BasicConfigurationBuilder<>(
            DatabaseConfiguration.class);
        Parameters params = new Parameters();
        builder.configure(params.database()
                                .setDataSource(dataSource)
                                .setTable("configurations")
                                .setKeyColumn("key")
                                .setValueColumn("value")
                                .setConfigurationNameColumn("scope")
                                .setConfigurationName(scope)
                                .setThrowExceptionOnMissing(false)
                                .setListDelimiterHandler(new DefaultListDelimiterHandler('|')));
        _cache.put(scope, (config = builder.getConfiguration()));
      } catch (ConfigurationException e) {
        log.error("building database configuration", e);
      }
    }
    return config;
  }

  /**
   * Gets, or creates if null, a properties configuration
   * 
   * @param url
   *          - location of properties file to load
   * @return configuration linked to a properties file
   */
  public Configuration getPropertyConfiguration(final URL url) {
    String key = url.toString();

    Configuration config = _cache.get(key);
    if (config == null) {
      try {
        log.debug("loading '{}' url configuration", key);

        // load properties file
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
            PropertiesConfiguration.class).configure(
                params.fileBased()
                      .setEncoding("UTF-8")
                      .setURL(url)
                      .setThrowExceptionOnMissing(false));
        _cache.put(key, (config = builder.getConfiguration()));
      } catch (ConfigurationException e) {
        log.error("loading configuration from {}", key);
      }
    }
    return config;
  }
}
