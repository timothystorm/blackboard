package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("CUSTOMER_ADDRESS")
public class CustomerAddress extends FxfAccountDao {

  public CustomerAddress(final JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM customer_address WHERE fk_customer_number IN(:accounts) ";
  }
}
