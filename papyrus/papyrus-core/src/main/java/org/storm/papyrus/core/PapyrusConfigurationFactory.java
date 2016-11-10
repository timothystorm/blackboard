package org.storm.papyrus.core;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ConfigurationFactory} that builds {@link PapyrusConfiguration}s
 * 
 * @author Timothy Storm
 */
public class PapyrusConfigurationFactory implements ConfigurationFactory {
  private final DataSource                       _dataSource;

  private static final Logger                    log    = LoggerFactory.getLogger(PapyrusConfigurationFactory.class);

  /** keep configurations in a cache to speed lookup **/
  private final Cache<String, Configuration> _cache;

  public PapyrusConfigurationFactory(DataSource dataSource) {
    this(dataSource, new SoftCache<>());
  }
  
  public PapyrusConfigurationFactory(DataSource dataSource, Cache<String, Configuration> cache){
    _dataSource = dataSource;
    _cache = cache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Configuration createConfiguration(final String scope) {
    Configuration config = _cache.get(scope);
    if (config == null) {
      log.debug("loading '{}' database configuration", scope);
      _cache.put(scope, (config = new PapyrusConfiguration(_dataSource, scope)));
    }
    return config;
  }
}
