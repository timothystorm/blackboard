package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("SUBSCRPTN_SCOPE")
public class SubscrptnScope extends FxfPhoneDao {

  SubscrptnScope(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM subscrptn_scope WHERE fk_phone_nbr IN(:phones)";
  }
}
