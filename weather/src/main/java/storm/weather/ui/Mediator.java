package storm.weather.ui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import storm.weather.domain.Location;
import storm.weather.domain.Weather;
import storm.weather.service.WeatherService;

/**
 * Coordinates interaction of controls within the WeatherApplications. Components simply need to register themselves.
 * 
 * @author Timothy Storm
 */
@Component
public class Mediator implements DisposableBean {
	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private WeatherService _service;

	private TextField _text;
	private Button _button;
	private ObservableList<Weather> _weatherList;

	/** Timer to periodically re-load weather */
	private Timeline _timer;

	public void registerAddButton(Button button) {
		_button = button;
		_button.setOnAction(event -> addPostal());
		_button.setDefaultButton(true);
	}

	/**
	 * Registers the postal code text field. Used to get the user entered postal code.
	 * 
	 * @param text
	 */
	public void registerPostalTextField(TextField text) {
		_text = text;
	}

	public void registerWeatherView(ListView<Weather> weatherList) {
		weatherList.setItems((_weatherList = FXCollections.observableArrayList()));

		// load at startup and reload periodically
		reloadWeather();
		if (_timer == null) {
			_timer = new Timeline(new KeyFrame(Duration.minutes(10), actionEvent -> reloadWeather()));
			_timer.setCycleCount(Timeline.INDEFINITE);
			_timer.play();
		}
	}

	/**
	 * Loads any weather already persisted or refreshes weather already loaded
	 */
	protected void reloadWeather() {
		// load weather in background to prevent deadlock
		new Thread(new Task<Void>() {
			protected Void call() throws Exception {
				try {
					if (_weatherList.isEmpty()) _weatherList.addAll(_service.retrieveWeather());
					else _weatherList.replaceAll(weather -> _service.lookupWeatherFor(weather.getLocation()));
				} catch (Exception e) {
					log.error("failed to load weather list", e);
				}
				return null;
			}
		}).start();
	}

	/**
	 * Called when the user submits a new postal code.
	 */
	protected void addPostal() {
		final String postal = _text.getText();
		_text.clear();

		if (StringUtils.isNotBlank(postal)) {
			// find weather in background to prevent deadlocks
			new Thread(new Task<Void>() {
				protected Void call() throws Exception {
					Weather weather = _service.lookupWeatherFor(new Location(postal));
					if (weather != null) Platform.runLater(() -> _weatherList.add(weather));
					return null;
				}
			}).start();
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Clean up resources
	 */
	@Override
	public void destroy() throws Exception {
		if (_timer != null) _timer.stop();
	}
}
