package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_PO_ITEM")
public class EcomPoItem extends FxfPhoneDao {

  public EcomPoItem(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ecom_po_item WHERE phone_nbr IN(:phones)";
  }
}
