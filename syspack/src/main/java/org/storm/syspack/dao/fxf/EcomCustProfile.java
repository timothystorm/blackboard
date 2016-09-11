package org.storm.syspack.dao.fxf;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.storm.syspack.domain.User;

@Repository("ECOM_CUST_PROFILE")
public class EcomCustProfile extends JdbcDaoSupport implements FxfDao {
  private static final String QUERY = "SELECT DISTINCT * FROM ECOM_CUST_PROFILE WHER PHONE_NBR = ? AND CUSTOMER_NBR = ?";

  public EcomCustProfile(final JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  @Override
  public List<Map<String, Object>> read(User... users) {

    return null;
  }

  @Override
  public List<Map<String, Object>> read(Collection<User> users) {
    return null;
  }
}
