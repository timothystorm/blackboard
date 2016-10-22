package org.storm.papyrus.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.storm.papyrus.core.ConfigManager;

public class ConfigManagerTest {

  static DataSource _dataSource;

  @BeforeClass
  public static void init() throws Exception {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setUrl("jdbc:h2:file:./target/papyrus");
    ds.setUser("sa");
    _dataSource = ds;
  }

  @AfterClass
  public static void cleanUp() throws Exception {
    _dataSource = null;
  }

  @Test
  public void getConfigurations() throws Exception {
    Configuration config = ConfigManager.getInstance()
                                        .getDatabaseConfiguration("dev", _dataSource);
    assertEquals("hello world", config.getString("string.message"));
    assertArrayEquals(new String[]{"this", "that", "another"}, config.getStringArray("array.values"));
    assertEquals("only 10 retries allowed", config.getString("intorpolated.value"));
  }
}
