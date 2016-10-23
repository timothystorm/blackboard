package org.storm.papyrus.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfigManagerTest {

  static DataSource _dataSource;
  Configuration     _config;

  @Before
  public void before() throws Exception {
    _config = new ConfigManager(_dataSource).getPapyrusConfiguration("papyrus");
  }

  @After
  public void after() throws Exception {
    _config = null;
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setUrl("jdbc:h2:file:./target/papyrus");
    ds.setUser("sa");
    _dataSource = ds;
  }

  @AfterClass
  public static void afterClass() throws Exception {
    _dataSource = null;
  }

  @Test
  public void getConfigurations() throws Exception {
    assertNotNull(_config.getString("project.version"));
    assertNotNull(_config.getString("project.group"));

    // tests interpolation
    assertThat(_config.getString("project.info"), not(containsString("$")));

    assertArrayEquals(new String[] { "one", "two", "three" }, _config.getStringArray("list.test"));
    assertEquals("one|two|three", _config.getString("escape.test"));
  }

  @Test
  public void saveConfiguration() throws Exception {
    _config.setProperty("save.test", "new_test_value");
    assertEquals("new_test_value", _config.getString("save.test"));
  }
}
