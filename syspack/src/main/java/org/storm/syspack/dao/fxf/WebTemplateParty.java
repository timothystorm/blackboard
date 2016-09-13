package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WEB_TEMPLATE_PARTY")
public class WebTemplateParty extends FxfPhoneDao {

  WebTemplateParty(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM web_template_party WHERE fk_phone_nbr IN(:phones)";
  }
}
