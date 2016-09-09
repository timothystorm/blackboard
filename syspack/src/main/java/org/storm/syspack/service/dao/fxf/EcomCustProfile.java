package org.storm.syspack.service.dao.fxf;

import java.util.Collection;

import javax.sql.DataSource;

import org.storm.syspack.domain.User;

import com.google.common.collect.Table;

public class EcomCustProfile implements DataDao {

  private final DataSource _dataSource;

  public EcomCustProfile(DataSource dataSource) {
    _dataSource = dataSource;
  }

  @Override
  public Table<Long, String, Object> read(User... users) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Table<Long, String, Object> read(Collection<User> users) {
    // TODO Auto-generated method stub
    return null;
  }

}
