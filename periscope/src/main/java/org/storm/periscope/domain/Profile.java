package org.storm.periscope.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable container of a person's details.
 */
public class Profile implements Serializable {
  private static final long serialVersionUID = 6858667830045831423L;

  private final String _email;
  private final LocalDate _birthDate;
  private final Name _name;

  public Profile(Name name, String email, LocalDate birthDate) {
    Objects.requireNonNull(name, "name required!");
    Objects.requireNonNull(email, "email required!");

    _name = name;
    _email = email;
    _birthDate = birthDate;
  }

  public String getEmail() { return _email; }

  public Profile setEmail(String email) { return new Profile(_name, email, _birthDate); }

  public Name getName() { return _name; }

  public Profile setName(Name name) { return new Profile(name, _email, _birthDate); }

  public LocalDate getBirthDate() { return _birthDate; }

  public Profile setBirthDate(LocalDate birthDate) { return new Profile(_name, _email, birthDate); }

  // TODO: create object methods [toString, hashCode, equals]
}
