package kafx.view.alert;

import kafx.controller.Controller;
import kafx.lang.Translator;
import javafx.scene.control.Alert;

public class AlertDelete extends Alert {

	public AlertDelete(String toBeDeleted) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(toBeDeleted);
		this.setTitle(Translator.get("alert_delete_title"));
		this.setContentText(Translator.get("alert_delete_main"));
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
