package com.fedex.toolbox.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

public class ConfigPostgresDao extends NamedParameterJdbcDaoSupport implements ConfigDao {

  public ConfigPostgresDao(DataSource dataSource) {
    setDataSource(dataSource);
  }

  @Override
  public String save(String key, String value) {
    String previous = findOne(key);
    getJdbcTemplate().update("INSERT INTO configuration(key, value) VALUES(?,?)", key, value);
    return previous;
  }

  @Override
  public String findOne(String key) {
    return getJdbcTemplate().queryForObject("SELECT value FROM configuration where key=?", String.class, key);
  }
  
  private final RowMapper<Entry<String, Object>> FIND_ALL_MAPPER = new RowMapper<Entry<String, Object>> (){
    @Override
    public Entry<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new SimpleEntry<String, Object>(rs.getString(1), rs.getObject(2));
    }
  };

  @Override
  public List<Entry<String, Object>> findAll() {
    return getJdbcTemplate().query("SELECT key, value FROM configuration", FIND_ALL_MAPPER);
  }

  @Override
  public Long count() {
    return getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM configuration", Long.class);
  }

  @Override
  public void delete(String key) {
    getJdbcTemplate().update("DELETE FROM configuration WHERE key=?", key);
  }

  @Override
  public boolean exists(String key) {
    return findOne(key) != null;
  }
}
