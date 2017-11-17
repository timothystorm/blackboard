package org.storm.periscope.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void test_object_methods() throws Exception {
        User u = new User("user1", "secret");
        assertTrue(u.equals(u));
        assertEquals(u.hashCode(), u.hashCode());
    }

    @Test
    public void test_accessors_mutators() throws Exception {
        User u = new User("user1", "secret");
        assertEquals("user1", u.getUsername());
        assertEquals("secret", u.getPassword());
    }

    @Test(expected = NullPointerException.class)
    public void test_null_username() throws Exception {
        User.builder().username(null).password("secret").build();
    }

    @Test(expected = NullPointerException.class)
    public void test_null_password() throws Exception {
        User.builder().username("user1").password(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_empty_username() throws Exception {
        User.builder().username("").password("secret").build();
    }

    @Test
    public void test_equals() throws Exception {
        User u1 = User.builder().username("user1").password("secret").build();
        assertEquals("same username should be equal", u1, u1);
        assertEquals("same username different password should be equal", u1, u1.withPassword("supersecret"));
        assertNotEquals("different username should NOT be equal", u1, u1.withUsername("user2"));
    }
}