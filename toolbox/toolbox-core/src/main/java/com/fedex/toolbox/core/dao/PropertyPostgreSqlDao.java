package com.fedex.toolbox.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.joda.time.LocalDateTime;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import com.fedex.toolbox.core.bean.Property;

public class PropertyPostgreSqlDao extends NamedParameterJdbcDaoSupport implements PropertyDao {

  private final RowMapper<Property> PROP_MAPPER = new RowMapper<Property>() {
    @Override
    public Property mapRow(ResultSet rs, int rowNum) throws SQLException {
      Long id = rs.getLong("id");
      String key = rs.getString("key");
      String value = rs.getString("value");
      Timestamp ts = rs.getTimestamp("created_at");
      return new Property(id, key, value, new LocalDateTime(ts.getTime()));
    }
  };

  public PropertyPostgreSqlDao(DataSource dataSource) {
    setDataSource(dataSource);
  }

  @Override
  public Long count() {
    return getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM properties", Long.class);
  }

  @Override
  public Property delete(String key) {
    Property prev = findOne(key);
    getJdbcTemplate().update("DELETE FROM properties WHERE key=?", key);
    return prev;
  }

  @Override
  public boolean exists(String key) {
    return findOne(key) != null;
  }

  @Override
  public List<Property> findAll() {
    return getJdbcTemplate().query("SELECT * FROM properties", PROP_MAPPER);
  }

  @Override
  public Property findOne(String key) {
    try {
      return getJdbcTemplate().queryForObject("SELECT * FROM properties WHERE key = ?", new Object[] { key },
          PROP_MAPPER);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  protected Property saveOrUpdate(String key, String value) {
    if (exists(key)) {
      getJdbcTemplate().update("UPDATE properties SET value = ? WHERE key = ?", new Object[] { value, key });
    } else {
      getJdbcTemplate().update("INSERT properties(key, value) VALUES(?,?)", new Object[] { key, value });
    }
    return findOne(key);
  }

  /**
   * @deprecated Use {@link #save(String,String)} instead
   */
  @Override
  public Property update(String key, String value) {
    return save(key, value);
  }

  @Override
  public Property save(String key, String value) {
    getJdbcTemplate().update("INSERT INTO properties(key, value) VALUES(?,?)", key, value);
    return findOne(key);
  }
}
