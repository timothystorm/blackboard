package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WEB_TEMPLATE_DTL")
public class WebTemplateDtl extends FxfPhoneDao {

  WebTemplateDtl(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM web_template_dtl WHERE fk_phone_nbr IN(:phones)";
  }
}
