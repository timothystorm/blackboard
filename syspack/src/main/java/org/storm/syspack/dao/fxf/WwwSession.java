package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WWW_SESSION")
public class WwwSession extends FxfNoParamDao {

  public WwwSession(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM WWW_SESSION ");
    query.append("WHERE LAST_ACC_TSP >= CURRENT_TIMESTAMP - 3 MONTHS");
    return query.toString();
  }
}
