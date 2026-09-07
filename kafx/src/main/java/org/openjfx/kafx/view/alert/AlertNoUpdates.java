package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Alert;

public class AlertNoUpdates extends Alert {

	public AlertNoUpdates() {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setTitle(TranslationController.translate("alert_no_updates_title"));
		this.setHeaderText(TranslationController.translate("alert_no_updates_header"));
		this.setContentText(TranslationController.translate("alert_no_updates_main"));
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
