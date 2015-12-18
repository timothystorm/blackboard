package storm.weather.dao;

import storm.weather.domain.Location;
import storm.weather.domain.Weather;

/**
 * Contract interface for managing weather data
 * 
 * @author Timothy Storm
 */
public interface WeatherDao {
	/**
	 * Reads the weather for the specified location. If the location cannot be found the weather will not have the temp
	 * configured but {@link Weather} never null.
	 * 
	 * @param location
	 *            - to search weather at
	 * @return the weather for the location
	 */
	Weather read(Location location);
}
