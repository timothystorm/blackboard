package storm.weather.dao;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import storm.weather.domain.Location;
import storm.weather.domain.Weather;
import wsf.cdyne.com.GetCityWeatherByZIP;
import wsf.cdyne.com.GetCityWeatherByZIPResponse;
import wsf.cdyne.com.WeatherReturn;

/**
 * <a href="http://wsf.cdyne.com/WeatherWS/Weather.asmx">CDYNE</a> implementation of {@link WeatherDao}.
 * 
 * @author Timothy Storm
 */
public class CdyneWeatherDao extends WebServiceGatewaySupport implements WeatherDao {
	private static final String CDYNE_URI = "http://wsf.cdyne.com/WeatherWS/Weather.asmx";
	private static final String WEATHER_BY_ZIP_ACTION = "http://ws.cdyne.com/WeatherWS/GetCityWeatherByZIP";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Weather read(final Location location) {
		if (location == null) throw new NullPointerException();

		// setup the request
		Weather weather = new Weather();
		weather.setLocation(location);
		GetCityWeatherByZIP request = new GetCityWeatherByZIP();
		request.setZIP(location.getPostal());

		// call the cdyne service
		GetCityWeatherByZIPResponse response = (GetCityWeatherByZIPResponse) getWebServiceTemplate()
				.marshalSendAndReceive(CDYNE_URI, request, new SoapActionCallback(WEATHER_BY_ZIP_ACTION));

		// extract the response
		WeatherReturn weatherReturn = response.getGetCityWeatherByZIPResult();
		if (weatherReturn.isSuccess()) {
			weather.setTemp(Integer.valueOf(weatherReturn.getTemperature()));
			weather.getLocation().setCity(weatherReturn.getCity());
		}

		return weather;
	}
}
