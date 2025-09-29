package org.openjfx.kafx.controller;

import java.net.URL;

import javafx.stage.Stage;

public class Controller {

	protected Controller() {
	}

	public static void init(String configFileName) {
		LogController.init();
		ExceptionController.init();
		ConfigController.init(configFileName);
		CloseController.init();
		FontSizeController.init();
		TranslationController.init();
	}

	public static void init(String configFilePath, String configFileName) {
		LogController.init();
		ExceptionController.init();
		ConfigController.init(configFilePath, configFileName);
		CloseController.init();
		FontSizeController.init();
		TranslationController.init();
	}

	public static URL getStylesheetURL() {
		return Controller.class.getResource("/org/openjfx/kafx/css/kafx.css");
	}

	private static Stage primaryStage;

	public static void setPrimaryStage(Stage stage) {
		LogController.log(LogController.DEBUG, "setting primary stage");
		primaryStage = stage;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

}
