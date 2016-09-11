package org.storm.syspack.dao.fxf;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.storm.syspack.domain.User;

@Repository("ANI_CUSTOMER")
public class AniCustomer extends JdbcDaoSupport implements FxfDao {

  public AniCustomer(final JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  @Override
  public List<Map<String, Object>> read(User... users) {
    return read(Arrays.asList(users));
  }

  @Override
  public List<Map<String, Object>> read(Collection<User> users) {
    return null;
  }
}
