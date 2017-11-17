package org.storm.periscope.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UserTest {
    @Test(expected = IllegalArgumentException.class)
    public void test_null_username() throws Exception {
        User.with(null, "secret");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_null_password() throws Exception {
        User.with("user1", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_empty_username() throws Exception {
        User.with("", "secret");
    }

    @Test
    public void test_equals() throws Exception {
        User u1 = User.with("user1", "secret");
        assertEquals("same usernames should be equal", u1, u1);
        assertEquals("same username different password should be equal", u1, u1.withPassword("supersecret"));
        assertNotEquals("different username should NOT be equal", u1, u1.withUsername("user2"));
    }
}