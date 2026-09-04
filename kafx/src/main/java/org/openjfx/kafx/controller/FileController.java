package org.openjfx.kafx.controller;

import java.io.File;

import javax.crypto.SecretKey;

import org.openjfx.kafx.io.FileIO;
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

	private static boolean readFromFile(File file) {
		if (!isInitialized()) {
			return false;
		} else {
			AutoSaveController.stop();
			try {
				boolean result = controller.handleReadFromFile(file);
				if (result) {
					LogController.log(LogController.DEBUG, "read from file " + file.getPath() + " - successful");
					ConfigController.set("LAST_FILE", file.getPath());
					ChangeController.resetChanges();
				} else {
					LogController.log(LogController.DEBUG, "read from file " + file.getPath() + " - successful");
				}
				AutoSaveController.start();
				return result;
			} catch (Exception e) {
				LogController.log(LogController.DEBUG, "read from file " + file.getPath() + " - exception");
				ExceptionController.exception(e);
				AutoSaveController.start();
				return false;
			}
		}
	}

	protected boolean handleReadFromFile(File file) throws Exception {
		return fileIO.readFromFile(file);
	}

	private static boolean writeToFile(File file) {
		if (!isInitialized()) {
			return false;
		} else {
			AutoSaveController.stop();
			try {
				boolean result = controller.handleWriteToFile(file);
				LogController.log(LogController.DEBUG,
						"write to file " + file.getPath() + (result ? " - successful" : " - unsuccessful"));
				if (result) {
					ConfigController.set("LAST_FILE", file.getPath());
					ChangeController.resetChanges();
				}
				AutoSaveController.start();
				return result;
			} catch (Exception e) {
				LogController.log(LogController.DEBUG, "write to file " + file.getPath() + " - exception");
				ExceptionController.exception(e);
				AutoSaveController.start();
				return false;
			}
		}
	}

	protected boolean handleWriteToFile(File file) throws Exception {
		return fileIO.writeToFile(file);
	}

	public static boolean newFile() {
		if (!isInitialized()) {
			return false;
		} else {
			return newFile(new Event(null));
		}
	}

	public static boolean newFile(Event event) {
		if (!isInitialized()) {
			return false;
		} else {
			AutoSaveController.stop();
			if (ChangeController.hasChanges()) {
				new AlertSaveChanges().showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						LogController.log(LogController.DEBUG, "new file - save changes ok");
						if (!saveFile()) {
							event.consume();
						}
					} else if (response == ButtonType.CANCEL) {
						LogController.log(LogController.DEBUG, "new file - save changes cancel");
						event.consume();
					} else if (response == ButtonType.NO) {
						LogController.log(LogController.DEBUG, "new file - save changes no");
					}
				});
				if (event.isConsumed()) {
					AutoSaveController.start();
					return false;
				}
			} else {
				LogController.log(LogController.DEBUG, "new file - no changes");
			}

			FileChooser fileChooser = new FileChooser();
			if (ConfigController.exists("LAST_FILE")) {
				fileChooser.setInitialDirectory(new File(ConfigController.get("LAST_FILE")).getParentFile());
			} else {
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}
			File file = fileChooser.showSaveDialog(null);
			if (file != null) {
				SecretKey oldKey = EncryptionController.getSecretKey();
				if (EncryptionController.setSecretKey()) {
					boolean result = controller.handleNewFile(file);
					if (result) {
						LogController.log(LogController.DEBUG, "new file - successful");
					} else {
						LogController.log(LogController.DEBUG, "new file - unsuccessful");
					}
					AutoSaveController.start();
					return result;
				} else {
					EncryptionController.restoreSecretKey(oldKey);
					AutoSaveController.start();
					return false;
				}
			} else {
				LogController.log(LogController.DEBUG, "new file - aborted");
				AutoSaveController.start();
				return false;
			}
		}
	}

	protected boolean handleNewFile(File file) {
		return writeToFile(file);
	}

	public static boolean openLastFile() {
		if (!isInitialized()) {
			return false;
		} else {
			LogController.log(LogController.DEBUG, "open last file");
			AutoSaveController.stop();
			SecretKey oldKey = EncryptionController.getSecretKey();
			if (EncryptionController.requestSecretKey()) {
				boolean result = controller.handleOpenFile(new File(ConfigController.get("LAST_FILE")));
				if (result) {
					LogController.log(LogController.DEBUG, "open file - successful");
				} else {
					LogController.log(LogController.DEBUG, "open file - unsuccessful");
				}
				AutoSaveController.start();
				return result;
			} else {
				EncryptionController.restoreSecretKey(oldKey);
				AutoSaveController.start();
				return false;
			}
		}
	}

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
			AutoSaveController.stop();
			if (ChangeController.hasChanges()) {
				new AlertSaveChanges().showAndWait().ifPresent(response -> {
					if (response == ButtonType.OK) {
						LogController.log(LogController.DEBUG, "open file - save changes ok");
						if (!saveFile()) {
							event.consume();
						}
					} else if (response == ButtonType.CANCEL) {
						LogController.log(LogController.DEBUG, "open file - save changes cancel");
						event.consume();
					} else if (response == ButtonType.NO) {
						LogController.log(LogController.DEBUG, "open file - save changes no");
					}
				});
				if (event.isConsumed()) {
					AutoSaveController.start();
					return false;
				}
			} else {
				LogController.log(LogController.DEBUG, "open file - no changes");
			}

			FileChooser fileChooser = new FileChooser();
			if (ConfigController.exists("LAST_FILE")) {
				fileChooser.setInitialDirectory(new File(ConfigController.get("LAST_FILE")).getParentFile());
			} else {
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
			}
			File file = fileChooser.showOpenDialog(null);
			if (file != null) {
				SecretKey oldKey = EncryptionController.getSecretKey();
				if (EncryptionController.requestSecretKey()) {
					boolean result = controller.handleOpenFile(file);
					if (result) {
						LogController.log(LogController.DEBUG, "open file - successful");
					} else {
						LogController.log(LogController.DEBUG, "open file - unsuccessful");
					}
					AutoSaveController.start();
					return result;
				} else {
					EncryptionController.restoreSecretKey(oldKey);
					AutoSaveController.start();
					return false;
				}
			} else {
				LogController.log(LogController.DEBUG, "open file - aborted");
				AutoSaveController.start();
				return false;
			}
		}
	}

	protected boolean handleOpenFile(File file) {
		return readFromFile(file);
	}

	public static boolean saveFile() {
		if (!isInitialized()) {
			return false;
		} else {
			AutoSaveController.stop();
			LogController.log(LogController.DEBUG, "save file");
			boolean result = controller.handleSaveFile(new File(ConfigController.get("LAST_FILE")));
			AutoSaveController.start();
			return result;
		}
	}

	public static boolean saveAs() {
		if (!isInitialized()) {
			return false;
		} else {
			AutoSaveController.stop();
			LogController.log(LogController.DEBUG, "save file as");
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
				boolean result = controller.handleSaveFile(file);
				AutoSaveController.start();
				return result;
			} else {
				AutoSaveController.start();
				return false;
			}
		}
	}

	protected boolean handleSaveFile(File file) {
		return writeToFile(file);
	}

}
