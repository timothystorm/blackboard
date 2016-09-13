package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_PO_NBR_XREF")
public class EcomPoNbrXref extends FxfPhoneDao {
  public EcomPoNbrXref(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ecom_po_nbr_xref WHERE phone_nbr IN(:phones)";
  }
}
