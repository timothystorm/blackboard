package org.storm.syspack.dao.db2;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Level implements Serializable {
  private static final long serialVersionUID = 5216463314573336183L;
  private final String      _url, _driver, _attrs;

  Level(String driver, String url, String attrs) {
    _driver = driver;
    _url = url;
    _attrs = attrs;
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

  public String getDriver() {
    return _driver;
  }

  public String getAttributes() {
    return _attrs;
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
    str.append("props", getAttributes());
    return str.toString();
  }
}
