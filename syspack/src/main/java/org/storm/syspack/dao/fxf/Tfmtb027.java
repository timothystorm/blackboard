package org.storm.syspack.dao.fxf;

import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("TFMTB027")
public class Tfmtb027 extends AbstractFxfDao {

  private static final String[] REC_IDS = new String[] { "WEBMSG", "NOQUOTE", "SWS-CONF", "GAMDZIP", "US-MIN",
      "U-C-ECON", "CA-MINCH", "CA-INTRA", "CA-INTER", "FDS-CONF" };

  protected Tfmtb027(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {

    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM tfmtb027 ");
    query.append("WHERE tf_recid IN(");
    for (Iterator<?> it = new ArrayIterator(REC_IDS); it.hasNext();) {
      String item = (String) it.next();
      query.append("'").append(item).append("'");

      if (it.hasNext()) query.append(",");
    }
    return query.append(")").toString();
  }

}
