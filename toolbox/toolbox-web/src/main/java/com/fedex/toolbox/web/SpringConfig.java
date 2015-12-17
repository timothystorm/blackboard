package com.fedex.toolbox.web;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.fedex.toolbox.core.dao.MachineDao;
import com.fedex.toolbox.core.dao.MachineSqliteDao;

@Configuration
@EnableTransactionManagement
public class SpringConfig implements TransactionManagementConfigurer {
    private static DataSource _dataSource;

    @Bean
    public MachineDao machineDao() {
        return new MachineSqliteDao(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        if (_dataSource == null) {
            synchronized (SpringConfig.class) {
                if (_dataSource == null) {
                    BasicDataSource ds = new BasicDataSource();
                    ds.setDriver(new org.sqlite.JDBC());

                    Path pathToDatabase = Paths.get(System.getProperty("user.home"), "toolbox");
                    ds.setUrl("jdbc:sqlite:" + pathToDatabase.toString());
                    _dataSource = ds;
                }
            }
        }
        return _dataSource;
    }

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }
}
