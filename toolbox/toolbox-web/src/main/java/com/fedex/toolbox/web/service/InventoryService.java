package com.fedex.toolbox.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fedex.toolbox.core.dao.MachineDao;
import com.fedex.toolbox.core.domain.Environment;
import com.fedex.toolbox.core.domain.Machine;

@Service
public class InventoryService {
    @Autowired
    private MachineDao _machineDao;
    
    @Transactional
    public void persist(Environment... envs) {
        if (envs == null || envs.length <= 0) return;

        for (Environment e : envs) {
            _machineDao.createMachines(e.getMachines());
        }
    }

    public Collection<Environment> fetch() {
        final Collection<Machine> machines = _machineDao.readMachines();
        if(machines.isEmpty()) return null;
        
        List<Environment> envs = new ArrayList<>();
        envs.add(new Environment("TEST").machines(machines));
        return envs;
    }
}
