package org.storm.papyrus.core;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maintains a cache of the requested {@link PapyrusConfiguration}s to improve lookup performance.
 * 
 * @author Timothy Storm
 */
public class ConfigManager {
  private final DataSource                       _dataSource;

  private static final Logger                    log    = LoggerFactory.getLogger(ConfigManager.class);

  /** keep a soft reference cache to allow gc if memory gets low **/
  private final SoftCache<String, Configuration> _cache = new SoftCache<>();

  public ConfigManager(DataSource dataSource) {
    _dataSource = dataSource;
  }

  /**
   * Gets, or creates if null, a DB configuration with properties in the provided scope
   * 
   * @param scope
   *          - filter of properties
   * @param dataSource
   *          - to connect to the correct DB
   * @return configuration linked to the DB
   */
  public Configuration getPapyrusConfiguration(final String scope) {
    Configuration config = _cache.get(scope);
    if (config == null) {
      log.debug("loading '{}' database configuration", scope);
      _cache.put(scope, (config = new PapyrusConfiguration(_dataSource, scope)));
    }
    return config;
  }
}
