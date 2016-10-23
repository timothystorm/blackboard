package org.storm.papyrus.core.service;

import java.util.Map;

public interface PapyrusService {
  Map<String, Object> getProperties(String scope);

  String getProperty(String scope, String key);

  String saveProperty(String scope, String key, Object value);

  String deleteProperty(String scope, String key);

  void saveProperties(String scope, Map<String, Object> properties);
}
