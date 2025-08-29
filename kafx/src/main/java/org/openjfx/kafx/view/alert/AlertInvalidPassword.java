package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.lang.Translator;

import javafx.scene.control.Alert;

public class AlertInvalidPassword extends Alert {

	public AlertInvalidPassword() {
		super(AlertType.ERROR);
		this.setGraphic(null);
		this.setHeaderText(null);
		this.setTitle(Translator.get("alert_invalidPassword_title"));
		this.setContentText(Translator.get("alert_invalidPassword_main"));
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
