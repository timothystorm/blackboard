package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_CUST_CONTACT")
public class EcomCustContact extends FxfPhoneDao {

  public EcomCustContact(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ecom_cust_contact WHERE phone_nbr IN(:phones)";
  }
}
