package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WWW_SESSION")
public class WwwSession extends AbstractFxfDao {

  public WwwSession(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM www_session ");
    query.append("WHERE last_acc_tsp >= CURRENT_TIMESTAMP - 3 MONTHS");
    return query.toString();
  }
}
