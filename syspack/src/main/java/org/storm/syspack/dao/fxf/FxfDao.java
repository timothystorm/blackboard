package org.storm.syspack.dao.fxf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.storm.syspack.domain.User;

/**
 * Table<Row_Count, Column_Name, Value>
 */
public interface FxfDao {
  abstract List<Map<String, Object>> read(User... users);

  abstract List<Map<String, Object>> read(Collection<User> users);
}
