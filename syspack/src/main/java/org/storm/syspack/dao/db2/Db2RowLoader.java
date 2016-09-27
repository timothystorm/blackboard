package org.storm.syspack.dao.db2;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

public class Db2RowLoader extends NamedParameterJdbcDaoSupport {
  static final Logger log = LoggerFactory.getLogger(Db2RowLoader.class);

  public Db2RowLoader(final JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  public void load(String tableName, Map<String, Object> params) {
    try {
      NamedParameterInsert insert = new NamedParameterInsert(getConnection(), tableName);
      getNamedParameterJdbcTemplate().update(insert.query(), params);
    } catch (CannotGetJdbcConnectionException e) {} catch (SQLException e) {}
  }
}
