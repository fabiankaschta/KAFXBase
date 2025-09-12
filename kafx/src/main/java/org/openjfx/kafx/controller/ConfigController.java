package org.openjfx.kafx.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigController extends Controller {

	private static ConfigController controller;

	private final String filename;
	private final String path;
	private File file;
	private final Properties properties = new Properties();

	protected ConfigController(String fileName) {
		this(System.getProperty("user.home"), fileName);
	}

	protected ConfigController(String path, String fileName) {
		this.path = path;
		this.filename = fileName;
		try {
			this.file = new File(this.path == null ? this.filename
					: this.path + System.getProperty("file.separator") + this.filename);
			this.file.createNewFile();
			this.properties.load(new FileInputStream(this.file));
		} catch (IOException e) {
			ExceptionController.exception(e);
		}
	}

	public static void init(String fileName) {
		init(new ConfigController(fileName));
	}

	public static void init(String path, String fileName) {
		init(new ConfigController(path, fileName));
	}

	public static void init(ConfigController controller) {
		log(DEBUG, "init config controller");
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
			log(DEBUG, "config remove " + option);
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
			log(DEBUG, "config set " + option + " to " + value);
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
				log(DEBUG, "storing config to file " + controller.file.getPath() + " - successful");
			} catch (IOException e) {
				log(DEBUG, "storing config to file " + controller.file.getPath() + " - exception");
				ExceptionController.exception(e);
			}
		}
	}

}
