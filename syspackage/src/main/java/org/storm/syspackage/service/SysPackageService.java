package org.storm.syspackage.service;

import java.util.List;

import org.storm.syspackage.domain.SysPackage;

public interface SysPackageService {

  List<SysPackage> getPackagesFor(List<String> packageNames);

}
