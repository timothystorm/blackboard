package org.storm.syspack;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.storm.syspack.dao.db2.Level;
import org.storm.syspack.dao.fxf.FxfDao;
import org.storm.syspack.dao.fxf.FxfDaoFactory;

/**
 * Application configuration. The {@link Session} is used to capture the user's credentials for DataSource
 * setup. So the user's credentials should be populated in the {@link Session} before this class is started.
 * 
 * @author Timothy Storm
 * @see Session
 */
@Configuration
@ComponentScan(basePackages = "org.storm.syspack")
public class Config {
  /**
   * Must be static so it is initialized before the values it holds are requested
   * 
   * @return initialized property place holder
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public DataSource dataSource() {
    Session session = Session.instance(false);
    if (session == null) throw new IllegalStateException("Session not created!");

    // capture session configurations
    String username = session.get(Session.USERNAME);
    String password = session.get(Session.PASSWORD);
    Level level = session.get(Session.DB2LEVEL);

    // build DataSource
    BasicDataSource ds = new BasicDataSource();
    ds.setConnectionProperties(level.getAttributes());
    ds.setDriverClassName(level.getDriver());
    ds.setUrl(level.getUrl());
    ds.setUsername(username);
    ds.setPassword(password);
    ds.setMinIdle(1);
    ds.setMaxIdle(Runtime.getRuntime().availableProcessors() + 1);
    ds.setValidationQuery("SELECT COUNT(*) FROM SYSDUMMY1");
    ds.setValidationQueryTimeout(3);
    return ds;
  }

  /**
   * @return a service locator that finds {@link FxfDao} based on the name of the repository/table
   */
  @Bean
  public ServiceLocatorFactoryBean fxfDaoLocatorFactory() {
    ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
    bean.setServiceLocatorInterface(FxfDaoFactory.class);
    return bean;
  }

  @Bean
  public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }

  @Bean
  public NamedParameterJdbcTemplate namedJdbcTemplate(DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }
}
