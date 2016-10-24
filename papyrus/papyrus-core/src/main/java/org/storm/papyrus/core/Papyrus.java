package org.storm.papyrus.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;

public class Papyrus {
  private final ConfigManager _manager;

  public Papyrus(DataSource dataSource) {
    _manager = new ConfigManager(dataSource);
  }

  public Map<String, Object> getProperties(String scope) {
    Configuration config = _manager.getPapyrusConfiguration(scope);
    Map<String, Object> props = new HashMap<>();
    for (Iterator<String> keyIt = config.getKeys(); keyIt.hasNext();) {
      String key = keyIt.next();
      props.put(key, config.getProperty(key));
    }
    return Collections.unmodifiableMap(props);
  }

  public String getProperty(String scope, String key) {
    return _manager.getPapyrusConfiguration(scope).getString(key);
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
    properties.entrySet().forEach(e -> {
      saveProperty(scope, e.getKey(), e.getValue());
    });
  }
}
