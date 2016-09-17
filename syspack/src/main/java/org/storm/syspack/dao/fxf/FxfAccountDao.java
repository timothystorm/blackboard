package org.storm.syspack.dao.fxf;

import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.storm.syspack.domain.User;

/**
 * Base DAO that fetches records by ACCOUNTS. Implementations need to provide a {@link #query()} with an 'IN(:accounts)'
 * clause.
 */
public abstract class FxfAccountDao extends AbstractFxfDao {
  public FxfAccountDao(JdbcTemplate template) {
    super(template);
  }

  /**
   * Converts a collection of {@link User}s to a singleton map (one key, many values) with unique account numbers
   * 
   * @param users
   *          - to extract account numbers from
   * @return singleton map of unique account numbers
   */
  @Override
  protected SqlParameterSource params(Collection<User> users) {
    // map accounts into list
    List<String> accounts = users.stream().flatMap((usr) -> {
      return usr.getAccounts().stream();
    }).distinct().collect(Collectors.toList());

    // convert to SqlParameterSource
    return new MapSqlParameterSource().addValue("accounts", accounts, Types.VARCHAR);
  }
}
