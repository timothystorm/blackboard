package org.storm.periscope.domain

import org.junit.Test

import static org.junit.Assert.*

class UserTest {
    @Test
    void object_methods() throws Exception {
        def user = new User("user1", "secret");

        // equals
        assertEquals(user, user);
        assertEquals(user, new User("user1", "secret"));
        assertEquals(user, user.withPassword("user2"));

        // hashCode
        assertEquals(user.hashCode(), user.hashCode());
        assertEquals(user.hashCode(), new User("user1", "secret").hashCode());
        assertNotEquals(user.hashCode(), user.withUsername("qwerty"));

        // toString
        String str = user.toString();
        assertTrue(str.contains("user1"));
        assertFalse(str.contains("secret"));
    }

    @Test
    void default_user() throws Exception {
        def user = new User("user1", "secret");
        assertTrue(user.enabled);
        assertTrue(user.notExpired);
        assertTrue(user.notLocked);
    }

    @Test
    void immutable() throws Exception {
        def user = new User("user1", "secret");
        assertSame(user, user);

        assertNotSame(user, user.withUsername("user2"));
        assertNotSame(user, user.withPassword("qwerty"));
    }

    @Test
    void accessors_accessors() throws Exception {
        def user = new User("user1", "secret");
        assertEquals("user1", user.username);
        assertEquals("secret", user.password);
    }

    @Test(expected = NullPointerException.class)
    void null_username() throws Exception {
        User.builder().username(null).password("secret").build();
    }

    @Test(expected = NullPointerException.class)
    void null_password() throws Exception {
        User.builder().username("user1").password(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    void empty_username() throws Exception {
        User.builder().username("").password("secret").build();
    }

    @Test
    void erase_credentials() throws Exception {
        User user = new User("user1", "secret");
        assertNotNull(user.getPassword());
        assertEquals("secret", user.getPassword());

        user.eraseCredentials();
        assertNull(user.getPassword());
    }

    @Test
    void builder() throws Exception {
        User user = User.builder()
                .username("user1")
                .password("secret")
                .enabled(true)
                .expired(false)
                .locked(false)
                .passwordEncoder({ password -> password.reverse() })
                .build();

        assertNotNull(user);
        assertEquals("user1", user.username);
        assertEquals("terces", user.password);
        assertTrue(user.enabled);
        assertTrue(user.notExpired);
        assertTrue(user.notLocked);
    }
}