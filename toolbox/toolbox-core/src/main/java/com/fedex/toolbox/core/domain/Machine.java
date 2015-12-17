package com.fedex.toolbox.core.domain;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Machine implements Identifiable {
    private Environment _environment;
    private Identity    _id;
    private InetAddress _inetAddress;

    public Machine() {
        this((String) null);
    }

    public Machine(Identity id, String host) {
        setIdentity(id);
        setHost(host);
    }

    public Machine(String host) {
        this(new Identity.UuidIdentity(), host);
    }

    public Machine environment(Environment environment) {
        setEnvironment(environment);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Machine)) return false;
        if (obj == this) return true;

        Machine other = (Machine) obj;
        EqualsBuilder equals = new EqualsBuilder();
        equals.append(getInetAddress(), other.getInetAddress());
        return equals.isEquals();
    }

    public Environment childOf() {
        return _environment;
    }

    @Override
    public Identity getIdentity() {
        return _id;
    }

    protected InetAddress getInetAddress() {
        return _inetAddress;
    }

    public String getHost() {
        return _inetAddress == null ? null : _inetAddress.getHostName();
    }

    public String getAddress() {
        return _inetAddress == null ? null : _inetAddress.getHostAddress();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder(17, 31);
        hash.append(getInetAddress());
        return hash.toHashCode();
    }

    public Machine id(Identity id) {
        setIdentity(id);
        return this;
    }

    public void setEnvironment(Environment environment) {
        _environment = environment;
    }

    public void setHost(String host) {
        if (host == null) return;

        try {
            _inetAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("host <" + host + "> unknown", e);
        }
    }

    public void setIdentity(Identity id) {
        _id = id;
    }

    @Override
    public String toString() {
        ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        str.append("identity", getIdentity());
        str.append("host", getHost());
        str.append("ip", getAddress());
        return str.toString();
    }
}
