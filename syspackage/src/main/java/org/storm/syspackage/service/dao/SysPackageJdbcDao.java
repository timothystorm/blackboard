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

  private static final String QUERY    = "SELECT DISTINCT a.dname, a.bname, a.bqualifier " + "FROM sysibm.syspackdep a "
      + "JOIN sysibm.syspackage b " + "ON a.dlocation = b.location " + "AND a.dcollid = b.collid "
      + "AND a.dcontoken = b.contoken " + "WHERE a.dlocation = ' ' " + "AND a.btype = 'T' "
      + "AND a.dtype IN(' ', 'N', 'T', 'F') " + "AND a.dname LIKE(?) " + "AND b.lastused > CURRENT_DATE - 1 YEAR ";

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
        String name = rs.getString("dname");

        SysPackage sysPackage = packages.get(name);
        if (sysPackage == null) packages.put(name, (sysPackage = new SysPackage(name)));
        sysPackage.addTable(rs.getString("bname"), rs.getString("bqualifier"));
      }

      return packages.values();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
