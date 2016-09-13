package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WEB_APPL_DEFAULTS")
public class WebApplDefaults extends FxfPhoneDao {

  WebApplDefaults(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM web_appl_defaults WHERE fk_phone_nbr IN(:phones)";
  }
}
