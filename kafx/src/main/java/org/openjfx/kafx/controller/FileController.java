package org.openjfx.kafx.controller;

import java.io.File;
import java.io.FileNotFoundException;

import org.openjfx.kafx.io.FileIO;
import org.openjfx.kafx.view.alert.AlertLastFileMissing;
import org.openjfx.kafx.view.alert.AlertSaveChanges;

import javafx.event.Event;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

public abstract class FileController extends Controller {

	private static FileController controller;

	private final FileIO fileIO;

	protected FileController(FileIO fileIO) {
		this.fileIO = fileIO;
	}

	public static void init(FileController controller) {
		LogController.log(LogController.DEBUG, "init file controller");
		FileController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static boolean readFromFile() {
		if (!isInitialized()) {
			return false;
		} else if (ConfigController.exists("LAST_FILE")) {
			try {
				boolean result = controller.handleReadFromFile(new File(ConfigController.get("LAST_FILE")));
				if (result) {
					LogController.log(LogController.DEBUG, "read from last file " + ConfigController.get("LAST_FILE") + " - successful");
					ChangeController.resetChanges();
				} else {
					LogController.log(LogController.DEBUG, "read from last file " + ConfigController.get("LAST_FILE") + " - unsuccessful");
					ConfigController.remove("LAST_FILE");
				}
				return result;
			} catch (FileNotFoundException e) {
				LogController.log(LogController.DEBUG, "read from last file " + ConfigController.get("LAST_FILE") + " - not found");
				new AlertLastFileMissing(ConfigController.get("LAST_FILE")).showAndWait();
				ConfigController.remove("LAST_FILE");
				return false;
			} catch (Exception e) {
				LogController.log(LogController.DEBUG, "read from last file " + ConfigController.get("LAST_FILE") + " - exception");
				ExceptionController.exception(e);
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean readFromFile(File file) {
		if (!isInitialized()) {
			return false;
		} else {
			try {
				boolean result = controller.handleReadFromFile(file);
				if (result) {
					LogController.log(LogController.DEBUG, "read from file " + file.getPath() + " - successful");
					ConfigController.set("LAST_FILE", file.getPath());
					ChangeController.resetChanges();
				} else {
					LogController.log(LogController.DEBUG, "read from file " + file.getPath() + " - successful");
					ConfigController.remove("LAST_FILE");
				}
				return result;
			} catch (Exception e) {
				LogController.log(LogController.DEBUG, "read from file " + file.getPath() + " - exception");
				ExceptionController.exception(e);
				return false;
			}
		}
	}

	public static boolean readFromFile(String filePath) {
		if (!isInitialized()) {
			return false;
		} else {
			try {
				boolean result = controller.handleReadFromFile(new File(filePath));
				if (result) {
					LogController.log(LogController.DEBUG, "read from file " + filePath + " - successful");
					ConfigController.set("LAST_FILE", filePath);
					ChangeController.resetChanges();
				} else {
					LogController.log(LogController.DEBUG, "read from file " + filePath + " - successful");
					ConfigController.remove("LAST_FILE");
				}
				return result;
			} catch (Exception e) {
				LogController.log(LogController.DEBUG, "read from file " + filePath + " - exception");
				ExceptionController.exception(e);
				return false;
			}
		}
	}

	protected boolean handleReadFromFile(File file) throws Exception {
		return fileIO.readFromFile(file);
	}

	public static boolean writeToFile() {
		if (!isInitialized()) {
			return false;
		} else {
			boolean result;
			if (ConfigController.exists("LAST_FILE")) {
				try {
					result = controller.handlewriteToFile(new File(ConfigController.get("LAST_FILE")));
					LogController.log(LogController.DEBUG, "write to file " + ConfigController.get("LAST_FILE")
							+ (result ? " - successful" : " - unsuccessful"));
				} catch (Exception e) {
					LogController.log(LogController.DEBUG, "write to file " + ConfigController.get("LAST_FILE") + " - exception");
					ExceptionController.exception(e);
					return false;
				}
			} else {
				File file = new FileChooser().showSaveDialog(null);
				if (file != null) {
					try {
						result = controller.handlewriteToFile(file);
						LogController.log(LogController.DEBUG, "write to file " + file.getPath() + (result ? " - successful" : " - unsuccessful"));
					} catch (Exception e) {
						LogController.log(LogController.DEBUG, "write to file " + file.getPath() + " - exception");
						ExceptionController.exception(e);
						return false;
					}
				} else {
					result = false;
					LogController.log(LogController.DEBUG, "write to file - aborted");
				}
			}
			if (result) {
				ChangeController.resetChanges();
			}
			return result;
		}
	}

	public static boolean writeToFile(File file) {
		if (!isInitialized()) {
			return false;
		} else {
			try {
				boolean result = controller.handlewriteToFile(file);
				LogController.log(LogController.DEBUG, "write to file " + file.getPath() + (result ? " - successful" : " - unsuccessful"));
				if (result) {
					ConfigController.set("LAST_FILE", file.getPath());
					ChangeController.resetChanges();
				}
				return result;
			} catch (Exception e) {
				LogController.log(LogController.DEBUG, "write to file " + file.getPath() + " - exception");
				ExceptionController.exception(e);
				return false;
			}
		}
	}

	public static boolean writeToFile(String filePath) {
		if (!isInitialized()) {
			return false;
		} else {
			try {
				boolean result = controller.handlewriteToFile(new File(filePath));
				LogController.log(LogController.DEBUG, "write to file " + filePath + (result ? " - successful" : " - unsuccessful"));
				if (result) {
					ConfigController.set("LAST_FILE", filePath);
					ChangeController.resetChanges();
				}
				return result;
			} catch (Exception e) {
				LogController.log(LogController.DEBUG, "write to file " + filePath + " - exception");
				ExceptionController.exception(e);
				return false;
			}
		}
	}

	protected boolean handlewriteToFile(File file) throws Exception {
		return fileIO.writeToFile(file);
	}

	public static void newFile(Event event) {
		if (!isInitialized()) {
			return;
		} else {
			if (ChangeController.hasChanges()) {
				new AlertSaveChanges().showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						LogController.log(LogController.DEBUG, "new file - save changes ok");
						if (writeToFile()) {
							controller.handleNewFile();
						} else {
							event.consume();
						}
					} else if (response == ButtonType.CANCEL) {
						LogController.log(LogController.DEBUG, "new file - save changes cancel");
						event.consume();
					} else if (response == ButtonType.NO) {
						LogController.log(LogController.DEBUG, "new file - save changes no");
						controller.handleNewFile();
					}
				});
			} else {
				LogController.log(LogController.DEBUG, "new file - no changes");
				controller.handleNewFile();
			}
		}
	}

	protected abstract void handleNewFile();

	public static boolean openFile() {
		if (!isInitialized()) {
			return false;
		} else {
			return openFile(new Event(null));
		}
	}

	public static boolean openFile(Event event) {
		if (!isInitialized()) {
			return false;
		} else {
			if (ChangeController.hasChanges()) {
				new AlertSaveChanges().showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						LogController.log(LogController.DEBUG, "open file - save changes ok");
						if (!writeToFile()) {
							event.consume();
						}
					} else if (response == ButtonType.CANCEL) {
						LogController.log(LogController.DEBUG, "open file - save changes cancel");
						event.consume();
					} else if (response == ButtonType.NO) {
						LogController.log(LogController.DEBUG, "open file - save changes no");
					}
				});
				if (!event.isConsumed()) {
					return controller.handleOpenFile();
				} else {
					return false;
				}
			} else {
				LogController.log(LogController.DEBUG, "open file - no changes");
				return controller.handleOpenFile();
			}
		}
	}

	protected boolean handleOpenFile() {
		FileChooser fileChooser = new FileChooser();
		if (ConfigController.exists("LAST_FILE")) {
			fileChooser.setInitialDirectory(new File(ConfigController.get("LAST_FILE")).getParentFile());
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
		if (!isInitialized()) {
			return false;
		} else {
			LogController.log(LogController.DEBUG, "save file");
			return controller.handleSaveFile();
		}
	}

	protected boolean handleSaveFile() {
		return writeToFile();
	}

	public static boolean saveAs() {
		if (!isInitialized()) {
			return false;
		} else {
			LogController.log(LogController.DEBUG, "save file as");
			return controller.handleSaveAs();
		}
	}

	protected boolean handleSaveAs() {
		FileChooser fileChooser = new FileChooser();
		if (ConfigController.exists("LAST_FILE")) {
			File lastFile = new File(ConfigController.get("LAST_FILE"));
			fileChooser.setInitialDirectory(lastFile.getParentFile());
			fileChooser.setInitialFileName(lastFile.getName());
		} else {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		}
		File file = fileChooser.showSaveDialog(null);
		if (file != null) {
			EncryptionController.clearSecretKey(); // new password
			return writeToFile(file);
		} else {
			return false;
		}
	}

}
