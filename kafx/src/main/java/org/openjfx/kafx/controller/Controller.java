package org.openjfx.kafx.controller;

import java.net.URL;

import org.openjfx.kafx.view.pane.MenuBarMessage;

import javafx.application.Application;
import javafx.stage.Stage;

public class Controller {

	protected Controller() {
	}

	public static void init(String appName) {
		Controller.appName = appName;
	}

	public static URL getStylesheetURL() {
		return Controller.class.getResource("/org/openjfx/kafx/css/kafx.css");
	}

	private static Application application;
	private static Stage primaryStage;
	private static MenuBarMessage menuBar;
	private static String appName = "KAFX";

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

	public static String getAppName() {
		return appName;
	}

}
