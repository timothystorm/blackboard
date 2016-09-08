package org.storm.syspackage.service;

import java.util.Collection;
import java.util.Collections;

import org.storm.syspackage.domain.SysPackage;
import org.storm.syspackage.service.dao.SysPackageDao;

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
  public Collection<SysPackage> getPackagesFor(String packageName) {
    if (packageName == null || packageName.isEmpty()) return Collections.emptyList();
    return _dao.find(packageName);
  }
}
