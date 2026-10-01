package org.openjfx.kafx.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigController extends Controller {

	private static ConfigController controller;

	private File file;
	private final Properties properties = new Properties();

	protected ConfigController(String path, String fileName) {
		try {
			if (path != null) {
				File pathFile = new File(path);
				pathFile.mkdirs();
				this.file = new File(path + System.getProperty("file.separator") + fileName);
			} else {
				this.file = new File(fileName);
			}
			this.file.createNewFile();
			this.properties.load(new FileInputStream(this.file));
		} catch (IOException e) {
			ExceptionController.exception(e);
		}
	}

	public static void init() {
		String appName = Controller.getAppName().toLowerCase();
		init(new ConfigController(
				System.getProperty("user.home") + System.getProperty("file.separator") + '.' + appName,
				appName + ".cfg"));
	}

	public static void init(String fileName) {
		init(new ConfigController(System.getProperty("user.home") + System.getProperty("file.separator") + '.'
				+ Controller.getAppName().toLowerCase(), fileName));
	}

	public static void init(String path, String fileName) {
		init(new ConfigController(path, fileName));
	}

	public static void init(ConfigController controller) {
		LogController.log(LogController.DEBUG, "init config controller");
		ConfigController.controller = controller;
		putIfNotExists("WIDTH", String.valueOf(800));
		putIfNotExists("HEIGHT", String.valueOf(600));
		putIfNotExists("FONT_SIZE", String.valueOf(12));
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static boolean exists(String option) {
		if (!isInitialized()) {
			return false;
		} else {
			return get(option) != null;
		}
	}

	public static void remove(String option) {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "config remove " + option);
			controller.properties.remove(option.toString());
		}
	}

	public static String get(String option) {
		if (!isInitialized()) {
			return null;
		} else {
			return controller.properties.getProperty(option.toString());
		}
	}

	public static void set(String option, String value) {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "config set " + option + " to " + value);
			controller.properties.setProperty(option.toString(), value);
		}
	}

	public static void putIfNotExists(String option, String value) {
		if (isInitialized()) {
			if (!exists(option)) {
				set(option.toString(), value);
			}
		}
	}

	public static void store() {
		if (isInitialized()) {
			try {
				controller.properties.store(new FileOutputStream(controller.file), "");
				LogController.log(LogController.DEBUG,
						"storing config to file " + controller.file.getPath() + " - successful");
			} catch (IOException e) {
				LogController.log(LogController.DEBUG,
						"storing config to file " + controller.file.getPath() + " - exception");
				ExceptionController.exception(e);
			}
		}
	}

}
