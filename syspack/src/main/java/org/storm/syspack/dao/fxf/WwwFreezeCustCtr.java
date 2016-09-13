package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WWW_FREEZE_CUSTCTR")
public class WwwFreezeCustCtr extends FxfNoParamDao {

  public WwwFreezeCustCtr(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM WWW_FREEZE_CUSTCTR ");
    query.append("WHERE EFFECTIVE_DATE >= CURRENT_DATE - 3 YEARS");
    return query.toString();
  }
}
