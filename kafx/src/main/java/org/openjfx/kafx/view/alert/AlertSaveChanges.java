package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.ButtonType;

public class AlertSaveChanges extends AlertCustom {

	public AlertSaveChanges() {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(null);
		this.setTitle(TranslationController.translate("alert_saveChanges_title"));
		this.setContentText(TranslationController.translate("alert_saveChanges_main"));
		this.getDialogPane().getButtonTypes().addAll(ButtonType.NO);
	}

}
