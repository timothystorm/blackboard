package org.storm.syspackage.service.dao;

import java.util.Collection;

import org.storm.syspackage.domain.SysPackage;

public interface SysPackageDao {
  Collection<SysPackage> find(String packageName);
}
