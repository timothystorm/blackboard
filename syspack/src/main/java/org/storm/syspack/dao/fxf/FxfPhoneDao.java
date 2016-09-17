package org.storm.syspack.dao.fxf;

import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.storm.syspack.domain.User;

/**
 * Base DAO that fetches records by PHONES. Implementations need to provide a {@link #query()} with an 'IN(:phones)'
 * clause.
 */
public abstract class FxfPhoneDao extends AbstractFxfDao {
  protected Logger log = LoggerFactory.getLogger(getClass());

  FxfPhoneDao(JdbcTemplate template) {
    super(template);
  }

  @Override
  protected SqlParameterSource params(Collection<User> users) {
    // map phones into list
    List<Double> phones = users.stream().map(u -> {
      return u.getPhone().getNumber();
    }).distinct().collect(Collectors.toList());

    // convert to SqlParameterSource
    return new MapSqlParameterSource().addValue("phones", phones, Types.DECIMAL);
  }
}
