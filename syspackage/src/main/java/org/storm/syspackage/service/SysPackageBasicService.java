package org.storm.syspackage.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.storm.syspackage.domain.SysPackage;
import org.storm.syspackage.service.dao.SysPackageDao;

public class SysPackageBasicService implements SysPackageService {
  private final SysPackageDao _dao;

  public SysPackageBasicService(SysPackageDao dao) {
    _dao = dao;
  }

  /* (non-Javadoc)
   * @see org.storm.syspackage.service.SysPackageService#getPackagesFor(java.util.List)
   */
  @Override
  public List<SysPackage> getPackagesFor(List<String> packageNames) {
    if (packageNames == null || packageNames.isEmpty()) return Collections.emptyList();

    List<SysPackage> sysPackages = new ArrayList<>();

    // stream : distinct names -> sorted -> filter empty names -> map name to SysPackage
    packageNames.parallelStream().distinct().filter((name) -> {
      return isNotBlank(name);
    }).map((name) -> {
      return maptoSysPackage(name);
    }).forEach(sysPackages::addAll);

    // sort and return
    Collections.sort(sysPackages);
    return sysPackages;
  }

  protected Collection<SysPackage> maptoSysPackage(String packageName) {
    return _dao.find(packageName);
  }
}
