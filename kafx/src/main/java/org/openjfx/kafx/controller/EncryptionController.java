package org.openjfx.kafx.controller;

import java.util.NoSuchElementException;

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

	public static SecretKey requestSecretKey() {
		if (!isInitialized()) {
			return null;
		} else {
			LogController.log(LogController.DEBUG, "requesting password");
			return controller.secretKey = new DialogEnterPassword().showAndWait().orElseThrow();
		}
	}

	public static SecretKey getSecretKey() {
		if (!isInitialized()) {
			return null;
		} else {
			if (controller.secretKey == null) {
				LogController.log(LogController.DEBUG, "setting new password");
				controller.secretKey = new DialogSetPassword().showAndWait().orElseThrow();
			} else {
				LogController.log(LogController.DEBUG, "using password");
			}
			return controller.secretKey;
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

	public static void changePassword() {
		if (!isInitialized()) {
			return;
		} else {
			SecretKey oldKey = controller.secretKey; // save old password
			controller.secretKey = null; // remove old password
			try {
				LogController.log(LogController.DEBUG, "changing password - successful");
				getSecretKey(); // set new password
			} catch (NoSuchElementException e) {
				LogController.log(LogController.DEBUG, "changing password - unsuccessful");
				controller.secretKey = oldKey;
			}
			FileController.saveFile(); // save file
		}
	}

}
