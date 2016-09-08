package org.storm.syspackage.domain;

import static org.apache.commons.lang3.ArrayUtils.contains;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Contains the elements of a SysPackage bind information
 * 
 * @author Timothy Storm
 */
public class SysPackage implements Comparable<SysPackage>, Iterable<Entry<String, List<String>>> {
  private LocalDate                 _lastUsed;
  private String                    _name, _contoken;

  // key: qualifier, value:tables
  private Map<String, List<String>> _packages = new LinkedHashMap<>();

  public SysPackage() {}

  public SysPackage(String name) {
    setName(name);
  }

  public SysPackage(String name, String tableName, String qualifier) {
    setName(name);
    addTable(tableName, qualifier);
  }

  public void addTable(String tableName, String qualifier) {
    List<String> tables = _packages.get(qualifier);
    if (tables == null) _packages.put(qualifier, (tables = new ArrayList<>()));
    tables.add(tableName);
  }

  @Override
  public int compareTo(SysPackage other) {
    CompareToBuilder compare = new CompareToBuilder();
    compare.append(getName(), other.getName());
    compare.append(getPackages(), other.getPackages());
    compare.append(getContoken(), other.getContoken());
    compare.append(getLastUsed(), other.getLastUsed());
    return compare.toComparison();
  }

  public String getContoken() {
    return _contoken;
  }

  public LocalDate getLastUsed() {
    return _lastUsed;
  }

  public String getName() {
    return _name;
  }

  public Map<String, List<String>> getPackages() {
    return Collections.unmodifiableMap(_packages);
  }

  /**
   * @return
   */
  public Collection<String> getQualifiers() {
    return Collections.unmodifiableCollection(_packages.keySet());
  }

  /**
   * @return all distinct tables in this SysPackage
   */
  public Collection<String> getTables() {
    Set<String> unique = new TreeSet<>();
    _packages.values().forEach((t) -> {
      t.forEach(unique::add);
    });
    return unique;
  }

  /**
   * All tables with the specified qualifier
   * 
   * @param qualifiers
   *          - filters tables linked with the qualifier
   * @return all tables linked to the qualifier
   */
  public Collection<String> getTablesFor(final String... qualifiers) {
    Set<String> unique = new TreeSet<>();
    _packages.entrySet().stream().filter((entry) -> contains(qualifiers, entry.getKey()))
        .map((entry) -> entry.getValue()).forEach(unique::addAll);
    return unique;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hash = new HashCodeBuilder(7, 31);
    hash.append(getName());
    hash.append(getPackages());
    return hash.toHashCode();
  }

  @Override
  public Iterator<Entry<String, List<String>>> iterator() {
    return getPackages().entrySet().iterator();
  }

  public void setContoken(String contoken) {
    _contoken = contoken;
  }

  public void setLastUsed(LocalDate lastUsed) {
    _lastUsed = lastUsed;
  }

  public void setName(String name) {
    _name = name;
  }

  @Override
  public String toString() {
    ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
    str.append("name", getName());
    str.append("packages", getPackages());
    str.append("contoken", getContoken());
    str.append("lastUsed", getLastUsed());
    return str.toString();
  }
}
