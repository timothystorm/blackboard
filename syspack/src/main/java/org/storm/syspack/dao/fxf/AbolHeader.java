package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ABOL_HEADER")
public class AbolHeader extends FxfAccountDao {

  public AbolHeader(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM abol_header ");
    query.append("WHERE af_customer_number IN(:accounts) ");
    return query.append("AND abol_date >= CURRENT_DATE - 3 YEAR ").toString();
  }
}
