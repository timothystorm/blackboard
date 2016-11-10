package org.storm.papyrus.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;

/**
 * <p>
 * Main BasePapyrus service.
 * </p>
 * 
 * @author Timothy Storm
 */
public class BasePapyrus implements Papyrus {
  /** creates configurations for the property scopes */
  private final ConfigurationFactory _factory;

  public BasePapyrus(ConfigurationFactory factory) {
    _factory = factory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String deleteProperty(String scope, String key) {
    Configuration config = _factory.createConfiguration(scope);
    String prev = config.getString(key);
    config.clearProperty(key);
    return prev;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object> getProperties(String scope) {
    Configuration config = _factory.createConfiguration(scope);
    if(config == null || config.isEmpty()) return Collections.emptyMap();
    
    Map<String, Object> props = new HashMap<>();
    for (Iterator<String> keyIt = config.getKeys(); keyIt.hasNext();) {
      String key = keyIt.next();
      props.put(key, config.getProperty(key));
    }
    return Collections.unmodifiableMap(props);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getProperty(String scope, String key) {
    return _factory.createConfiguration(scope)
                   .getString(key);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void saveProperties(String scope, Map<String, Object> properties) {
    properties.entrySet()
              .forEach(e -> {
                saveProperty(scope, e.getKey(), e.getValue());
              });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String saveProperty(String scope, String key, Object value) {
    Configuration config = _factory.createConfiguration(scope);
    String prev = config.getString(key);
    config.addProperty(key, value);
    return prev;
  }
}
