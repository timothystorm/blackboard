package org.storm.papyrus.rest;

import java.io.Serializable;

import org.storm.papyrus.core.Version;

public class UserScope implements Serializable {
  private static final long serialVersionUID = Version.svuid();

  private final String      _id;

  public UserScope(String id) {
    _id = id;
  }

  public String getId() {
    return _id;
  }
}
