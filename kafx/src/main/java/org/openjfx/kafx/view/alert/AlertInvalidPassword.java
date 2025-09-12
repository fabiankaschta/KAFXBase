package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Alert;

public class AlertInvalidPassword extends Alert {

	public AlertInvalidPassword() {
		super(AlertType.ERROR);
		this.setGraphic(null);
		this.setHeaderText(null);
		this.setTitle(TranslationController.translate("alert_invalidPassword_title"));
		this.setContentText(TranslationController.translate("alert_invalidPassword_main"));
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
