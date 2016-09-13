package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_CUST_ITEM")
public class EcomCustItem extends FxfPhoneDao {
  
  public EcomCustItem(JdbcTemplate template){
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ecom_cust_item WHERE phone_nbr IN(:phones) AND change_tsp <= CURRENT_TIMESTAMP - 3 YEAR";
  }
}
