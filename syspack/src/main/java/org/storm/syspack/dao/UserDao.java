package org.storm.syspack.dao;

import org.storm.syspack.domain.User;

public interface UserDao {
  User find(String userId);
}
