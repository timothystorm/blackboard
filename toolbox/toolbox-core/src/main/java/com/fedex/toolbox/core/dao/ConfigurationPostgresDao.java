package com.fedex.toolbox.core.dao;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

public class ConfigurationPostgresDao extends NamedParameterJdbcDaoSupport implements ConfigurationDao {
  
  public ConfigurationPostgresDao(DataSource dataSource){
    setDataSource(dataSource);
  }

  @Override
  public Map<String, String> findAll() {
    return null;
  }

  @Override
  public String findByKey(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasKey(String key) {
    // TODO Auto-generated method stub
    return false;
  }

}
