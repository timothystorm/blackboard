package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("SUBSCRPTN_HEADER")
public class SubscrptnHeader extends FxfPhoneDao {

  SubscrptnHeader(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM subscrptn_header WHERE phone_nbr IN(:phones)";
  }
}
