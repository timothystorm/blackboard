package org.storm.syspack;

import java.beans.PropertyVetoException;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.storm.syspack.dao.fxf.FxfDaoFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Application configuration. The {@link Registry} is used to capture the user's credentials for DataSource setup. So
 * the user's credentials should be populated in the {@link Registry} before this class is loaded.
 * 
 * @author Timothy Storm
 * @see Registry
 */
@Configuration
@PropertySource("classpath:config.properties")
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

  @Value("${db2.driver}")
  public String _db2Driver;

  @Value("${db2.url}")
  public String _defaultDb2Url;

  @Bean
  public DataSource dataSource() {
    String username = Registry.getUsername();
    String password = Registry.getPassword();
    String db2Url = Registry.getDb2Url();

    try {
      ComboPooledDataSource ds = new ComboPooledDataSource();
      ds.setDriverClass(_db2Driver);
      ds.setJdbcUrl(db2Url == null ? _defaultDb2Url : db2Url);
      ds.setUser(username);
      ds.setPassword(password);
      ds.setAcquireRetryAttempts(1);
      return ds;
    } catch (PropertyVetoException e) {
      StringBuilder msg = new StringBuilder("Failed to create DataSource");
      if (username == null || password == null) {
        msg.append(" - username & password must be configured in Registry!");
      }
      throw new BeanCreationException(msg.toString(), e);
    }
  }

  @Bean
  public ServiceLocatorFactoryBean fxfDaoServiceFactoryBean() {
    ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
    bean.setServiceLocatorInterface(FxfDaoFactory.class);
    return bean;
  }

  @Bean
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(dataSource());
  }
}
