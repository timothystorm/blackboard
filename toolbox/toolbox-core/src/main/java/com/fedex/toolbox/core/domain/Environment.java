package com.fedex.toolbox.core.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Environment implements Identifiable, Iterable<Machine> {
    private Identity            _id;

    private String              _label;

    private Collection<Machine> _machines;

    public Environment() {
        this((String) null);
    }

    public Environment(Identity id, String label, Machine... machines) {
        setIdentity(id);
        setLabel(label);
        setMachines(machines);
    }

    public Environment(String label) {
        this(new Identity.UuidIdentity(), label, (Machine[]) null);
    }

    public Environment machines(Collection<Machine> machines) {
        if (machines != null){
            for(Machine m : machines){
                getMachinesInternal().add(m.environment(this));
            }
        }
        return this;
    }

    public Environment machines(Machine... machines) {
        machines(Arrays.asList(machines));
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Environment)) return false;
        if (obj == this) return true;

        Environment other = (Environment) obj;
        EqualsBuilder equals = new EqualsBuilder();
        equals.append(getLabel(), other.getLabel());
        equals.append(getMachines(), other.getMachines());
        return equals.isEquals();
    }

    @Override
    public Identity getIdentity() {
        return _id;
    }

    public String getLabel() {
        return _label;
    }

    public Collection<Machine> getMachines() {
        return Collections.unmodifiableCollection(getMachinesInternal());
    }

    protected Collection<Machine> getMachinesInternal() {
        if (_machines == null) {
            synchronized (Environment.class) {
                if (_machines == null) _machines = Collections.synchronizedSet(new HashSet<Machine>());
            }
        }
        return _machines;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder(17, 31);
        hash.append(getLabel());
        hash.append(getMachines());
        return hash.toHashCode();
    }

    public Environment id(Identity id) {
        setIdentity(id);
        return this;
    }

    @Override
    public Iterator<Machine> iterator() {
        return getMachines().iterator();
    }

    public Environment lable(String label) {
        setLabel(label);
        return this;
    }

    public void setIdentity(Identity id) {
        _id = id;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public void setMachines(Machine... machines) {
        if (machines == null) return;
        getMachinesInternal().clear();
        machines(machines);
    }

    @Override
    public String toString() {
        ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        str.append("identity", getIdentity());
        str.append("label", getLabel());
        str.append("machines", getMachines());
        return str.toString();
    }
}
