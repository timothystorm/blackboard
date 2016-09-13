package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("CUSTOMER_PROFILE")
public class CustomerProfile extends FxfAccountDao {
  
  public CustomerProfile(JdbcTemplate template){
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM customer_profile WHERE customer_number IN(:accounts)";
  }
}
