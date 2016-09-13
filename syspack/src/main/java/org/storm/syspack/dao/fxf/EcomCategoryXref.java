package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ECOM_CATEGORY_XREF")
public class EcomCategoryXref extends FxfPhoneDao {

  public EcomCategoryXref(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ecom_category_xref WHERE phone_number IN(:phones)";
  }
}
