package org.storm.syspack.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.storm.syspack.domain.BindPackage;

public class BindPackageJdbcDao implements BindPackageDao {
  private final DataSource    _dataSource;

  private static final String WILDCARD = "%";

  private static final Long   YEARS    = 3L;

  private static final String QUERY    = "SELECT DISTINCT a.dname AS package_name, a.bname AS table_name, b.lastused, b.contoken "
      + "FROM sysibm.syspackdep a JOIN sysibm.syspackage b "
      + "ON a.dlocation = b.location AND a.dcollid = b.collid AND a.dcontoken = b.contoken "
      + "WHERE a.dlocation = ' ' AND a.btype = 'T' AND a.dtype IN(' ', 'N', 'T', 'F') AND a.dname LIKE(?) AND b.lastused >= CURRENT_DATE - ? YEAR ";

  public BindPackageJdbcDao(DataSource dataSource) {
    _dataSource = dataSource;
  }

  @Override
  public Collection<BindPackage> find(final String packagePattern) {
    try (Connection conn = _dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(QUERY)) {
      stmt.setString(1, WILDCARD + packagePattern + WILDCARD);
      stmt.setLong(2, YEARS);
      ResultSet rs = stmt.executeQuery();

      Map<String, BindPackage> bindPacks = new LinkedHashMap<>();
      while (rs.next()) {
        // capture results
        String packageName = rs.getString("package_name");
        String contoken = rs.getString("contoken");
        LocalDate lastUsed = rs.getDate("lastused").toLocalDate();

        // put a new bind package in the map
        BindPackage bindPack = bindPacks.computeIfAbsent(packageName, (name) -> {
          BindPackage bp = new BindPackage(name);
          bp.setContoken(contoken);
          bp.setLastUsed(lastUsed);
          return bp;
        }).addTable(rs.getString("table_name"));

        // capture the newest bind used
        if (lastUsed.isAfter(bindPack.getLastUsed())) {
          bindPack.setLastUsed(lastUsed);
          bindPack.setContoken(contoken);
        }
      }

      return bindPacks.values();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
