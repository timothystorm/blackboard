package org.storm.periscope.repo;

import org.storm.periscope.domain.User;

public interface UserRepo {
    User findByUsername(String username);
    User save(User user);
}
