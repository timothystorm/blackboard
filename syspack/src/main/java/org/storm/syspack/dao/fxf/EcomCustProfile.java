package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_CUST_PROFILE")
public class EcomCustProfile extends FxfPhoneDao {

  public EcomCustProfile(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ecom_cust_profile WHERE phone_nbr IN(:phones)";
  }
}
