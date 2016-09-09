package org.storm.syspack.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Holds the key attributes of FXF tables [uuid, phone, account]
 */
public class User {
  private String             _uuid;
  private Phone              _phone;
  private Collection<String> _accounts = new TreeSet<>();

  public void setUuid(String uuid) {
    _uuid = uuid;
  }

  public void setPhone(Phone phone) {
    _phone = phone;
  }

  public void setAccounts(String... accounts) {
    setAccounts(Arrays.asList(accounts));
  }

  public void setAccounts(Collection<String> accounts) {
    _accounts.clear();
    _accounts.addAll(accounts);
  }

  public User addAccount(String... accounts) {
    Arrays.stream(accounts).forEach(a -> _accounts.add(a));
    return this;
  }

  public String getUuid() {
    return _uuid;
  }

  public Phone getPhone() {
    return _phone;
  }

  public String[] getAccount() {
    return _accounts.toArray(new String[_accounts.size()]);
  }

  public String toString() {
    ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
    str.append("uuid", getUuid());
    str.append("phone", getPhone());
    str.append("account", getAccount());
    return str.toString();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hash = new HashCodeBuilder(17, 31);
    hash.append(getUuid());
    hash.append(getPhone());
    hash.append(getAccount());
    return hash.toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof User)) return false;
    if (obj == this) return true;

    User other = (User) obj;
    EqualsBuilder equals = new EqualsBuilder();
    equals.append(getUuid(), other.getUuid());
    equals.append(getPhone(), other.getPhone());
    equals.append(getAccount(), other.getAccount());
    return equals.isEquals();
  }
}
