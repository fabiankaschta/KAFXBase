package org.openjfx.kafx.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import javax.crypto.SecretKey;

import org.openjfx.kafx.io.Config;
import org.openjfx.kafx.io.FileIO;
import org.openjfx.kafx.lang.Translator;
import org.openjfx.kafx.secure.EncryptionHelper;
import org.openjfx.kafx.view.alert.AlertException;
import org.openjfx.kafx.view.alert.AlertInvalidPassword;
import org.openjfx.kafx.view.alert.AlertLastFileMissing;
import org.openjfx.kafx.view.alert.AlertSaveChanges;
import org.openjfx.kafx.view.dialog.DialogEnterPassword;
import org.openjfx.kafx.view.dialog.DialogSetPassword;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.Event;
import javafx.print.PageLayout;
import javafx.print.PageRange;
import javafx.print.PrintResolution;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {

	protected static Controller controller;
	protected final FileIO fileIO;
	protected final Config config;
	protected final Translator translator;
	protected final EncryptionHelper encrpytionHelper;
	protected Stage primaryStage = null;

	protected Controller(FileIO fileIO, Config config, Translator translator) {
		this(fileIO, config, translator, null);
	}

	protected Controller(FileIO fileIO, Config config, Translator translator, EncryptionHelper encrpytionHelper) {
		this.fileIO = fileIO;
		this.config = config;
		this.translator = translator;
		this.encrpytionHelper = encrpytionHelper;
		logger.setUseParentHandlers(false);
		logger.addHandler(logHandler);
		// default config
		config.load();
		config.putIfNotExists("WIDTH", String.valueOf(800));
		config.putIfNotExists("HEIGHT", String.valueOf(600));
		config.putIfNotExists("FONT_SIZE", String.valueOf(12));
		fontSize.set(Integer.valueOf(config.get("FONT_SIZE")));
		fontSize.subscribe(fontSize -> config.set("FONT_SIZE", String.valueOf(fontSize)));
	}

	public static void init(Controller controller) {
		Controller.controller = controller;
	}

	@SuppressWarnings("serial")
	public static final Level DEBUG = new Level("DEBUG", Level.FINE.intValue()) {
	};

	private final Formatter logFormatter = new Formatter() {

		@Override
		public String format(LogRecord record) {
			StringBuilder builder = new StringBuilder();
			builder.append(record.getLevel() + ": ");
			builder.append(formatMessage(record));
			builder.append(System.lineSeparator());
			return builder.toString();
		}
	};
	private final StreamHandler logHandler = new StreamHandler(System.out, logFormatter) {

		@Override
		public synchronized void publish(LogRecord record) {
			super.publish(record);
			super.flush();
		}
	};
	private final Logger logger = Logger.getLogger("kafx.controller.logger");

	private long changeCounter = 0;

	public static void log(Level level, String message) {
		controller.handleLog(level, message);
	}

	protected final void handleLog(Level level, String message) {
		logger.log(level, message);
	}

	public static void setDebugMode(boolean enabled) {
		if (enabled) {
			controller.logger.setLevel(Level.ALL);
			controller.logHandler.setLevel(Level.ALL);
		} else {
			controller.logger.setLevel(Level.OFF);
			controller.logHandler.setLevel(Level.OFF);
		}
	}

	public final static ChangeListener<Object> LISTENER_UNSAVED_CHANGES = (o, a, b) -> {
		controller.handleLog(DEBUG, o + " " + a + " -> " + b);
		setUnsavedChanges();
	};
	public final static ListChangeListener<Object> LISTLISTENER_UNSAVED_CHANGES = c -> {
		controller.handleLog(DEBUG, c.toString());
		setUnsavedChanges();
	};
	public final static MapChangeListener<Object, Object> MAPLISTENER_UNSAVED_CHANGES = m -> {
		controller.handleLog(DEBUG, m.toString());
		setUnsavedChanges();
	};

	public static ChangeListener<Object> getConditionalListenerUnsavedChanges(Supplier<Boolean> condition) {
		ChangeListener<Object> listener = (o, a, b) -> {
			if (condition.get()) {
				controller.handleLog(DEBUG, o + " " + a + " -> " + b);
				setUnsavedChanges();
			}
		};
		return listener;
	}

	protected final void incChangeCounter() {
		changeCounter++;
	}

	protected final void resetChangeCounter() {
		changeCounter = 0;
	}

	protected final long getChangeCounter() {
		return changeCounter;
	}

	private static void setUnsavedChanges() {
		controller.incChangeCounter();
		controller.handleUnsavedChanges();
	}

	protected void handleUnsavedChanges() {
	}

	public static void resetUnsavedChanges() {
		controller.resetChangeCounter();
		controller.handleUnsavedChanges();
	}

	public static boolean hasUnsavedChanges() {
		return controller.getChangeCounter() > 0;
	}

	public static void exception(Exception e) {
		controller.handleException(e);
	}

	protected void handleException(Exception e) {
		new AlertException(e).showAndWait();
	}

	public static boolean readFromFile() {
		if (existsConfigOption("LAST_FILE")) {
			try {
				boolean result = controller.handleReadFromFile(new File(getConfigOption("LAST_FILE")));
				if (result) {
					resetUnsavedChanges();
				} else {
					removeConfigOption("LAST_FILE");
				}
				return result;
			} catch (FileNotFoundException e) {
				new AlertLastFileMissing(getConfigOption("LAST_FILE")).showAndWait();
				removeConfigOption("LAST_FILE");
				return false;
			} catch (Exception e) {
				exception(e);
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean readFromFile(File file) {
		try {
			boolean result = controller.handleReadFromFile(file);
			if (result) {
				setConfigOption("LAST_FILE", file.getPath());
				resetUnsavedChanges();
			} else {
				removeConfigOption("LAST_FILE");
			}
			return result;
		} catch (Exception e) {
			exception(e);
			return false;
		}
	}

	public static boolean readFromFile(String filePath) {
		try {
			boolean result = controller.handleReadFromFile(new File(filePath));
			if (result) {
				setConfigOption("LAST_FILE", filePath);
				resetUnsavedChanges();
			} else {
				removeConfigOption("LAST_FILE");
			}
			return result;
		} catch (Exception e) {
			exception(e);
			return false;
		}
	}

	protected boolean handleReadFromFile(File file) throws Exception {
		if (fileIO == null) {
			handleException(new UnsupportedOperationException("readFromFile is not supported for this controller"));
			return false;
		} else {
			return fileIO.readFromFile(file);
		}
	}

	public static boolean writeToFile() {
		boolean result;
		if (existsConfigOption("LAST_FILE")) {
			result = controller.handlewriteToFile(new File(getConfigOption("LAST_FILE")));
		} else {
			File file = new FileChooser().showSaveDialog(null);
			if (file != null) {
				result = controller.handlewriteToFile(file);
			} else {
				result = false;
			}
		}
		if (result) {
			resetUnsavedChanges();
		}
		return result;
	}

	public static boolean writeToFile(File file) {
		boolean result = controller.handlewriteToFile(file);
		if (result) {
			setConfigOption("LAST_FILE", file.getPath());
			resetUnsavedChanges();
		}
		return result;
	}

	public static boolean writeToFile(String filePath) {
		boolean result = controller.handlewriteToFile(new File(filePath));
		if (result) {
			setConfigOption("LAST_FILE", filePath);
			resetUnsavedChanges();
		}
		return result;
	}

	protected boolean handlewriteToFile(File file) {
		if (fileIO == null) {
			handleException(new UnsupportedOperationException("writeToFile is not supported for this controller"));
			return false;
		} else {
			try {
				return fileIO.writeToFile(file);
			} catch (Exception e) {
				exception(e);
				return false;
			}
		}
	}

	public static void newFile(Event event) {
		if (hasUnsavedChanges()) {
			new AlertSaveChanges().showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					if (Controller.writeToFile()) {
						controller.handleNewFile();
					} else {
						event.consume();
					}
				} else if (response == ButtonType.CANCEL) {
					event.consume();
				} else if (response == ButtonType.NO) {
					controller.handleNewFile();
				}
			});
		} else {
			controller.handleNewFile();
		}
	}

	protected void handleNewFile() {
		handleException(new UnsupportedOperationException("newFile is not supported for this controller"));
	}

	public static boolean openFile() {
		return openFile(new Event(null));
	}

	public static boolean openFile(Event event) {
		if (hasUnsavedChanges()) {
			new AlertSaveChanges().showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					if (!Controller.writeToFile()) {
						event.consume();
					}
				} else if (response == ButtonType.CANCEL) {
					event.consume();
				}
			});
			if (!event.isConsumed()) {
				return controller.handleOpenFile();
			} else {
				return false;
			}
		} else {
			return controller.handleOpenFile();
		}
	}

	protected boolean handleOpenFile() {
		FileChooser fileChooser = new FileChooser();
		if (existsConfigOption("LAST_FILE")) {
			fileChooser.setInitialDirectory(new File(getConfigOption("LAST_FILE")).getParentFile());
		} else {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		}
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			return readFromFile(file);
		} else {
			return false;
		}
	}

	public static boolean saveFile() {
		return controller.handleSaveFile();
	}

	protected boolean handleSaveFile() {
		return writeToFile();
	}

	public static boolean saveAs() {
		return controller.handleSaveAs();
	}

	protected boolean handleSaveAs() {
		FileChooser fileChooser = new FileChooser();
		if (existsConfigOption("LAST_FILE")) {
			File lastFile = new File(getConfigOption("LAST_FILE"));
			fileChooser.setInitialDirectory(lastFile.getParentFile());
			fileChooser.setInitialFileName(lastFile.getName());
		} else {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		}
		File file = fileChooser.showSaveDialog(null);
		if (file != null) {
			this.secretKey = null; // new password
			return writeToFile(file);
		} else {
			return false;
		}
	}

	public static void close() {
		close(new Event(null));
	}

	public static void close(Event event) {
		controller.handleClose(event);
	}

	protected void handleClose(Event event) {
		if (hasUnsavedChanges()) {
			new AlertSaveChanges().showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					if (!Controller.writeToFile()) {
						event.consume();
					}
				} else if (response == ButtonType.CANCEL) {
					event.consume();
				}
			});
		}
		if (!event.isConsumed()) {
			config.store();
			// TODO AutoSave.stop();
		}
	}

	public static void print() {
		PrinterJob job = PrinterJob.createPrinterJob();
		job.getJobSettings().setPageRanges(new PageRange(1, 1));
		Node root = controller.getPrintableNode();
		if (root != null) {
			if (job != null && job.showPrintDialog(root.getScene().getWindow())) {
				Printer printer = job.getPrinter();
				PageLayout pageLayout = controller.getDefaultPageLayout(printer);
				job.getJobSettings().setPageLayout(pageLayout);
				if (job.showPageSetupDialog(root.getScene().getWindow())) {
					root.setManaged(false);
					pageLayout = job.getJobSettings().getPageLayout();

					double width = root.getLayoutBounds().getWidth();
					double height = root.getLayoutBounds().getHeight();
					PrintResolution resolution = job.getJobSettings().getPrintResolution();
					width /= resolution.getFeedResolution();
					height /= resolution.getCrossFeedResolution();
					double scaleX = pageLayout.getPrintableWidth() / width / 600;
					double scaleY = pageLayout.getPrintableHeight() / height / 600;
					Scale scale = new Scale(Math.min(scaleX, scaleY), Math.min(scaleX, scaleY));
					root.getTransforms().add(scale);

					boolean success = job.printPage(root);
					if (success) {
						job.endJob();
					} else {
						job.cancelJob();
					}
					root.getTransforms().remove(scale);
				}
			}
			root.setManaged(true);
		}
	}

	protected PageLayout getDefaultPageLayout(Printer printer) {
		return printer.getDefaultPageLayout();
	}

	protected Node getPrintableNode() {
		handleException(new UnsupportedOperationException("print is not supported for this controller"));
		return null;
	}

	protected IntegerProperty fontSize = new SimpleIntegerProperty(this, "fontSize", 12);

	public static IntegerProperty fontSizeProperty() {
		return controller.fontSize;
	}

	public static int getFontSize() {
		return controller.fontSize.get();
	}

	public static void setFontSize(int fontSize) {
		controller.fontSize.set(fontSize);
	}

	public static <T> void bindTableColumnWidthToFontSize(TableView<T> tableView) {
		fontSizeProperty().subscribe((oldSize, newSize) -> {
			tableView.getColumns().forEach(column -> {
				double width = column.getWidth();
				double widthRatio = width / oldSize.doubleValue();
				double newWidth = widthRatio * newSize.doubleValue();
				tableView.resizeColumn(column, newWidth - width);
			});
		});
	}

	public static boolean existsConfigOption(String option) {
		return controller.config.exists(option);
	}

	public static void removeConfigOption(String option) {
		controller.config.remove(option);
	}

	public static String getConfigOption(String option) {
		return controller.config.get(option);
	}

	public static void setConfigOption(String option, String value) {
		controller.config.set(option, value);
	}

	public static void putConfigOptionIfNotExists(String option, String value) {
		controller.config.putIfNotExists(option, value);
	}

	private SecretKey secretKey;

	public static SecretKey requestSecretKey() {
		return controller.secretKey = new DialogEnterPassword().showAndWait().orElseThrow();
	}

	public static SecretKey getSecretKey() {
		if (controller.secretKey == null) {
			controller.secretKey = new DialogSetPassword().showAndWait().orElseThrow();
		}
		return controller.secretKey;
	}

	public static void clearSecretKey() {
		controller.secretKey = null;
	}

	public static boolean encryptionSupported() {
		return controller.encrpytionHelper != null;
	}

	public static EncryptionHelper getEncryptionHelper() {
		if (encryptionSupported()) {
			return controller.encrpytionHelper;
		} else {
			exception(new UnsupportedOperationException("encryption is not supported for this controller"));
			return null;
		}
	}

	public static void invalidPassword() {
		controller.handleInvalidPassword();
	}

	protected void handleInvalidPassword() {
		new AlertInvalidPassword().showAndWait();
	}

	public static void changePassword() {
		SecretKey oldKey = controller.secretKey; // save old password
		controller.secretKey = null; // remove old password
		try {
			getSecretKey(); // set new password
		} catch (NoSuchElementException e) {
			controller.secretKey = oldKey;
		}
		saveFile(); // save file
	}

	public static String translate(String key) {
		return controller.translator.get(key);
	}

	public static void setPrimaryStage(Stage stage) {
		controller.primaryStage = stage;
	}

	public static Stage getPrimaryStage() {
		return controller.primaryStage;
	}

}
