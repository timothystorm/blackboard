package org.storm.syspackage.service;

import java.util.Collection;

import org.storm.syspackage.domain.SysPackage;

public interface SysPackageService {
  Collection<SysPackage> getPackagesFor(String packageName);
}
