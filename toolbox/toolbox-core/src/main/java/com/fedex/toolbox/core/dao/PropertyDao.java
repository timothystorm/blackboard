package com.fedex.toolbox.core.dao;

import java.util.List;

import com.fedex.toolbox.core.bean.Property;

public interface PropertyDao {
  /**
   * Saves a new configuration
   * 
   * @param key
   *          - which the specified value is to be associated
   * @param value
   *          - to be associated with the specified key
   * @return previous value associated with key, or null if there was no mapping for key
   * @deprecated Use {@link #save(String,String)} instead
   */
  Property update(String key, String value);

  /**
   * Saves a new configuration
   * 
   * @param key
   *          - which the specified value is to be associated
   * @param value
   *          - to be associated with the specified key
   * @return previous value associated with key, or null if there was no mapping for key
   */
  Property save(String key, String value);

  Property findOne(String key);

  List<Property> findAll();

  Long count();

  Property delete(String key);

  boolean exists(String key);
}
