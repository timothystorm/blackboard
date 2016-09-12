package org.storm.syspack.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Holds the key attributes of FXF tables [uuid, phone, account]
 */
public class User implements Comparable<User>, Iterable<String>, Serializable {
  private static final long serialVersionUID = -1501361777896630821L;
  private Collection<String> _accounts = new TreeSet<>();
  private Phone              _phone;
  private String             _uuid, _username;
  
  public User(){}
  
  public User(String uuid){
    setUuid(uuid);
  }

  public User addAccount(String... accounts) {
    Arrays.stream(accounts).forEach(a -> _accounts.add(a));
    return this;
  }

  @Override
  public int compareTo(User other) {
    CompareToBuilder compare = new CompareToBuilder();
    compare.append(getUsername(), other.getUsername());
    compare.append(getUuid(), other.getUuid());
    compare.append(getPhone(), other.getPhone());
    return compare.toComparison();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof User)) return false;
    if (obj == this) return true;

    User other = (User) obj;
    EqualsBuilder equals = new EqualsBuilder();
    equals.append(getUsername(), other.getUsername());
    equals.append(getUuid(), other.getUuid());
    equals.append(getPhone(), other.getPhone());
    equals.append(getAccounts(), other.getAccounts());
    return equals.isEquals();
  }

  public Collection<String> getAccounts() {
    return Collections.unmodifiableCollection(_accounts);
  }

  public Phone getPhone() {
    return _phone;
  }

  public String getUsername() {
    return _username;
  }

  public String getUuid() {
    return _uuid;
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hash = new HashCodeBuilder(17, 31);
    hash.append(getUsername());
    hash.append(getUuid());
    hash.append(getPhone());
    hash.append(getAccounts());
    return hash.toHashCode();
  }

  @Override
  public Iterator<String> iterator() {
    return getAccounts().iterator();
  }

  public void setAccounts(Collection<String> accounts) {
    _accounts.clear();
    _accounts.addAll(accounts);
  }

  public void setAccounts(String... accounts) {
    setAccounts(Arrays.asList(accounts));
  }

  public void setPhone(Phone phone) {
    _phone = phone;
  }

  public void setUsername(String username) {
    _username = username;
  }

  public void setUuid(String uuid) {
    _uuid = uuid;
  }

  public String toString() {
    ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
    str.append("username", getUsername());
    str.append("uuid", getUuid());
    str.append("phone", getPhone());
    str.append("accounts", getAccounts());
    return str.toString();
  }
}
