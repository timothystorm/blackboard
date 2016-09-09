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
  private final DataSource       _dataSource;

  private static final String    WILDCARD = "%";

  private static final Long      YEARS    = 3L;

  /** singleton - do not access directory instead use {@link #query()} */
  private static volatile String QUERY;

  public BindPackageJdbcDao(DataSource dataSource) {
    _dataSource = dataSource;
  }

  private void assertPackagePattern(final String pattern) {
    if (pattern == null || pattern
        .isEmpty()) { throw new IllegalArgumentException("package pattern required but was '" + pattern + "'"); }
  }

  private String query() {
    if (QUERY == null) {
      synchronized (BindPackageJdbcDao.class) {
        if (QUERY == null) {
          StringBuilder sql = new StringBuilder();
          sql.append("SELECT DISTINCT ");
          sql.append("a.dname AS package_name, ");
          sql.append("a.bname AS table_name, ");
          sql.append("b.lastused, b.contoken ");
          sql.append("FROM sysibm.syspackdep a ");
          sql.append("JOIN sysibm.syspackage b ");
          sql.append("ON a.dlocation = b.location ");
          sql.append("AND a.dcollid = b.collid ");
          sql.append("AND a.dcontoken = b.contoken ");
          sql.append("WHERE a.dlocation = ' ' ");
          sql.append("AND a.btype = 'T' ");
          sql.append("AND a.dtype IN(' ', 'N', 'T', 'F') ");
          sql.append("AND a.dname LIKE(?) "); // 1
          sql.append("AND b.lastused >= CURRENT_DATE - ? YEAR "); // 2
          QUERY = sql.toString();
        }
      }
    }
    return QUERY;
  }

  @Override
  public Collection<BindPackage> find(final String pattern) {
    assertPackagePattern(pattern);

    try (Connection conn = _dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query())) {
      stmt.setString(1, pattern + WILDCARD);
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

        // use the newest bind parameters
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
