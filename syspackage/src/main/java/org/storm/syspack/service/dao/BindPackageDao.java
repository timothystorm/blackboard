package org.storm.syspack.service.dao;

import java.util.Collection;

import org.storm.syspack.domain.BindPackage;

public interface BindPackageDao {
  /**
   * Find Bind packages based on a name pattern.
   * 
   * @param bindPackage
   * @return
   */
  Collection<BindPackage> find(String bindPackage);
}
