package org.storm.periscope.repo;

import org.storm.periscope.domain.User;

public interface UserRepo {
    User findByUsername(String username);
    User add(User user);
    boolean remove(User user);
    boolean contains(User user);
}
