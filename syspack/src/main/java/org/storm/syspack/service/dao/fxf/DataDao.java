package org.storm.syspack.service.dao.fxf;

import java.util.Collection;

import org.storm.syspack.domain.User;

import com.google.common.collect.Table;

/**
 * Table<Row_Count, Column_Name, Value>
 */
public interface DataDao {
  Table<Long, String, Object> read(User... users);

  Table<Long, String, Object> read(Collection<User> users);
}
