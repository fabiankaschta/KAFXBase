package kafx.view.alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import kafx.controller.Controller;
import kafx.lang.Translator;

public class AlertSaveChanges extends Alert {

	public AlertSaveChanges() {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(null);
		this.setTitle(Translator.get("alert_saveChanges_title"));
		this.setContentText(Translator.get("alert_saveChanges_main"));
		this.getDialogPane().getButtonTypes().addAll(ButtonType.NO);
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
