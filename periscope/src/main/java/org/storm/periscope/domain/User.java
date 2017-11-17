package org.storm.periscope.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable user details.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 5026142403543143203L;
    private final String _username, _salt;
    private final boolean _enabled;
    private final transient String _password; /* not file to allow for erasure */

    /**
     * Construct a <code>User</code>.
     *
     * @param username - user principal
     * @param password - user credentials
     * @param enabled  - set to <code>true</code> if the user is enabled
     * @throws IllegalArgumentException if a <code>null</code> value was passed for the username or password
     */
    private User(String username, String password, String salt, boolean enabled) {
        if (username == null || username.trim().isEmpty() || password == null) {
            throw new IllegalArgumentException("username and password required");
        }
        _username = username;
        _password = password;
        _salt = salt;
        _enabled = enabled;
    }

    /**
     * Constructs a basic <code>User</code> with no salt and disabled
     *
     * @param username - user principal
     * @param password - user credentials
     * @return constructed <code>User</code>
     */
    public static User with(String username, String password) {
        return new User(username, password, null, false);
    }

    public String getUsername() {
        return _username;
    }

    public User withUsername(String username) {
        return new User(username, _password, _salt, _enabled);
    }

    public String getPassword() {
        return _password;
    }

    public User withPassword(String password) {
        return new User(_username, password, _salt, _enabled);
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public User enabled() {
        return new User(_username, _password, _salt, true);
    }

    public User disabled() {
        return new User(_username, _password, _salt, false);
    }

    @Override
    public int hashCode() {
        return _username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) return _username.equals(((User) obj)._username);
        return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString()).append(": ");
        str.append("username: ").append(_username).append("; ");
        str.append("password: [PROTECTED]; ");
        str.append("enabled: ").append(_enabled);
        return str.toString();
    }

    public static class Builder {
        private String _username, _password, _salt;
        private boolean _enabled = false;

        private Builder() {
        }

        /**
         * Populates the username. This attribute is required.
         *
         * @param username - the username. Cannot be null.
         * @return this {@link User.Builder} for method chaining
         */
        public User.Builder username(String username) {
            Objects.requireNonNull(username, "username required");
            _username = username;
            return this;
        }

        /**
         * Populates the password. This attribute is required.
         *
         * @param password - password the password. Cannot be null.
         * @return this {@link User.Builder} for method chaining
         */
        public User.Builder password(String password) {
            Objects.requireNonNull(password, "password required");
            _password = password;
            return this;
        }

        /**
         * Defines if the account is enabled or not. Default is false.
         *
         * @param enabled - false if the account is disabled, true othewise
         * @return this {@link User.Builder} for method chaining
         */
        public User.Builder enabled(boolean enabled) {
            _enabled = enabled;
            return this;
        }

        public User.Builder salt(String salt) {
            _salt = salt;
            return this;
        }

        public User build() {
            return new User(_username, _password, _salt, _enabled);
        }
    }
}
