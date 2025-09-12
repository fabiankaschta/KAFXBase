package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Alert;

public class AlertLastFileMissing extends Alert {

	public AlertLastFileMissing(String path) {
		super(AlertType.ERROR);
		this.setGraphic(null);
		this.setHeaderText(path);
		this.setTitle(TranslationController.translate("alert_lastFileMissing_title"));
		this.setContentText(TranslationController.translate("alert_lastFileMissing_main"));
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
