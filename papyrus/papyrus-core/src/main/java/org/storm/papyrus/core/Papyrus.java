package org.storm.papyrus.core;

import java.util.Map;

/**
 * <p>
 * Allows basic access to scoped configuration properties. Scope in this sense is a grouping of configurations by
 * group, organization or application.
 * </p>
 * 
 * @author Timothy Storm
 */
public interface Papyrus {

  /**
   * Delete a property mapped to the key
   * 
   * @param scope
   *          - grouping of properties
   * @param key
   *          - property is mapped to
   * @return value associated to the key before the delete
   */
  String deleteProperty(String scope, String key);

  /**
   * Fetches all properties in the provided scope
   * 
   * @param scope
   *          - grouping of properties
   * @return all properties in scope, is never null but can be empty
   */
  Map<String, Object> getProperties(String scope);

  /**
   * Gets a single property in the provided scope mapped to the key
   * 
   * @param scope
   *          - grouping of properties
   * @param key
   *          - property is mapped to
   * @return property value or null if no property is mapped to the key in scope
   */
  Object getProperty(String scope, String key);

  /**
   * Save many properties in the provided scope
   * 
   * @param scope
   *          - grouping of properties
   * @param properties
   *          - key/value map of properties to be saved
   */
  void saveProperties(String scope, Map<String, Object> properties);

  /**
   * Save a new property in the provided scope mapped to the key
   * 
   * @param scope
   *          - grouping of properties
   * @param key
   *          - property is mapped to
   * @param value
   *          - to save
   * @return previous value mapped to the key or null if no property was ever mapped
   */
  String saveProperty(String scope, String key, Object value);
}
