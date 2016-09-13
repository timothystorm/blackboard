package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ABOL_DT_REFERENCE")
public class AbolDtReference extends FxfAccountDao {

  public AbolDtReference(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT d.* FROM abol_dt_reference d ");
    query.append("JOIN abol_header h ");
    query.append("ON d.fk_abol_headeradva = h.advance_pickup_no ");
    query.append("WHERE h.abol_date >= CURRENT_DATE - 3 YEAR ");
    query.append("AND h.af_customer_number IN(:accounts)");
    return query.toString();
  }
}
