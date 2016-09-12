package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_PO_NBR_XREF")
public class EcomPoNbrXref extends AbstractFxfPhoneParamDao {
  public EcomPoNbrXref(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ECOM_PO_NBR_XREF WHERE PHONE_NBR IN(:phones)";
  }
}
