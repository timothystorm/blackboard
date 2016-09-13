package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ANI_CUSTOMER")
public class AniCustomer extends FxfPhoneDao {

  public AniCustomer(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ani_customer WHERE phone_number IN(:phones)";
  }
}
