package com.fedex.toolbox.core.dao;

import java.util.Map;

public interface ConfigurationDao {
  Map<String, String> findAll();
  String findByKey(String key);
  boolean hasKey(String key);
}
