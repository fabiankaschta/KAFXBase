package kafx.view.alert;

import javafx.scene.control.Alert;
import kafx.controller.Controller;
import kafx.lang.Translator;

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
