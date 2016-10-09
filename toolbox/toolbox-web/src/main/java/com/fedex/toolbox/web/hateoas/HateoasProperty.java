package com.fedex.toolbox.web.hateoas;

import java.net.URI;

import org.joda.time.LocalDateTime;

import com.fedex.toolbox.core.Version;
import com.fedex.toolbox.core.bean.Property;

import jersey.repackaged.com.google.common.base.Objects;

public class HateoasProperty extends Property {
  private static final long serialVersionUID = Version.svuid();

  protected final URI       _location;
  
  private final Property _decorated;
  
  public HateoasProperty(Property property, URI location) {
    _decorated = property;
    _location = location;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof HateoasProperty)) return false;
    if (obj == this) return true;
    if (!super.equals(obj)) return false;
    return Objects.equal(_location, ((HateoasProperty) obj)._location);
  }

  public URI getLocation() {
    return _location;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), _location);
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.setLength(str.length() - 1);
    return str.append("location:").append(_location.toString()).toString();
  }
  
  @Override
  public LocalDateTime getCreatedAt() {
    return _decorated.getCreatedAt();
  }
  
  @Override
  public Long getId() {
    return _decorated.getId();
  }
  
  @Override
  public String getKey() {
    return _decorated.getKey();
  }
  
  @Override
  public String getValue() {
    return _decorated.getKey();
  }
}
