package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("SUBSCRPTN_RECIP")
public class SubscrptnRecip extends FxfPhoneDao {

  SubscrptnRecip(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM subscrptn_recip WHERE fk_phone_nbr IN(:phones)";
  }
}
