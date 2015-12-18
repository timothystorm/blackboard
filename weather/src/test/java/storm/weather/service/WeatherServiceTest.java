package storm.weather.service;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
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
public class WeatherServiceTest {
	@Autowired
	WeatherService _service;

	@After
	public void tearDown() throws Exception {
		_service.clear();
	}

	@Test
	public void test_findWeatherFor() {
		Weather w = _service.lookupWeatherFor(new Location("80920"));
		assertNotNull(w);
	}
}
