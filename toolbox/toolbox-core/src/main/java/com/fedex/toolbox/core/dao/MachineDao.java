package com.fedex.toolbox.core.dao;

import java.util.Collection;

import com.fedex.toolbox.core.domain.Identity;
import com.fedex.toolbox.core.domain.Machine;

public interface MachineDao {
    public Collection<Machine> readMachines();

    public void createMachines(Machine... machines);

    public void createMachines(Collection<Machine> machines);

    public Machine readMachine(Identity id);
}
