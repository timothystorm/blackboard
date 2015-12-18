package storm.weather;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.ConnectionProperties;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseConfigurer;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;

import storm.weather.dao.CdyneWeatherDao;
import storm.weather.dao.WeatherDao;

/**
 * Spring configuration.
 * 
 * TODO: Externalize properties
 * 
 * @author Timothy Storm
 */
@ComponentScan({ "storm.weather" })
@Configuration
public class AppConfig {
	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * @return {@link JdbcTemplate}
	 */
	@Bean
	public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	/**
	 * @return {@link NamedParameterJdbcTemplate}
	 */
	@Bean
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * JAXB marshaller for "weather.wsdl"
	 * 
	 * @return ${jaxb2Marshaller}
	 */
	@Bean
	public Jaxb2Marshaller getWeatherMarshaller() {
		if(log.isDebugEnabled()) log.debug("configuring jaxb marshaller starting...");
		
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("wsf.cdyne.com");
		
		if(log.isDebugEnabled()) log.debug("configuring jaxb marshaller finished.");
		return marshaller;
	}

	/**
	 * WeatherDao that connects to the CDYN weather service.
	 * 
	 * @param marshaller
	 *            - for un/marshalling request/responses form CDYN weather service
	 * @return {@link WeatherDao}
	 */
	@Bean
	public WeatherDao getWeatherDao(Jaxb2Marshaller marshaller) {
		if(log.isDebugEnabled()) log.debug("configuring weather dao starting...");
		
		CdyneWeatherDao dao = new CdyneWeatherDao();
		dao.setDefaultUri("http://ws.cdyne.com/WeatherWS");
		dao.setMarshaller(marshaller);
		dao.setUnmarshaller(marshaller);
		
		if(log.isDebugEnabled()) log.debug("configuring weather dao finished.");
		return dao;
	}

	/**
	 * @return HSQL data source
	 */
	@Bean
	public DataSource getDataSource() {
		if(log.isDebugEnabled()) log.debug("configuring datasource starting...");
		
		ResourceLoader resourceLoader = new DefaultResourceLoader();

		// add load scripts
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(resourceLoader.getResource("db/schema.sql"));

		// create the factory and generate the DB
		EmbeddedDatabaseFactory factory = new EmbeddedDatabaseFactory();
		factory.setDatabaseName("weather");
		factory.setDatabasePopulator(populator);
		factory.setDatabaseConfigurer(PersistentHsqlConfigurer.getInstance());
		
		if(log.isDebugEnabled()) log.debug("configuring datasource finished.");
		return factory.getDatabase();
	}

	/**
	 * Similar to {@link HsqlEmbeddedDatabaseConfigurer} but instead of creating an in memory data base this configurer
	 * creates persisted datastore located in the user's home directory.
	 * 
	 * @author Timothy Storm
	 */
	private static class PersistentHsqlConfigurer implements EmbeddedDatabaseConfigurer {
		protected final Log log = LogFactory.getLog(getClass());

		/** Singleton */
		private static PersistentHsqlConfigurer _instance;

		private final String _path = "jdbc:hsqldb:file:" + System.getProperty("user.home");;

		private final Class<? extends Driver> _driverClass;

		/**
		 * @return singleton instance of the configurer
		 */
		@SuppressWarnings("unchecked")
		public static PersistentHsqlConfigurer getInstance() {
			try {
				if (_instance == null) {
					synchronized (PersistentHsqlConfigurer.class) {
						if (_instance == null) {
							_instance = new PersistentHsqlConfigurer((Class<? extends Driver>) ClassUtils
									.forName("org.hsqldb.jdbcDriver", PersistentHsqlConfigurer.class.getClassLoader()));
						}
					}
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("DataSource failure", e);
			}
			return _instance;
		}

		private PersistentHsqlConfigurer(Class<? extends Driver> driverClass) {
			_driverClass = driverClass;
		}

		/**
		 * {@inheritDoc}
		 */
		public void configureConnectionProperties(ConnectionProperties properties, String databaseName) {
			properties.setDriverClass(_driverClass);
			properties.setUrl(_path + "/" + databaseName);
			properties.setUsername("sa");
			properties.setPassword("");
		}

		/**
		 * {@inheritDoc}
		 */
		public void shutdown(DataSource dataSource, String databaseName) {
			Connection con = null;
			try {
				con = dataSource.getConnection();
				con.createStatement().execute("SHUTDOWN");
			} catch (SQLException ex) {
				log.warn("Could not shut down embedded database", ex);
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (Throwable ex) {
						log.debug("Could not close JDBC Connection on shutdown", ex);
					}
				}
			}
		}

	}
}
