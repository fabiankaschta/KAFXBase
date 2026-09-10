package org.openjfx.kafx.view.dialog;

import javax.crypto.SecretKey;

import org.openjfx.kafx.controller.EncryptionController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

public class DialogEnterPassword extends DialogUserInput<SecretKey> {

	private final UserInputTextInput password;

	public DialogEnterPassword() {
		super(TranslationController.translate("dialog_enterPassword_title"));

		this.password = new UserInputTextInput(new PasswordField());
		super.addInput(this.password, TranslationController.translate("dialog_enterPassword_password"));

		this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		this.getDialogPane().lookupButton(ButtonType.OK).disableProperty()
				.bind(this.password.isSelectedExpression().not());

		this.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return EncryptionController.generateFromPassword(this.password.getValue().trim());
			} else {
				return null;
			}
		});
	}
}