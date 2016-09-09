package org.storm.syspack.domain;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Phone {
  private static final NumberFormat PHONE_FORMATTER = new DecimalFormat("0000000000");
  private Double                    _number;

  public Phone(Double number) {
    setNumber(number);
  }

  public Phone(String number) {
    setNumber(number);
  }

  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof Phone)) return false;
    if (obj == this) return true;
    return new EqualsBuilder().append(getNumber(), ((Phone) obj).getNumber()).isEquals();
  }

  public Double getNumber() {
    return Double.valueOf(_number);
  }

  public String getNumberString() {
    return PHONE_FORMATTER.format(_number);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 31).append(getNumber()).toHashCode();
  }

  public void setNumber(Double number) {
    _number = number;
  }

  public void setNumber(String number) {
    setNumber(Double.valueOf(number));
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("phone", getNumberString()).toString();
  }
}
