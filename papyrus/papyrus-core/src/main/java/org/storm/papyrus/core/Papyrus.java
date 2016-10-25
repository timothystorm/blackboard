package org.storm.papyrus.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;

/**
 * <p>
 * Main Papyrus service.
 * </p>
 * <p>
 * Allows basic access to scoped configuration properties. Scope in this sense is a grouping of configurations by
 * group, organization or application.
 * </p>
 * 
 * @author Timothy Storm
 */
public class Papyrus {
  private final ConfigManager _manager;

  public Papyrus(DataSource dataSource) {
    _manager = new ConfigManager(dataSource);
  }

  /**
   * Fetches all properties in the provided scope
   * 
   * @param scope
   *          - grouping of properties
   * @return all properties in scope, is never null but can be empty
   */
  public Map<String, Object> getProperties(String scope) {
    Configuration config = _manager.getPapyrusConfiguration(scope);
    Map<String, Object> props = new HashMap<>();
    for (Iterator<String> keyIt = config.getKeys(); keyIt.hasNext();) {
      String key = keyIt.next();
      props.put(key, config.getProperty(key));
    }
    return Collections.unmodifiableMap(props);
  }

  /**
   * Gets a single property in the provided scope mapped to the key
   * 
   * @param scope
   *          - grouping of properties
   * @param key
   *          - of property
   * @return property value or null if no property is mapped to the key in scope
   */
  public Object getProperty(String scope, String key) {
    return _manager.getPapyrusConfiguration(scope)
                   .getProperty(key);
  }

  public String saveProperty(String scope, String key, Object value) {
    Configuration config = _manager.getPapyrusConfiguration(scope);
    String prev = config.getString(key);
    config.addProperty(key, value);
    return prev;
  }

  public String deleteProperty(String scope, String key) {
    Configuration config = _manager.getPapyrusConfiguration(scope);
    String prev = config.getString(key);
    config.clearProperty(key);
    return prev;
  }

  public void saveProperties(String scope, Map<String, Object> properties) {
    properties.entrySet()
              .forEach(e -> {
                saveProperty(scope, e.getKey(), e.getValue());
              });
  }
}
