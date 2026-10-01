package org.openjfx.kafx.controller;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LogController extends Controller {

	private static LogController controller;

	protected LogController(String path, String fileName, Level level) {
		logger.setUseParentHandlers(false);
		logger.setLevel(level);
		Formatter logFormatter = new Formatter() {

			@Override
			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder();
				builder.append(record.getLevel() + ": ");
				builder.append(formatMessage(record));
				builder.append(System.lineSeparator());
				return builder.toString();
			}
		};
		logger.addHandler(new StreamHandler(System.out, logFormatter) {

			@Override
			public synchronized void publish(LogRecord record) {
				super.publish(record);
				super.flush();
			}
		});
		if (fileName != null) {
			try {
				File file;
				if (path != null) {
					File pathFile = new File(path);
					pathFile.mkdirs();
					file = new File(path + System.getProperty("file.separator") + fileName);
				} else {
					file = new File(fileName);
				}
				file.createNewFile();
				FileHandler fileHandler = new FileHandler(file.getPath()) {

					@Override
					public synchronized void publish(LogRecord record) {
						super.publish(record);
						super.flush();
					}
				};
				fileHandler.setFormatter(logFormatter);
				logger.addHandler(fileHandler);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}
		for (Handler h : logger.getHandlers()) {
			h.setLevel(level);
		}
	}

	public static void init() {
		init(Level.WARNING);
	}

	public static void init(Level level) {
		String appName = Controller.getAppName().toLowerCase();
		init(System.getProperty("user.home") + System.getProperty("file.separator") + '.' + appName, appName + ".log",
				level);
	}

	public static void init(String path, String fileName, Level level) {
		init(new LogController(path, fileName, level));
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void init(LogController controller) {
		LogController.log(LogController.DEBUG, "init log controller");
		LogController.controller = controller;
	}

	@SuppressWarnings("serial")
	public static final Level DEBUG = new Level("DEBUG", Level.FINE.intValue()) {
	};

	private final static Logger logger = Logger.getLogger("kafx.controller.logger");

	public static void log(Level level, String message) {
		logger.log(level, message);
	}

}
