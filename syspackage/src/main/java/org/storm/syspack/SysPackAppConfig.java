package org.storm.syspack;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.storm.syspack.service.BindPackageBasicService;
import org.storm.syspack.service.BindPackageService;
import org.storm.syspack.service.dao.BindPackageDao;
import org.storm.syspack.service.dao.BindPackageJdbcDao;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Poor man's DI
 */
public class SysPackAppConfig {
  public static final String DEFAULT_URL = "jdbc:db2://zos1.freight.fedex.com:446/HRO_DBP1";

  private static final String DRIVER_CLASS = "com.ibm.db2.jcc.DB2Driver";

  private BindPackageDao _dao;

  private DataSource _dataSource;

  private Lock _lock = new ReentrantLock();

  private BindPackageService _service;

  private final String _username, _password, _url;

  public SysPackAppConfig(String username, String password, String url) {
    _username = username;
    _password = password;
    _url = url == null ? DEFAULT_URL : url;
  }

  public DataSource dataSource() {
    if (_dataSource == null) {
      _lock.lock();

      try {
        if (_dataSource == null) {
          ComboPooledDataSource ds = new ComboPooledDataSource();
          ds.setDriverClass(DRIVER_CLASS);
          ds.setJdbcUrl(_url);
          ds.setUser(_username);
          ds.setPassword(_password);
          ds.setAcquireRetryAttempts(1);
          _dataSource = ds;
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        _lock.unlock();
      }
    }
    return _dataSource;
  }

  public BindPackageDao sysPackageDao() {
    if (_dao == null) {
      _lock.lock();

      try {
        if (_dao == null) _dao = new BindPackageJdbcDao(dataSource());
      } finally {
        _lock.unlock();
      }
    }
    return _dao;
  }

  public BindPackageService sysPackageService() {
    if (_service == null) {
      _lock.lock();

      try {
        if (_service == null) _service = new BindPackageBasicService(sysPackageDao());
      } finally {
        _lock.unlock();
      }
    }
    return _service;
  }
}
