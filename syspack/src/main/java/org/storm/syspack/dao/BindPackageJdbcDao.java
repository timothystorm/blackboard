package org.storm.syspack.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.storm.syspack.domain.BindPackage;

@Repository
public class BindPackageJdbcDao extends JdbcDaoSupport implements BindPackageDao {
  protected Logger log = LoggerFactory.getLogger(getClass());
  
  private static final String    WILDCARD = "%";

  /** Number of years previous to search for active bind packages */
  private static final Long      YEARS    = 3L;

  /** singleton - do not access directory instead use {@link #query()} */
  private static volatile String QUERY;

  public BindPackageJdbcDao(final JdbcTemplate template) {
    setJdbcTemplate(template);
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
          sql.append("AND a.dname LIKE(?) "); // String:1
          sql.append("AND b.lastused >= CURRENT_DATE - ? YEAR "); // Integer:2
          QUERY = sql.toString();
        }
      }
    }
    return QUERY;
  }

  @Override
  public Collection<BindPackage> find(final String pattern) {
    assertPackagePattern(pattern);
    
    if(log.isDebugEnabled()) log.debug("find '{}'bind package starting...", pattern);

    // setup input params
    List<Object> params = new ArrayList<>();
    params.add(pattern + WILDCARD);
    params.add(YEARS);

    // query and build response
    final Map<String, BindPackage> bindPacks = new LinkedHashMap<>();
    getJdbcTemplate().query(query(), params.toArray(), new RowCallbackHandler() {
      public void processRow(ResultSet rs) throws SQLException {
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
    });

    return bindPacks.values();
  }
}
