package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_CUST_ADDRESS")
public class EcomCustAddress extends FxfPhoneDao {
  public EcomCustAddress(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ecom_cust_address WHERE phone_nbr IN(:phones)";
  }
}
