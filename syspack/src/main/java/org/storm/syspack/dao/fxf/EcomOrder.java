package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_ORDER")
public class EcomOrder extends FxfPhoneDao {

  public EcomOrder(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ecom_order WHERE phone_nbr IN(:phones)";
  }
}
