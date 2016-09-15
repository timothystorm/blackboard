package org.storm.syspack.dao.db2;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Holds environment level information for connecting to a data source. Use {@link #builder()} to create new instances.
 * 
 * @author Timothy Storm
 */
public class Level implements Serializable {
  public static class Builder {
    private StringBuilder _attr = new StringBuilder();
    private String        _url, _driver;

    public Builder attribute(String key, String value) {
      _attr.append(key).append("=").append(value).append(";");
      return this;
    }

    public String attributes() {
      return _attr.toString();
    }

    public Level build() {
      return new Level(this);
    }

    public String driver() {
      return _driver;
    }

    public Builder driver(String driver) {
      _driver = driver;
      return this;
    }

    public Builder attribute(String keyValue) {
      _attr.append(keyValue).append(";");
      return this;
    }

    public String url() {
      return _url;
    }

    public Builder url(String url) {
      _url = url;
      return this;
    }
  }

  private static final long serialVersionUID = 5216463314573336183L;

  public static Builder builder() {
    return new Builder();
  }

  private final String _url, _driver, _attrs;

  private Level(Level.Builder builder) {
    _driver = builder.driver();
    _url = builder.url();
    _attrs = builder.attributes();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof Level)) return false;
    if (obj == this) return true;

    Level other = (Level) obj;
    EqualsBuilder equals = new EqualsBuilder();
    equals.append(getDriver(), other.getDriver());
    equals.append(getUrl(), other.getUrl());
    equals.append(getAttributes(), other.getAttributes());
    return equals.isEquals();
  }

  public String getAttributes() {
    return _attrs;
  }

  public String getDriver() {
    return _driver;
  }

  public String getUrl() {
    return _url;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hash = new HashCodeBuilder(17, 31);
    hash.append(getDriver());
    hash.append(getUrl());
    hash.append(getAttributes());
    return hash.toHashCode();
  }

  @Override
  public String toString() {
    ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
    str.append("driver", getDriver());
    str.append("url", getUrl());
    str.append("attributes", getAttributes());
    return str.toString();
  }
}
