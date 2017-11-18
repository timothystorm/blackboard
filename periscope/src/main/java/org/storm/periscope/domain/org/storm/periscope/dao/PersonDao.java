package org.storm.periscope.domain.org.storm.periscope.dao;

import org.storm.periscope.domain.Profile;
import org.storm.periscope.domain.User;

public interface PersonDao {
  User readUserByUsername(String username);
  User delete(User user);
  User update(User user);

  Profile readProfileByUser(User user);

}
