package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("NATIONAL_ACCOUNTS")
public class NationalAccounts extends FxfAccountDao {

  public NationalAccounts(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM national_accounts WHERE tf_cno IN(:accounts)";
  }
}
