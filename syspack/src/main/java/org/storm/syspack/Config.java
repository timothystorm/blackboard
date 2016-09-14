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
import org.storm.syspack.dao.fxf.FxfDaoFactory;

/**
 * Application configuration. The {@link Session} is used to capture the user's credentials for DataSource
 * setup. So
 * the user's credentials should be populated in the {@link Session} before this class is loaded.
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
    Session session = Session.instance();
    String username = session.get(Session.USERNAME);
    String password = session.get(Session.PASSWORD);
    Level level = session.get(Session.DB2LEVEL);

    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName(level.getDriver());
    ds.setUrl(level.getUrl());
    ds.setUsername(username);
    ds.setPassword(password);
    ds.setMinIdle(5);
    ds.setMaxIdle(10);
    ds.setConnectionProperties(level.getProps());
    return ds;
  }

  @Bean
  public ServiceLocatorFactoryBean serviceLocatorFactoryBean() {
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
