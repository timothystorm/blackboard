package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("SUBSCRPTN_RPT_DATA")
public class SubscrptnRptData extends FxfPhoneDao {

  SubscrptnRptData(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM subscrptn_rpt_data WHERE fk_phone_nbr IN(:phones)";
  }
}
