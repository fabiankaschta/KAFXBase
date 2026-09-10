package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.controller.FontSizeController;

import javafx.scene.control.Alert;

public class AlertCustom extends Alert {

	public AlertCustom(AlertType type) {
		super(type);
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
		this.initOwner(Controller.getPrimaryStage());
	}

}
