package org.storm.syspack.dao.fxf;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.storm.syspack.domain.User;

import com.opencsv.CSVWriter;

/**
 * Base query that fetches records by ACCOUNTS ex. 'SELECT * FROM x WHERE account IN(:accounts)'. Implementations need to simply
 * setup a query as the example and provide an IN(:accounts) clause.
 */
public abstract class AbstractFxfAccountParamDao extends NamedParameterJdbcDaoSupport implements FxfDao {
  
  AbstractFxfAccountParamDao(JdbcTemplate template){
    setJdbcTemplate(template);
  }

  @Override
  public void loadTo(Collection<User> users, CSVWriter csv) {
    Map<String, List<String>> param = FxfDaoUtils.singletonAccountParam("accounts", users);

    getNamedParameterJdbcTemplate().query(query(), param, new ResultSetExtractor<User>() {
      public User extractData(ResultSet rs) throws SQLException, DataAccessException {
        try {
          csv.writeAll(rs, true, true);
        } catch (IOException e) {
          e.printStackTrace(System.err);
        }

        // the user data was written to the CSV so no need to return
        return null;
      }
    });
  }

  abstract String query();
}
