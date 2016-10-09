package com.fedex.toolbox.core.bean;

import java.io.Serializable;
import java.util.Objects;

import org.joda.time.LocalDateTime;

import com.fedex.toolbox.core.Version;

public class Property implements Serializable {
  private static final long serialVersionUID = Version.svuid();

  public static Property create(Long id, String key, String value, LocalDateTime createdAt) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    Objects.requireNonNull(createdAt);
    return new Property(id, key, value, createdAt);
  }

  protected final LocalDateTime _createdAt;
  protected final Long          _id;

  protected final String        _key, _value;

  public Property() {
    this(null, null);
  }

  public Property(Long id, String key, String value, LocalDateTime createdAt) {
    _id = id;
    _key = key;
    _value = value;
    _createdAt = createdAt;
  }

  public Property(String key, String value) {
    this(null, key, value, null);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof Property)) return false;
    if (obj == this) return true;

    Property other = (Property) obj;
    if (!Objects.equals(_id, other._id)) return false;
    if (!Objects.equals(_key, other._key)) return false;
    if (!Objects.equals(_value, other._value)) return false;
    if (!Objects.equals(_createdAt, other._createdAt)) return false;
    return true;
  }

  public LocalDateTime getCreatedAt() {
    return _createdAt;
  }

  public Long getId() {
    return _id;
  }

  public String getKey() {
    return _key;
  }

  public String getValue() {
    return _value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(_id, _key, _value, _createdAt);
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("[");
    if (_id != null) str.append("id:").append(_id).append(", ");
    if (_key != null) str.append("key:").append(_key).append(", ");
    if (_value != null) str.append("value:").append(_value).append(", ");
    if (_createdAt != null) str.append("createdAt:").append(_createdAt).append(", ");

    // remove the last comma
    str.setLength(str.length() - 2);
    return str.append("]").toString();
  }
}
