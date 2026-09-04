package org.openjfx.kafx.controller;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.openjfx.kafx.view.alert.AlertInvalidPassword;
import org.openjfx.kafx.view.dialog.DialogEnterPassword;
import org.openjfx.kafx.view.dialog.DialogSetPassword;

public abstract class EncryptionController extends Controller {

	private static EncryptionController controller;

	protected EncryptionController() {
	}

	public static void init(EncryptionController controller) {
		LogController.log(LogController.DEBUG, "init encryption controller");
		EncryptionController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	private SecretKey secretKey;

	public static boolean requestSecretKey() {
		if (!isInitialized()) {
			return true;
		} else {
			LogController.log(LogController.DEBUG, "requesting password");
			SecretKey newKey = new DialogEnterPassword().showAndWait().orElse(null);
			if (newKey == null) {
				LogController.log(LogController.DEBUG, "requesting password - unsuccessful");
				return false;
			} else {
				LogController.log(LogController.DEBUG, "requesting password - successful");
				controller.secretKey = newKey;
				return true;
			}
		}
	}

	public static SecretKey getSecretKey() {
		if (!isInitialized()) {
			return null;
		} else {
			LogController.log(LogController.DEBUG, "getting password");
			return controller.secretKey;
		}
	}

	public static boolean setSecretKey() {
		if (!isInitialized()) {
			return true;
		} else {
			LogController.log(LogController.DEBUG, "setting new password");
			SecretKey newKey = new DialogSetPassword().showAndWait().orElse(null);
			if (newKey == null) {
				LogController.log(LogController.DEBUG, "setting new password - unsuccessful");
				return false;
			} else {
				LogController.log(LogController.DEBUG, "setting new password - successful");
				controller.secretKey = newKey;
				return true;
			}
		}
	}

	public static void restoreSecretKey(SecretKey key) {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "restoring password");
			controller.secretKey = key;
		}
	}

	public static void clearSecretKey() {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "removing secret key");
			controller.secretKey = null;
		}
	}

	public static Cipher getCipher() {
		if (!isInitialized()) {
			return null;
		} else {
			return controller.handleGetCipher();
		}
	}

	public abstract Cipher handleGetCipher();

	public static SecretKey generateFromPassword(String password) {
		if (!isInitialized()) {
			return null;
		} else {
			LogController.log(LogController.DEBUG, "generating secret key from password");
			return controller.handleGenerateFromPassword(password);
		}
	}

	public abstract SecretKey handleGenerateFromPassword(String password);

	public static void invalidPassword() {
		if (!isInitialized()) {
			return;
		} else {
			LogController.log(LogController.DEBUG, "password invalid");
			controller.handleInvalidPassword();
		}
	}

	protected void handleInvalidPassword() {
		new AlertInvalidPassword().showAndWait();
	}

}
