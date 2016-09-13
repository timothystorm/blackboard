package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ANI_CONTACT")
public class AniContact extends FxfPhoneDao {

  AniContact(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ani_contact WHERE fk_phone_number IN(:phones)";
  }
}
