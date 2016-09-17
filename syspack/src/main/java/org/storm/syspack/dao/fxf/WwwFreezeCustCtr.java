package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WWW_FREEZE_CUSTCTR")
public class WwwFreezeCustCtr extends AbstractFxfDao {

  public WwwFreezeCustCtr(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM www_freeze_custctr ");
    query.append("WHERE effective_date >= CURRENT_DATE - 3 YEARS");
    return query.toString();
  }
}
