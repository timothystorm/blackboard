package org.storm.syspack.service.dao.fxf;

import java.util.Arrays;
import java.util.Collection;

import javax.sql.DataSource;

import org.storm.syspack.domain.User;

import com.google.common.collect.Table;

public class AniCustomer implements DataDao {
  private final DataSource _dataSource;

  public AniCustomer(DataSource dataSource) {
    _dataSource = dataSource;
  }

  @Override
  public Table<Long, String, Object> read(User... users) {
    return read(Arrays.asList(users));
  }

  @Override
  public Table<Long, String, Object> read(Collection<User> users) {
    return null;
  }
}
