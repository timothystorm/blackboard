package org.storm.syspack.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.storm.syspack.domain.BindPackage;
import org.storm.syspack.service.dao.BindPackageDao;

public class BindPackageBasicService implements BindPackageService {
  private final BindPackageDao _dao;

  public BindPackageBasicService(BindPackageDao dao) {
    _dao = dao;
  }

  /*
   * (non-Javadoc)
   * @see org.storm.syspack.service.BindPackageService#getPackagesFor(java.util.List)
   */
  @Override
  public Collection<BindPackage> getPackages(String... packageName) {
    if (packageName == null || packageName.length <= 0) return Collections.emptyList();
    Collection<BindPackage> packages = new LinkedHashSet<>();
    Arrays.stream(packageName).forEach(name -> {
      Collection<BindPackage> sysPacks = _dao.find(name);
      if (sysPacks != null) packages.addAll(sysPacks);
    });
    return packages;
  }
}
