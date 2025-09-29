package org.openjfx.kafx.controller;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesController extends Controller {

	private static PropertiesController controller;

	private final Properties properties;

	protected PropertiesController() {
		this(PropertiesController.class.getResourceAsStream("/org/openjfx/kafx/kafx.properties"));
	}

	protected PropertiesController(InputStream inputStream) {
		this.properties = new Properties();
		try {
			this.properties.load(inputStream);
		} catch (Exception e) {
			ExceptionController.exception(e);
		}
	}

	public static void init() {
		init(new PropertiesController());
	}

	public static void init(InputStream inputStream) {
		init(new PropertiesController(inputStream));
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
