package org.storm.syspack.dao.fxf;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("ALIAS_ACCOUNT")
public class AliasAccount extends AbstractFxfPhoneParamDao {
  public AliasAccount(JdbcTemplate template) {
    super(template);
  }

  @Override
  String query() {
    return "SELECT * FROM ALIAS_ACCOUNT WHERE FK_PHONE_NUMBER IN(:phones)";
  }
}
