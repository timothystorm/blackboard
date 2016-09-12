package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_CUST_ADDRESS")
public class EcomCustAddress extends AbstractFxfPhoneParamDao {
  public EcomCustAddress(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ECOM_CUST_ADDRESS WHERE PHONE_NBR IN(:phones)";
  }
}
