package org.storm.syspack.dao.fxf;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.storm.syspack.domain.User;

import com.opencsv.CSVWriter;

/**
 * Base query that fetches records without any user arguments. Implementations need to provide a {@link #query()}.
 */
public abstract class FxfNoParamDao extends NamedParameterJdbcDaoSupport implements FxfDao {
  protected Logger _log = LoggerFactory.getLogger(getClass());
  
  protected FxfNoParamDao(JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  @Override
  public void loadTo(Collection<User> users, CSVWriter csv) {
    getJdbcTemplate().query(query(), new ResultSetExtractor<User>() {
      public User extractData(ResultSet rs) throws SQLException, DataAccessException {
        try {
          csv.writeAll(rs, true, true);
        } catch (IOException e) {
          _log.error("failed to write csv data", e);
        }

        // the user data was written to the CSV so no need to return
        return null;
      }
    });
  }

  abstract String query();
}
