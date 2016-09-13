package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("SUBSCRPTN_SCHED")
public class SubscrptnSched extends FxfPhoneDao {

  SubscrptnSched(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM subscrptn_sched WHERE fk_phone_nbr IN(:phones)";
  }
}
