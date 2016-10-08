package com.fedex.toolbox.core.dao;

import java.util.List;
import java.util.Map.Entry;

public interface ConfigDao {
  /**
   * Saves a new configuration
   * 
   * @param key
   *          - which the specified value is to be associated
   * @param value
   *          - to be associated with the specified key
   * @return previous value associated with key, or null if there was no mapping for key
   */
  String save(String key, String value);

  String findOne(String key);

  List<Entry<String, Object>> findAll();

  Long count();

  void delete(String key);

  boolean exists(String key);
}
