package com.fedex.toolbox.core.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.fedex.toolbox.core.domain.Identity;
import com.fedex.toolbox.core.domain.Machine;

public class MachineSqliteDao extends SqliteSupport implements MachineDao {
    public MachineSqliteDao(DataSource dataSource) {
        super(dataSource);
        initTable();
    }

    protected void initTable() {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS machines (");
        {
            query.append("id UNSIGNED BIG INT PRIMARY KEY NOT NULL, ");
            query.append("host VARCHAR(255) NOT NULL");
        }
        query.append(");");
        template().execute(query.toString());
    }

    private static final RowMapper<Machine> MACHINE_ROW_MAPPER = new RowMapper<Machine>() {
        @Override
        public Machine mapRow(ResultSet rs, int rowNum) throws SQLException {
            Machine machine = new Machine();
            machine.setIdentity(new Identity(rs.getBigDecimal("id")));
            machine.setHost(rs.getString("host"));
            return machine;
        }
    };

    @Override
    public Collection<Machine> readMachines() {
        return template().query("SELECT * FROM machines", MACHINE_ROW_MAPPER);
    }

    @Override
    public void createMachines(final Collection<Machine> machines) {
        if (machines == null || machines.isEmpty()) return;

        final Iterator<Machine> machineIt = machines.iterator();
        template().batchUpdate("INSERT INTO machines(id, host) values(?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Machine machine = machineIt.next();
                ps.setBigDecimal(1, machine.getIdentity().getId());
                ps.setString(2, machine.getHost());
            }

            @Override
            public int getBatchSize() {
                return machines.size();
            }
        });
    }

    @Override
    public void createMachines(final Machine... machines) {
        if (machines == null || machines.length <= 0) return;
        createMachines(Arrays.asList(machines));
    }

    @Override
    public Machine readMachine(Identity id) {
        return template().queryForObject("SELECT * FROM machines where id = ?", new Object[] { id.getId() },
                MACHINE_ROW_MAPPER);
    }
}
