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
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.storm.syspack.dao.FxfDao;
import org.storm.syspack.domain.User;

import com.opencsv.CSVWriter;

/**
 * Based DAO for executing queries and writing the results to a {@link CSVWriter}.
 * 
 * @author Timothy Storm
 */
public abstract class AbstractFxfDao extends NamedParameterJdbcDaoSupport implements FxfDao {

  protected Logger log = LoggerFactory.getLogger(getClass());

  public AbstractFxfDao(JdbcTemplate template) {
    setJdbcTemplate(template);
  }

  @Override
  public void load(Collection<User> users, CSVWriter toCsv) {
    if (log.isDebugEnabled()) log.debug("Loading {}...", getClass());

    getNamedParameterJdbcTemplate().query(query(), params(users), new ResultSetExtractor<User>() {
      public User extractData(ResultSet rs) throws SQLException, DataAccessException {
        try {
          toCsv.writeAll(rs, true);
        } catch (IOException e) {
          log.error("load failure", e);
        }

        // the user data was written to the CSV so no need to return
        return null;
      }
    });
  }

  /**
   * template method to provide the query parameters that will be bound to the query.
   * 
   * @return {@link SqlParameterSource} to bind to the query
   */
  protected SqlParameterSource params(Collection<User> users) {
    return null;
  }

  /**
   * template method to provide the query to execute
   * 
   * @return SQL query to execute
   */
  abstract String query();
}
