package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.ButtonType;

public class AlertDelete extends AlertCustom {

	public AlertDelete(String toBeDeleted) {
		this(toBeDeleted, null);
	}

	public AlertDelete(String toBeDeleted, Runnable delete) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(toBeDeleted);
		this.setTitle(TranslationController.translate("alert_delete_title"));
		this.setContentText(TranslationController.translate("alert_delete_main"));
		if (delete != null) {
			this.setResultConverter(button -> {
				if (button == ButtonType.OK) {
					delete.run();
				}
				return button;
			});
		}
	}

}
