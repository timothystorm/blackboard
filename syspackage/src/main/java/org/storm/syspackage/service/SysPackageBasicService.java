package org.storm.syspackage.service;

import org.storm.syspackage.domain.SysPackage;
import org.storm.syspackage.service.dao.SysPackageDao;

import java.util.*;

public class SysPackageBasicService implements SysPackageService {
  private final SysPackageDao _dao;

  public SysPackageBasicService(SysPackageDao dao) {
    _dao = dao;
  }

  /*
   * (non-Javadoc)
   * @see org.storm.syspackage.service.SysPackageService#getPackagesFor(java.util.List)
   */
  @Override
  public Collection<SysPackage> getPackages(String... packageName) {
    if (packageName == null || packageName.length <= 0) return Collections.emptyList();
    Set<SysPackage> packages = new HashSet<>();
    Arrays.stream(packageName).forEach(name -> {
      packages.add(_dao.find(name));
    });
    return packages;
  }
}
