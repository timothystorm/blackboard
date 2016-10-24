package org.storm.papyrus.core;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.DatabaseConfiguration;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.io.ConfigurationLogger;

/**
 * Database based configurations for retrieving and storing properties. This is a simplification of
 * {@link DatabaseConfiguration} since that implementation is too generic and is doing to much.
 * 
 * @author Timothy Storm
 */
public class PapyrusConfiguration extends AbstractConfiguration {
  private static final String CLEAR_PROP  = "DELETE FROM configurations WHERE scope = ? AND key = ?";
  private static final String COUNT_PROPS = "SELECT COUNT(*) FROM configurations WHERE scope = ?";
  private static final String GET_KEYS    = "SELECT DISTINCT key from configurations WHERE scope = ?";
  private static final String GET_PROP    = "SELECT * FROM configurations WHERE scope = ? and key = ?";
  private static final String INSERT_PROP = "INSERT INTO configurations(scope, key, value) VALUES(?,?,?)";
  private final DataSource    _dataSource;
  private final String        _scope;

  public PapyrusConfiguration(final DataSource dataSource, final String scope) {
    addErrorLogListener();
    setThrowExceptionOnMissing(false);
    setListDelimiterHandler(new DefaultListDelimiterHandler('|'));
    initLogger(new ConfigurationLogger(PapyrusConfiguration.class));

    _dataSource = dataSource;
    _scope = scope;
  }

  @Override
  protected void addPropertyDirect(final String key, final Object value) {
    try (Connection conn = _dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_PROP)) {
      pstmt.setString(1, _scope);
      pstmt.setString(2, key);
      pstmt.setObject(3, value);
      pstmt.executeUpdate();
      if(!conn.getAutoCommit()) conn.commit();
    } catch (SQLException e) {
      fireError(ConfigurationErrorEvent.WRITE, ConfigurationEvent.ADD_PROPERTY, key, value, e);
    }
  }

  @Override
  protected void clearPropertyDirect(String key) {
    try (Connection conn = _dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(CLEAR_PROP)) {
      pstmt.setString(1, _scope);
      pstmt.setString(2, key);
      pstmt.executeUpdate();
      if(!conn.getAutoCommit()) conn.commit();
    } catch (SQLException e) {
      fireError(ConfigurationErrorEvent.WRITE, ConfigurationEvent.CLEAR_PROPERTY, key, null, e);
    }
  }

  @Override
  protected boolean containsKeyInternal(String key) {
    try (Connection conn = _dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(GET_PROP)) {
      pstmt.setString(1, _scope);
      pstmt.setString(2, key);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null, e);
    }
    return false;
  }

  private Object convertClob(Clob clob) throws SQLException {
    int len = (int) clob.length();
    return (len > 0) ? clob.getSubString(1, len) : "";
  }

  @Override
  protected Iterator<String> getKeysInternal() {
    final Collection<String> keys = new ArrayList<>();
    try (Connection conn = _dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(GET_KEYS)) {
      pstmt.setString(1, _scope);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          keys.add(rs.getString(1));
        }
      }
    } catch (SQLException e) {
      fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null, e);
    }
    return keys.iterator();
  }

  @Override
  protected Object getPropertyInternal(String key) {
    List<Object> results = new ArrayList<>();

    try (Connection conn = _dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(GET_PROP)) {
      pstmt.setString(1, _scope);
      pstmt.setString(2, key);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          Object value = rs.getObject("value");
          if (value instanceof Clob) value = convertClob((Clob) value);

          for (Object o : getListDelimiterHandler().parse(value)) {
            results.add(o);
          }
        }
      }
    } catch (SQLException e) {
      fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null, e);
    }

    if (!results.isEmpty()) return (results.size() > 1) ? results : results.get(0);
    return null;
  }

  @Override
  protected boolean isEmptyInternal() {
    try (Connection conn = _dataSource.getConnection(); PreparedStatement pstmt = conn.prepareStatement(COUNT_PROPS)) {
      pstmt.setString(1, _scope);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next() ? (rs.getInt(1) == 0) : true;
      }
    } catch (SQLException e) {
      fireError(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null, e);
    }
    return true;
  }
}
