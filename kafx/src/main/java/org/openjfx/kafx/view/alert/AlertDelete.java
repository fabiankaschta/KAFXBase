package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Alert;

public class AlertDelete extends Alert {

	public AlertDelete(String toBeDeleted) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(toBeDeleted);
		this.setTitle(TranslationController.translate("alert_delete_title"));
		this.setContentText(TranslationController.translate("alert_delete_main"));
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
