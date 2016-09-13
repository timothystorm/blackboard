package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ALIAS_ACCOUNT")
public class AliasAccount extends FxfPhoneDao {
  public AliasAccount(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM alias_account WHERE fk_phone_number IN(:phones)";
  }
}
