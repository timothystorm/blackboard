package org.storm.syspack.dao.fxf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.storm.syspack.domain.User;

@Repository("ANI_CUSTOMER_XREF")
public class AniCustomerXref extends JdbcDaoSupport implements FxfDao {

  public AniCustomerXref(final JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  @Override
  public List<Map<String, Object>> read(User... users) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Map<String, Object>> read(Collection<User> users) {
    // TODO Auto-generated method stub
    return null;
  }

}
