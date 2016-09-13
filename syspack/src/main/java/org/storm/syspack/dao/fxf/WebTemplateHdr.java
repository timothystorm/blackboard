package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WEB_TEMPLATE_HDR")
public class WebTemplateHdr extends FxfPhoneDao {

  WebTemplateHdr(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM web_template_hdr WHERE phone_nbr IN(:phones)";
  }
}
