package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.Controller;

import javafx.scene.control.Alert;

public class AlertDelete extends Alert {

	public AlertDelete(String toBeDeleted) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(toBeDeleted);
		this.setTitle(Controller.translate("alert_delete_title"));
		this.setContentText(Controller.translate("alert_delete_main"));
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
