package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("WEB_RATQUOTE")
public class WebRatQuote extends FxfUuidDao {

  public WebRatQuote(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM WEB_RATQUOTE ");
    query.append("WHERE FCL_UUID IN(:uuids) ");
    query.append("AND RATE_QUOTE_DATE >= CURRENT_DATE - 3 YEAR");
    return query.toString();
  }
}
