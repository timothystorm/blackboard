package org.storm.syspack.db2;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Level implements Serializable {
  private static final long serialVersionUID = 5216463314573336183L;
  private final String      _url, _driver, _props;

  Level(String driver, String url, String props) {
    _driver = driver;
    _url = url;
    _props = props;
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
    equals.append(getProps(), other.getProps());
    return equals.isEquals();
  }

  public String getDriver() {
    return _driver;
  }

  public String getProps() {
    return _props;
  }

  public String getUrl() {
    return _url;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hash = new HashCodeBuilder(17, 31);
    hash.append(getDriver());
    hash.append(getUrl());
    hash.append(getProps());
    return hash.toHashCode();
  }

  @Override
  public String toString() {
    ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
    str.append("driver", getDriver());
    str.append("url", getUrl());
    str.append("props", getProps());
    return str.toString();
  }
}
