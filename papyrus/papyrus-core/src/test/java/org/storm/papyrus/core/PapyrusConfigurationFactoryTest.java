package org.storm.papyrus.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.postgresql.ds.PGSimpleDataSource;
import org.storm.papyrus.category.Integration;

@Category(Integration.class)
public class PapyrusConfigurationFactoryTest {
  static DataSource   _dataSource;

  static final String USER_HOME = System.getProperty("user.home");
  
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

  Configuration       _config;

  @After
  public void after() throws Exception {
    _config = null;
  }

  @Before
  public void before() throws Exception {
    _config = new PapyrusConfigurationFactory(_dataSource).createConfiguration("papyrus");
  }

  @Test
  public void containsKey() throws Exception {
    assertTrue(_config.containsKey("project.name"));
    assertFalse(_config.containsKey("noop.key"));
  }

  @Test
  public void getConvertedProperties() throws Exception {
    assertNotNull(_config.getString("project.name"));
    assertNotNull(_config.getString("project.group"));

    // tests interpolation
    assertThat(_config.getString("project.info"), not(containsString("$")));

    assertArrayEquals(new String[] { "one", "two", "three" }, _config.getStringArray("list.test"));
    assertEquals("one|two|three", _config.getString("escape.test"));
  }

  @Test
  public void getKeys() {
    _config.getKeys()
           .forEachRemaining(k -> assertNotNull(k));
  }

  @Test
  public void isEmpty() {
    assertFalse(_config.isEmpty());
  }
  
  @Test
  public void save() throws Exception {
    _config.setProperty("save.test", "test_value");
    assertEquals("test_value", _config.getProperty("save.test"));
  }
}
