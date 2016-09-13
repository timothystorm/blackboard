package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("FXF_REGISTRATION")
public class FxfRegistration extends FxfUuidDao {

  FxfRegistration(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM fxf_registration WHERE fcl_uuid_txt IN(:uuids)";
  }
}
