package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("SUBSCRPTN_ACCTS")
public class SubscrptnAccts extends FxfPhoneDao {

  SubscrptnAccts(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM subscrptn_accts WHERE fk_phone_nbr IN(:phones)";
  }
}
