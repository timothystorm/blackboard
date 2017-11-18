package org.storm.periscope.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
  @Test
  public void object_methods() throws Exception {
    User u = new User("user1", "secret");

    // equals
    assertEquals(u, u);
    assertEquals(u, new User("user1", "secret"));
    assertEquals(u, u.withPassword("user2"));

    // hashCode
    assertEquals(u.hashCode(), u.hashCode());
    assertEquals(u.hashCode(), new User("user1", "secret").hashCode());
    assertNotEquals(u.hashCode(), u.withUsername("qwerty"));

    // toString
    String str = u.toString();
    assertTrue(str.contains("user1"));
    assertFalse(str.contains("secret"));
  }

  @Test
  public void default_user() throws Exception {
    User u = new User("user1", "secret");
    assertTrue(u.isEnabled());
    assertTrue(u.isNotExpired());
    assertTrue(u.isNotLocked());
  }

  @Test
  public void immutable() throws Exception {
    User u = new User("user1", "secret");
    assertSame(u, u);

    assertNotSame(u, u.withUsername("user2"));
    assertNotSame(u, u.withPassword("qwerty"));
  }

  @Test
  public void accessors_mutators() throws Exception {
    User u = new User("user1", "secret");
    assertEquals("user1", u.getUsername());
    assertEquals("secret", u.getPassword());
  }

  @Test(expected = NullPointerException.class)
  public void null_username() throws Exception {
    User.builder().username(null).password("secret").build();
  }

  @Test(expected = NullPointerException.class)
  public void null_password() throws Exception {
    User.builder().username("user1").password(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void empty_username() throws Exception {
    User.builder().username("").password("secret").build();
  }

  @Test
  public void erase_credentials() throws Exception {
    User u = new User("user1", "secret");
    assertNotNull(u.getPassword());
    assertEquals("secret", u.getPassword());

    u.eraseCredentials();
    assertNull(u.getPassword());
  }

  @Test
  public void builder() throws Exception {
    User u = User.builder()
        .username("user1")
        .password("secret")
        .enabled(true)
        .expired(false)
        .locked(false)
        .passwordEncoder(password -> (
            new StringBuffer(password).reverse().toString())
        )
        .build();

    assertNotNull(u);
    assertEquals("user1", u.getUsername());
    assertEquals("terces", u.getPassword());
    assertTrue(u.isEnabled());
    assertTrue(u.isNotExpired());
    assertTrue(u.isNotLocked());
  }
}