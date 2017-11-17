package org.storm.periscope.domain;

import java.io.Serializable;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Immutable user identity attributes.
 */
public class User implements CredentialErasure, UserPolicy, Serializable {
    private static final long serialVersionUID = 5026142403543143203L;
    private final String _username;
    private final boolean _notExpired, _notLocked, _enabled;
    private transient String _password;

    /**
     * Construct a <code>User</code>.
     *
     * @param username - user principal, required and can not be empty/blank
     * @param password - user credentials, required
     * @throws IllegalArgumentException if a <code>null</code> value was passed for the username or password
     */
    public User(String username, String password) {
        this(username, password, true, true, true);
    }

    public User(String username, String password, boolean enabled, boolean notExpired, boolean notLocked) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("username and password required");
        }
        _username = username;
        _password = password;
        _enabled = enabled;
        _notExpired = notExpired;
        _notLocked = notLocked;
    }

    public static User.Builder builder() {
        return new Builder();
    }

    public String getUsername() {
        return _username;
    }

    public User withUsername(String username) {
        return new User(username, _password, _enabled, _notExpired, _notLocked);
    }

    public String getPassword() {
        return _password;
    }

    public User withPassword(String password) {
        return new User(_username, _password, _enabled, _notExpired, _notLocked);
    }

    @Override
    public boolean isEnabled() {
        return _enabled;
    }

    @Override
    public boolean isNotExpired() {
        return _notExpired;
    }

    @Override
    public boolean isNotLocked() {
        return _notLocked;
    }

    @Override
    public int hashCode() {
        return _username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof User) && _username.equals(((User) obj)._username);
    }

    @Override
    public void eraseCredentials() {
        _password = null;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString()).append(": ");
        str.append("username: ").append(_username).append("; ");
        str.append("password: [PROTECTED]; ");
        str.append("enabled: ").append(_enabled).append("; ");
        str.append("expired: ").append(!_notExpired).append("; ");
        str.append("locked: ").append(!_notLocked).append("; ");
        return str.toString();
    }

    public static class Builder {
        private String _username, _password;
        private Function<String, String> _encoder = (password -> password);
        private boolean _expired, _locked, _enabled;

        private Builder() {
        }

        public User.Builder username(String username) {
            requireNonNull(username, "username required!");
            _username = username;
            return this;
        }

        public User.Builder password(String password) {
            requireNonNull(password, "password required!");
            _password = password;
            return this;
        }

        public User.Builder passwordEncoder(Function<String, String> encoder) {
            requireNonNull(encoder, "password encoder required!");
            _encoder = encoder;
            return this;
        }

        public User.Builder expired(boolean expired) {
            _expired = expired;
            return this;
        }

        public User.Builder locked(boolean locked) {
            _locked = locked;
            return this;
        }

        public User.Builder enabled(boolean enabled) {
            _enabled = enabled;
            return this;
        }

        public User build() {
            String encodedPassword = _encoder.apply(_password);
            return new User(_username, encodedPassword, _enabled, !_expired, !_locked);
        }
    }
}
