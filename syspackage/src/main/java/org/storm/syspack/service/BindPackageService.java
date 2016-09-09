package org.storm.syspack.service;

import java.util.Collection;

import org.storm.syspack.domain.BindPackage;

public interface BindPackageService {
  Collection<BindPackage> getPackages(String... packageName);
}
