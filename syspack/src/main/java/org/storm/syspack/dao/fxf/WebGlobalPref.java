package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WEB_GLOBAL_PREF")
public class WebGlobalPref extends FxfPhoneDao {

  WebGlobalPref(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM web_global_pref WHERE fk_phone_nbr IN(:phones)";
  }
}
