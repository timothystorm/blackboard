package org.storm.periscope.domain;

public interface UserPolicy {
    boolean isEnabled();
    boolean isNotExpired();
    boolean isNotLocked();
}
