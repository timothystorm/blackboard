package org.storm.syspackage.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.storm.syspackage.domain.SysPackage;

public class SysPackageJdbcDao implements SysPackageDao {
  private final DataSource    _dataSource;

  private static final String WILDCARD = "%";

  private static final String QUERY    = "SELECT a.dname AS bind_name, a.bname AS table_name, b.qualifier, b.lastused, b.contoken "
      + "FROM sysibm.syspackdep a JOIN sysibm.syspackage b "
      + "ON a.dlocation = b.location AND a.dcollid = b.collid AND a.dcontoken = b.contoken "
      + "WHERE a.dlocation = ' ' AND a.btype = 'T' AND a.dtype IN(' ', 'N', 'T', 'F') AND a.dname LIKE(?) AND b.lastused >= CURRENT_DATE - 1 YEAR ";

  public SysPackageJdbcDao(DataSource dataSource) {
    _dataSource = dataSource;
  }

  @Override
  public Collection<SysPackage> find(final String packageName) {
    try (Connection conn = _dataSource.getConnection()) {
      Map<String, SysPackage> packages = new LinkedHashMap<>();

      PreparedStatement stmt = conn.prepareStatement(QUERY);
      stmt.setString(1, packageName + WILDCARD);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        String name = rs.getString("bind_name");

        SysPackage sysPackage = packages.computeIfAbsent(name, n -> new SysPackage(n));
        sysPackage.addTable(rs.getString("table_name"), rs.getString("qualifier"));
        sysPackage.setLastUsed(rs.getDate("lastused").toLocalDate());
        sysPackage.setContoken(rs.getString("contoken"));
      }

      return packages.values();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
