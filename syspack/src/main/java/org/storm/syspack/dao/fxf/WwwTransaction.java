package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WWW_TRANSACTION")
public class WwwTransaction extends AbstractFxfDao {

  public WwwTransaction(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT t.* FROM www_transaction t ");
    query.append("JOIN www_session s ON t.fk_session_id_txt = s.session_id_txt ");
    query.append("WHERE s.last_acc_tsp >= CURRENT_TIMESTAMP - 3 MONTHS");
    return query.toString();
  }
}
