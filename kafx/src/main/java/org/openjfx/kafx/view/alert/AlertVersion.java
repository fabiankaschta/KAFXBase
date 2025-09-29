package org.openjfx.kafx.view.alert;

import java.net.URL;

import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Alert;

public class AlertVersion extends Alert {

	public AlertVersion(String local, String remote, URL url) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setTitle(TranslationController.translate("alert_version_title"));
		this.setHeaderText(TranslationController.translate("alert_version_local") + ": " + local + " "
				+ TranslationController.translate("alert_version_remote") + ": " + remote);
		this.setContentText(TranslationController.translate("alert_version_main"));
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
