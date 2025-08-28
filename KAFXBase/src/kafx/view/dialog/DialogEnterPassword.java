package kafx.view.dialog;

import javax.crypto.SecretKey;

import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import kafx.controller.Controller;
import kafx.lang.Translator;
import kafx.view.dialog.userinput.UserInputTextInput;

public class DialogEnterPassword extends DialogUserInput<SecretKey> {

	private final UserInputTextInput password;

	public DialogEnterPassword() {
		super(Translator.get("dialog_enterPassword_title"));

		this.password = new UserInputTextInput(new PasswordField());
		super.addInput(this.password, Translator.get("dialog_enterPassword_password"));

		ButtonType cancelButtonType = ButtonType.CANCEL;
		ButtonType okButtonType = ButtonType.OK;
		this.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);
		this.getDialogPane().lookupButton(okButtonType).disableProperty()
				.bind(this.password.isSelectedExpression().not());

		this.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				return Controller.getEncryptionHelper().generateFromPassword(this.password.getValue().trim());
			} else {
				return null;
			}
		});
	}
}