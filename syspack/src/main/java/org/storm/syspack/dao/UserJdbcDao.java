package org.storm.syspack.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.storm.syspack.domain.User;

public class UserJdbcDao extends JdbcDaoSupport implements UserDao {
  public UserJdbcDao(JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  @Override
  public User find(String userId) {
    return null;
  }
}
