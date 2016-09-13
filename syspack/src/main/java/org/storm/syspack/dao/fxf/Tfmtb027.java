package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("TFMTB027")
public class Tfmtb027 extends FxfNoParamDao {

  protected Tfmtb027(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM tfmtb027 ");
    query.append(
        "WHERE tf_recid IN('WEBMSG', 'NOQUOTE', 'SWS-CONF', 'GAMDZIP', 'US-MIN', 'U-C-ECON', 'CA-MINCH', 'CA-INTRA', 'CA-INTER', 'FDS-CONF')");
    return query.toString();
  }
}
