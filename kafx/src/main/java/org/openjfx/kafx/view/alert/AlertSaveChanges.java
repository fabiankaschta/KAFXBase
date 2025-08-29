package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.Controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertSaveChanges extends Alert {

	public AlertSaveChanges() {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(null);
		this.setTitle(Controller.translate("alert_saveChanges_title"));
		this.setContentText(Controller.translate("alert_saveChanges_main"));
		this.getDialogPane().getButtonTypes().addAll(ButtonType.NO);
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
