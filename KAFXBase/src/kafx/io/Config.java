package kafx.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import kafx.controller.Controller;

public class Config {

	private final String filename;
	private final String path;
	private File file;
	private final Properties properties = new Properties();

	public Config(String fileName) {
		this.path = System.getProperty("user.home");
		this.filename = fileName;
	}

	public Config(String path, String fileName) {
		this.path = path;
		this.filename = fileName;
	}

	private String getFilePath() {
		return this.path == null ? this.filename : this.path + System.getProperty("file.separator") + this.filename;
	}

	public void load() {
		try {
			this.file = new File(getFilePath());
			this.file.createNewFile();
			this.properties.load(new FileInputStream(this.file));
		} catch (IOException e) {
			Controller.exception(e);
		}
	}

	public void store() {
		try {
			this.properties.store(new FileOutputStream(this.file), "");
		} catch (IOException e) {
			Controller.exception(e);
		}
	}

	public boolean exists(String option) {
		return get(option) != null;
	}

	public void remove(String option) {
		this.properties.remove(option.toString());
	}

	public String get(String option) {
		return this.properties.getProperty(option.toString());
	}

	public void set(String option, String value) {
		this.properties.setProperty(option.toString(), value);
	}

	public void putIfNotExists(String option, String value) {
		if (!exists(option)) {
			this.properties.setProperty(option.toString(), value);
		}
	}

}
