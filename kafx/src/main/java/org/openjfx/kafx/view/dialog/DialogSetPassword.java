package org.openjfx.kafx.view.dialog;

import javax.crypto.SecretKey;

import org.openjfx.kafx.controller.EncryptionController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.dialog.userinput.UserInputTextInput;

import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

public class DialogSetPassword extends DialogUserInput<SecretKey> {

	private final UserInputTextInput password;
	private final UserInputTextInput passwordConfirm;

	public DialogSetPassword() {
		super(TranslationController.translate("dialog_setPassword_title"));

		this.password = new UserInputTextInput(new PasswordField());
		super.addInput(this.password, TranslationController.translate("dialog_setPassword_password"));
		this.passwordConfirm = new UserInputTextInput(new PasswordField());
		super.addInput(this.passwordConfirm, TranslationController.translate("dialog_setPassword_passwordConfirm"));

		ButtonType cancelButtonType = ButtonType.CANCEL;
		ButtonType okButtonType = ButtonType.OK;
		this.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
		this.getDialogPane().lookupButton(okButtonType).disableProperty()
				.bind(this.password.isSelectedExpression().not().or(this.passwordConfirm.isSelectedExpression().not())
						.or(this.password.valueProperty().isNotEqualTo(this.passwordConfirm.valueProperty())));

		this.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				return EncryptionController.generateFromPassword(this.password.getValue().trim());
			} else {
				return null;
			}
		});
	}
}