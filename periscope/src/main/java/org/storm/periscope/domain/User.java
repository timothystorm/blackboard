package org.storm.periscope.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public class User implements Serializable {
    private static final long serialVersionUID = 5026142403543143203L;
    private final String _username;
    private final transient String _password;
    private final boolean _enabled;

    /**
     * Construct a <code>User</code> that is disabled.
     *
     * @param username - user principal
     * @param password - user credentials, should be encoded (encrypted)
     * @throws IllegalArgumentException if a <code>null</code> value was passed for the username or password
     */
    public User(String username, String password) {
        this(username, password, false);
    }

    /**
     * Construct a <code>User</code>.
     *
     * @param username - user principal
     * @param password - user credentials, should be encoded (encrypted)
     * @param enabled  - set to <code>true</code> if the user is enabled
     * @throws IllegalArgumentException if a <code>null</code> value was passed for the username or password
     */
    public User(String username, String password, boolean enabled) {
        if (username == null || username.trim().isEmpty() || password == null) {
            throw new IllegalArgumentException("username and password required");
        }
        _username = username;
        _password = password;
        _enabled = enabled;
    }

    /**
     * Creates a User.Builder with a specified user name
     *
     * @param username the username to use
     * @return the User.Builder
     */
    public static User.Builder withUsername(String username) {
        return new User.Builder().username(username);
    }

    public User eraseCredentials() {
        return new User(_username, null, _enabled);
    }

    public String getUsername() {
        return _username;
    }

    public User setUsername(String username) {
        return new User(username, _password, _enabled);
    }

    public String getPassword() {
        return _password == null ? null : new String(_password);
    }

    public User setPassword(String password) {
        return new User(_username, password, _enabled);
    }

    public boolean isEnabled() {
        return _enabled;
    }

    public User setEnabled(boolean enabled) {
        return new User(_username, _password, enabled);
    }

    public User enable() {
        return setEnabled(true);
    }

    public User disable() {
        return setEnabled(false);
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
        str.append("passwword: [PROTECTED]; ");
        str.append("enabled: ").append(_enabled);
        return str.toString();
    }

    public static class Builder {
        private String _username, _password;
        private boolean _enabled = false;
        private Function<String, String> _encoder = password -> password;

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
         * Encodes the current password (if non-null) and any future passwords supplied
         * to {@link #password(String)}.
         *
         * @param encoder the encoder to use
         * @return @return this {@link User.Builder} for method chaining
         */
        public User.Builder passwordEncoder(Function<String, String> encoder) {
            Objects.requireNonNull(encoder, "encoder required");
            _encoder = encoder;
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

        public User build() {
            String encodedPassword = _encoder.apply(_password);
            return new User(_username, encodedPassword, _enabled);
        }
    }
}
