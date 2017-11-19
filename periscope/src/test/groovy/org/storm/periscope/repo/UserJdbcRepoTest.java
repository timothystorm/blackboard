package org.storm.periscope.repo;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.*;
import org.storm.periscope.domain.User;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class UserJdbcRepoTest {
  static DataSource _dataSource;
  UserRepo _repo;

  @BeforeClass
  public static void init() throws Exception {
    Properties props = new Properties();
    props.load(new FileInputStream(new File(System.getProperty("user.home"), ".gradle/gradle.properties")));

    MysqlDataSource ds = new MysqlDataSource();
    ds.setURL("jdbc:mysql://localhost:3306/periscope");
    ds.setUser(props.getProperty("flyway.user"));
    ds.setPassword(props.getProperty("flyway.password"));
    _dataSource = ds;
  }

  @AfterClass
  public static void cleanUp() throws Exception {
    _dataSource = null;
  }

  @Before
  public void setUp() throws Exception {
    _repo = new UserJdbcRepo(_dataSource);
  }

  @Test
  @Ignore
  public void tets_findByUsername() throws Exception {
    User user = _repo.findByUsername("timmy");
    assertNotNull(user);
    System.out.println(user);
  }
}
