package org.storm.syspackage.domain;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.collections4.multimap.UnmodifiableMultiValuedMap;
import org.apache.commons.lang3.builder.*;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Contains the elements of a SysPackage bind information
 *
 * @author Timothy Storm
 */
public class SysPackage implements Comparable<SysPackage> {
  private LocalDate _lastUsed;
  private String    _name, _contoken;

  // key: qualifier, value:tables
  private MultiValuedMap<String, String> _tables = new HashSetValuedHashMap<>();

  public SysPackage addTable(String tableName, String qualifier) {
    _tables.put(qualifier, tableName);
    return this;
  }

  public SysPackage() {}

  public SysPackage(String name) {
    setName(name);
  }

  @Override
  public int compareTo(SysPackage other) {
    CompareToBuilder compare = new CompareToBuilder();
    compare.append(getName(), other.getName());
    compare.append(getTables(), other.getTables());
    compare.append(getContoken(), other.getContoken());
    compare.append(getLastUsed(), other.getLastUsed());
    return compare.toComparison();
  }

  public String getContoken() {
    return _contoken;
  }

  public void setContoken(String contoken) {
    _contoken = contoken;
  }

  public LocalDate getLastUsed() {
    return _lastUsed;
  }

  public void setLastUsed(LocalDate lastUsed) {
    _lastUsed = lastUsed;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public MultiValuedMap<String, String> getTables() {
    return UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap(_tables);
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hash = new HashCodeBuilder(7, 31);
    hash.append(getName());
    hash.append(getTables());
    hash.append(getContoken());
    hash.append(getLastUsed());
    return hash.toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof SysPackage)) return false;
    if (obj == this) return true;

    SysPackage other = (SysPackage) obj;
    EqualsBuilder equals = new EqualsBuilder();
    equals.append(getName(), other.getName());
    equals.append(getTables(), other.getTables());
    equals.append(getContoken(), other.getContoken());
    equals.append(getLastUsed(), other.getLastUsed());
    return equals.isEquals();
  }

  @Override
  public String toString() {
    ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
    str.append("name", getName());
    str.append("tables", getTables());
    str.append("contoken", getContoken());
    str.append("lastUsed", getLastUsed());
    return str.toString();
  }

  public Collection<String> getQualifiers() {
    return _tables.keySet();
  }

  public Collection<String> getTablesFor(String qualifier) {
    return _tables.get(qualifier);
  }
}
