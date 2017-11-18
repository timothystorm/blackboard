package org.storm.periscope.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Immutable person name
 */
public class Name implements Serializable {
  private static final long serialVersionUID = -3231374373260099582L;

  private final String _givenName, _familyName;
  private final String[] _middleName;

  public Name(String familyName, String givenName, String... middleName) {
    _familyName = familyName;
    _givenName = givenName;
    _middleName = middleName;
  }

  public String getFamilyName() { return _familyName; }

  public Name setFamilyName(String familyName) { return new Name(familyName, _givenName, _middleName); }

  public String getGivenName() { return _givenName; }

  public Name setGivenName(String givenName) { return new Name(_familyName, givenName, _middleName); }

  public String[] getMiddleName() { return (_middleName == null) ? new String[0] : _middleName; }

  public Name setMiddleName(String... middleName) { return new Name(_familyName, _givenName, middleName); }

  @Override
  public String toString() {
    return toString(name -> {
          StringBuilder str = new StringBuilder();
          str.append(super.toString()).append("; ");
          if (_familyName != null) str.append("familyName:").append(_familyName).append("; ");
          if (_givenName != null) str.append("givenName:").append(_givenName);

          if (_middleName != null && _middleName.length > 0) {
            str.append("; middleName:[");
            for (int i = 0; i < _middleName.length; i++) {
              str.append(_middleName[i]);

              if ((i + 1) < _middleName.length) str.append(", ");
            }
            str.append("]");
          }
          return str.toString();
        }
    );
  }

  public String toString(Function<Name, String> func) {
    return func.apply(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_familyName, _givenName, _middleName);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Name) {
      if(obj == null) return false;

      Name other = (Name) obj;
      if (!Objects.equals(_familyName, other._familyName)) return false;
      if (!Objects.equals(_givenName, other._familyName)) return false;
      if (!Arrays.equals(_middleName, other._middleName)) return false;

      return true;
    }
    return false;
  }
}
