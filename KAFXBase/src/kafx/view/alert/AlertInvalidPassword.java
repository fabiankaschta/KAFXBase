package kafx.view.alert;

import javafx.scene.control.Alert;
import kafx.controller.Controller;
import kafx.lang.Translator;

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
