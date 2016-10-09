package com.fedex.toolbox.web;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import com.fedex.toolbox.core.dao.ConfigDao;
import com.fedex.toolbox.core.dao.ConfigPostgresDao;

@Configuration
@PropertySource("classpath:database.properties")
public class SpringConfig {

  @Value("${jndi.name}")
  private String JNDI_NAME;

  @Bean(destroyMethod = "" /* fixes a defect with WL jndi entries being deleted when the context is reloaded */)
  public DataSource dataSource() {
    JndiDataSourceLookup jndi = new JndiDataSourceLookup();
    jndi.setResourceRef(false);
    return jndi.getDataSource(JNDI_NAME);
  }

  @Bean
  public ConfigDao configDao(final DataSource dataSource) {
    return new ConfigPostgresDao(dataSource);
  }
}
