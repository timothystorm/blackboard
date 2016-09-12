package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_CUST_CONTACT")
public class EcomCustContact extends AbstractFxfPhoneParamDao {

  public EcomCustContact(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ECOM_CUST_CONTACT WHERE PHONE_NBR IN(:phones)";
  }
}
