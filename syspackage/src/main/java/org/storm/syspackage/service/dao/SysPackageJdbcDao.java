package org.storm.syspackage.service.dao;

import org.storm.syspackage.domain.SysPackage;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SysPackageJdbcDao implements SysPackageDao {
  private final DataSource    _dataSource;

  private static final String WILDCARD = "%";

  private static final String QUERY = "SELECT DISTINCT a.bname AS table_name, b.qualifier, b.lastused, b.contoken " + "FROM sysibm.syspackdep a JOIN sysibm.syspackage b " + "ON a.dlocation = b.location AND a.dcollid = b.collid AND a.dcontoken = b.contoken " + "WHERE a.dlocation = ' ' AND a.btype = 'T' AND a.dtype IN(' ', 'N', 'T', 'F') AND a.dname LIKE(?) AND b.lastused >= CURRENT_DATE - 1 YEAR ";

  public SysPackageJdbcDao(DataSource dataSource) {
    _dataSource = dataSource;
  }

  @Override
  public SysPackage find(final String packageName) {
    try (Connection conn = _dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(QUERY)) {
      stmt.setString(1, packageName + WILDCARD);
      ResultSet rs = stmt.executeQuery();

      SysPackage sysPack = null;
      while (rs.next()) {
        if (sysPack == null) {
          sysPack = new SysPackage(packageName);
          sysPack.setContoken(rs.getString("contoken"));
          sysPack.setLastUsed(rs.getDate("lastused").toLocalDate());
        }
        sysPack.addTable(rs.getString("table_name"), rs.getString("qualifier"));
      }

      return sysPack;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
