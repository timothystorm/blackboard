package org.storm.papyrus.core;

import org.apache.commons.configuration2.Configuration;

public interface ConfigurationFactory {

  /**
   * Gets or creates a configuration with properties in the provided scope
   * 
   * @param scope
   *          - of the properties
   * @return configuration of the provided scope
   */
  Configuration createConfiguration(String scope);

}
