package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WEB_TEMPLATE_VARS")
public class WebTemplateVars extends FxfPhoneDao {

  WebTemplateVars(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM web_template_vars WHERE fk_phone_nbr IN(:phones)";
  }
}
