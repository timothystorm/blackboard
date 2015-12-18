package storm.weather.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import storm.weather.dao.LocationDao;
import storm.weather.dao.WeatherDao;
import storm.weather.domain.Location;
import storm.weather.domain.Weather;

/**
 * Coordinates operations of the weather application
 * 
 * @author Timothy Storm
 */
@Service
public class WeatherService {
	@Autowired
	private LocationDao _locationDao;

	@Autowired
	private WeatherDao _weatherDao;

	/**
	 * Clears any stored locations
	 */
	public void clear() {
		_locationDao.deleteAll();
	}

	/**
	 * Fetches the weather for a given location. If the weather is found for that location then that location is stored
	 * for future use.
	 * 
	 * @param loc
	 *            - to search
	 * @return Weather for that location or null if the locaiton is invalid or the weather can't be found.
	 */
	public Weather lookupWeatherFor(final Location loc) {
		Weather weather = _weatherDao.read(loc);

		// if weather was found then this is a valid location - save it
		if (weather.getTemp() != null) {
			_locationDao.save(loc);
			return weather;
		}
		return null;
	}

	/**
	 * @return weather for {@link Location}s previously stored.
	 * 
	 * @see #lookupWeatherFor(Location)
	 */
	public List<Weather> retrieveWeather() {
		List<Location> locs = _locationDao.readAll();
		if (CollectionUtils.isEmpty(locs)) return Collections.emptyList();
		return locs.stream().map(l -> _weatherDao.read(l)).collect(Collectors.toList());
	}
}
