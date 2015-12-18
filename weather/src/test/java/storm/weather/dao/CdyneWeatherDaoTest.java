package storm.weather.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import storm.weather.AppConfig;
import storm.weather.domain.Location;
import storm.weather.domain.Weather;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class }, loader = AnnotationConfigContextLoader.class)
public class CdyneWeatherDaoTest {
	@Autowired
	WeatherDao _dao;

	@Test
	public void test_getWeather() throws Exception {
		Weather weather = _dao.read(new Location("80920"));
		assertNotNull(weather);
		assertNotNull(weather.getTemp());
	}

	@Test(expected = NullPointerException.class)
	public void test_getWeather_null() throws Exception {
		_dao.read(null);
	}
	
	@Test
	public void test_getWeather_invalid() throws Exception{
		Weather weather = _dao.read(new Location("ABCDE"));
		assertNotNull(weather);
		assertNull(weather.getTemp());
	}
}
