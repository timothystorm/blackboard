package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WWW_SESSION_ARG")
public class WwwSessionArg extends FxfNoParamDao {

  public WwwSessionArg(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT a.* FROM www_session_arg a ");
    query.append("JOIN www_session s ON a.fk_session_id_txt = s.session_id_txt ");
    query.append("WHERE s.last_acc_tsp >= CURRENT_TIMESTAMP - 3 MONTHS");
    return query.toString();
  }
}
