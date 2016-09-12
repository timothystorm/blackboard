package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ANI_CUSTOMER_XREF")
public class AniCustomerXref extends AbstractFxfPhoneParamDao {

  public AniCustomerXref(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ani_customer_xref WHERE fk_phone_number IN(:phones)";
  }
}
