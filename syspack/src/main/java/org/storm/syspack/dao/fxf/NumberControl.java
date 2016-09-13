package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("NUMBER_CONTROL")
public class NumberControl extends FxfNoParamDao {

  public NumberControl(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM number_control ");
    query.append("WHERE system_code = 'FCLP' ");
    query.append("OR (system_code IN('WEBN', 'WEBL') AND year0 = 1997 )");
    return query.toString();
  }
}
