package org.openjfx.kafx.controller;

import java.io.IOException;
import java.util.Properties;

public class PropertiesController extends Controller {

	private static PropertiesController controller;

	private final Properties properties;

	protected PropertiesController() {
		this("/org/openjfx/kafx/kafx.properties");
	}

	protected PropertiesController(String path) {
		this.properties = new Properties();
		try {
			this.properties.load(PropertiesController.class.getResourceAsStream(path));
		} catch (IOException e) {
			ExceptionController.exception(e);
		}
	}

	public static void init() {
		init(new PropertiesController());
	}

	public static void init(String path) {
		init(new PropertiesController(path));
	}

	public static void init(PropertiesController controller) {
		LogController.log(LogController.DEBUG, "init properties controller");
		PropertiesController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static String getProperty(String key) {
		if (isInitialized()) {
			return controller.properties.getProperty(key);
		} else {
			return null;
		}
	}

	public static String getProperty(String key, String defaultValue) {
		if (isInitialized()) {
			return controller.properties.getProperty(key, defaultValue);
		} else {
			return null;
		}
	}

}
