package org.storm.syspackage.service;

import org.storm.syspackage.domain.SysPackage;

import java.util.Collection;

public interface SysPackageService {
  Collection<SysPackage> getPackages(String... packageName);
}
