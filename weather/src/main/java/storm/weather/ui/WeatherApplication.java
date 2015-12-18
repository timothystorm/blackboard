package storm.weather.ui;

import static java.lang.String.format;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import storm.weather.AppConfig;
import storm.weather.Version;
import storm.weather.domain.Location;
import storm.weather.domain.Weather;

/**
 * Main entry point into weather application.
 * 
 * <ul>
 * <li>Starts up Spring context</li>
 * <li>Gets configured {@link Mediator}</li>
 * <li>Builds UI and registers components with the mediator</li>
 * <li>Runs the JavaFX application</li>
 * </ul>
 * 
 * @author Timothy Storm
 */
public class WeatherApplication extends Application {
	private static final int PREF_HEIGHT = 400;

	private static final int PREF_WIDTH = 310;

	public static void main(String[] args) {
		launch(args);
	}

	/** Spring context */
	private final ApplicationContext _cntx;

	/** Coordinates the controls */
	private final Mediator _mediator;

	public WeatherApplication() {
		_cntx = new AnnotationConfigApplicationContext(AppConfig.class);
		_mediator = _cntx.getBean(Mediator.class);
	}

	/**
	 * @return button bar pane
	 */
	private Node buildButtonBar() {
		// build new pane for input controls
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: #336699;");

		// setup postal text field
		TextField postal = new TextField();
		hbox.getChildren().add(postal);
		_mediator.registerPostalTextField(postal);

		// setup add button
		Button addButton = new Button();
		addButton.setText("Add ZIP");
		hbox.getChildren().add(addButton);
		_mediator.registerAddButton(addButton);

		return hbox;
	}

	/**
	 * Assembles the different components onto a single layout
	 * 
	 * @return root pane
	 */
	private Pane buildRootPane() {
		BorderPane border = new BorderPane();
		border.setLeft(buildWeatherView());
		border.setBottom(buildButtonBar());
		return border;
	}

	/**
	 * @return weather list pane to be added to the stage
	 */
	private Node buildWeatherView() {
		ListView<Weather> weatherView = new ListView<>();
		weatherView.setPrefWidth(PREF_WIDTH);
		weatherView.setStyle("-fx-font-size: 10pt; -fx-font-family: monospace; -fx-font-weight: bold");
		weatherView.setCellFactory(view -> new ListCell<Weather>() {
			protected void updateItem(Weather item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) setText(null);
				else {
					Location loc = item.getLocation();
					Platform.runLater(() -> setText(
							format("%-6s%-23s%d \u2109", loc.getPostal(), loc.getCity(), item.getTemp())));
				}
			}
		});
		_mediator.registerWeatherView(weatherView);
		return weatherView;
	}

	/**
	 * Add the UI components to the stage
	 * 
	 * @param stage
	 */
	private void init(Stage stage) {
		stage.setTitle(format("MyWeather %s.%s", Version.majorVersion(), Version.minorVersion()));
		stage.setWidth(PREF_WIDTH);
		stage.setHeight(PREF_HEIGHT);
		stage.setResizable(false);

		// add scenes to the stage
		Scene scene = new Scene(new Group());
		scene.setRoot(buildRootPane());
		stage.setScene(scene);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(Stage stage) throws Exception {
		init(stage);
		stage.show();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() throws Exception {
		if (_cntx != null) ((ConfigurableApplicationContext) _cntx).close();
	}
}
