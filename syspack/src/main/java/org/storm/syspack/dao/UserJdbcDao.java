package org.storm.syspack.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.storm.syspack.domain.Phone;
import org.storm.syspack.domain.User;

@Repository
public class UserJdbcDao extends JdbcDaoSupport implements UserDao {
  private static final String QUERY = "SELECT a.phone_number, a.fcl_uuid_nbr, x.customer_number "
      + "FROM ani_customer a JOIN  ani_customer_xref x ON a.phone_number = x.fk_phone_number "
      + "WHERE cust_userid = UPPER(?)";

  public UserJdbcDao(JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  @Override
  public User read(final String username) {
    User user = new User();
    user.setUsername(username);

    getJdbcTemplate().query(QUERY, new String[] { username }, new RowCallbackHandler() {
      public void processRow(ResultSet rs) throws SQLException {
        user.setUuid(StringUtils.trimToNull(rs.getString("fcl_uuid_nbr")));
        user.setPhone(new Phone(rs.getDouble("phone_number")));
        user.addAccount(StringUtils.trimToNull(rs.getString("customer_number")));
      }
    });

    return user;
  }
}
