package org.openjfx.kafx.controller;

import java.net.URL;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import javafx.stage.Stage;

public class Controller {

	protected Controller() {
		logger.setUseParentHandlers(false);
		logger.addHandler(logHandler);
	}

	public static void init(String configFileName) {
		ExceptionController.init();
		ConfigController.init(configFileName);
		CloseController.init();
		FontSizeController.init();
		TranslationController.init();
	}

	public static void init(String configFilePath, String configFileName) {
		ExceptionController.init();
		ConfigController.init(configFilePath, configFileName);
		CloseController.init();
		FontSizeController.init();
		TranslationController.init();
	}

	@SuppressWarnings("serial")
	public static final Level DEBUG = new Level("DEBUG", Level.FINE.intValue()) {
	};

	private final static Formatter logFormatter = new Formatter() {

		@Override
		public String format(LogRecord record) {
			StringBuilder builder = new StringBuilder();
			builder.append(record.getLevel() + ": ");
			builder.append(formatMessage(record));
			builder.append(System.lineSeparator());
			return builder.toString();
		}
	};

	private final static StreamHandler logHandler = new StreamHandler(System.out, logFormatter) {

		@Override
		public synchronized void publish(LogRecord record) {
			super.publish(record);
			super.flush();
		}
	};

	private final static Logger logger = Logger.getLogger("kafx.controller.logger");

	public static void log(Level level, String message) {
		logger.log(level, message);
	}

	public static void setDebugMode(boolean enabled) {
		if (enabled) {
			logger.setLevel(Level.ALL);
			logHandler.setLevel(Level.ALL);
		} else {
			logger.setLevel(Level.OFF);
			logHandler.setLevel(Level.OFF);
		}
	}

	public static URL getStylesheetURL() {
		return Controller.class.getResource("/org/openjfx/kafx/css/kafx.css");
	}

	private static Stage primaryStage;

	public static void setPrimaryStage(Stage stage) {
		log(Controller.DEBUG, "setting primary stage");
		primaryStage = stage;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

}
