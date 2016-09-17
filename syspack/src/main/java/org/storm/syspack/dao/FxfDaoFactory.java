package org.storm.syspack.dao;

/**
 * Factory of {@link FxfDao} implementations. There are no instances of this factory because it uses spring's <a href=
 * "http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/beans/factory/config/ServiceLocatorFactoryBean.html">ServiceFactoryLocatorFactoryBean</a>
 * 
 * @author Timothy Storm
 */
public interface FxfDaoFactory {
  /**
   * @param tableName
   *          - name of table to be queried
   * @return an implementation of {@link FxfDao} for the requested table
   */
  FxfDao getFxfDao(String tableName);
}
