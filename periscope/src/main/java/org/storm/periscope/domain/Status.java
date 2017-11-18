package org.storm.periscope.domain;

/**
 * State or condition of an entity.
 */
public interface Status {
  boolean isEnabled();

  boolean isNotExpired();

  boolean isNotLocked();
}
