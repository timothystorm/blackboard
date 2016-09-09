package org.storm.syspackage.service.dao;

import org.storm.syspackage.domain.SysPackage;

public interface SysPackageDao {
  SysPackage find(String packageName);
}
