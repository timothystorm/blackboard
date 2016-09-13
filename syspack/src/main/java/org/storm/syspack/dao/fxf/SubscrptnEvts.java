package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("SUBSCRPTN_EVTS")
public class SubscrptnEvts extends FxfPhoneDao {

  SubscrptnEvts(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM subscrptn_evts WHERE fk_phone_nbr IN(:phones)";
  }
}
