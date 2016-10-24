package org.storm.papyrus.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.ds.PGSimpleDataSource;

public class ConfigManagerTest {
  static final String USER_HOME = System.getProperty("user.home");

  static DataSource   _dataSource;
  Configuration       _config;

  @BeforeClass
  public static void beforeClass() throws Exception {
    Properties props = new Properties();
    props.load(new FileReader(new File(USER_HOME, "opt/postgresql.properties")));

    PGSimpleDataSource ds = new PGSimpleDataSource();
    ds.setUrl(props.getProperty("url"));
    ds.setUser(props.getProperty("username"));
    ds.setPassword(props.getProperty("password"));
    _dataSource = ds;
  }

  @Before
  public void before() throws Exception {
    _config = new ConfigManager(_dataSource).getPapyrusConfiguration("papyrus");
  }

  @After
  public void after() throws Exception {
    _config = null;
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
    _config.setProperty("save.test", "test_value");
    assertEquals("test_value", _config.getProperty("save.test"));
  }
}
