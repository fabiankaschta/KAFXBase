package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.lang.Translator;

import javafx.scene.control.Alert;

public class AlertLastFileMissing extends Alert {

	public AlertLastFileMissing(String path) {
		super(AlertType.ERROR);
		this.setGraphic(null);
		this.setHeaderText(path);
		this.setTitle(Translator.get("alert_lastFileMissing_title"));
		this.setContentText(Translator.get("alert_lastFileMissing_main"));
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
