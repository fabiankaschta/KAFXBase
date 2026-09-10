package org.openjfx.kafx.controller;

import java.net.URL;
import java.util.logging.Level;

import org.openjfx.kafx.view.pane.MenuBarMessage;

import javafx.stage.Stage;
import javafx.application.Application;

public class Controller {

	protected Controller() {
	}

	public static void init(String configFileName) {
		init(null, configFileName, Level.WARNING);
	}

	public static void init(String configFileName, Level logLevel) {
		init(null, configFileName, logLevel);
	}

	public static void init(String configFilePath, String configFileName) {
		init(configFilePath, configFileName, Level.WARNING);
	}

	public static void init(String configFilePath, String configFileName, Level logLevel) {
		LogController.init(logLevel);
		ExceptionController.init();
		PropertiesController.init();
		ChangeController.init();
		if (configFilePath == null) {
			ConfigController.init(configFileName);
		} else {
			ConfigController.init(configFilePath, configFileName);
		}
		CloseController.init();
		FontSizeController.init();
		TranslationController.init();
	}

	public static URL getStylesheetURL() {
		return Controller.class.getResource("/org/openjfx/kafx/css/kafx.css");
	}

	private static Application application;
	private static Stage primaryStage;
	private static MenuBarMessage menuBar;

	public static void setApplication(Application app) {
		LogController.log(LogController.DEBUG, "setting application");
		application = app;
	}

	public static void setPrimaryStage(Stage stage) {
		LogController.log(LogController.DEBUG, "setting primary stage");
		primaryStage = stage;
	}

	public static void setMenuBar(MenuBarMessage bar) {
		LogController.log(LogController.DEBUG, "setting menu bar");
		menuBar = bar;
	}

	public static Application getApplication() {
		return application;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void setMenuBarMessage(String message) {
		if (menuBar != null) {
			menuBar.setMessage(message);
		}
	}

}
