package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.TranslationController;

public class AlertDelete extends AlertCustom {

	public AlertDelete(String toBeDeleted) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setHeaderText(toBeDeleted);
		this.setTitle(TranslationController.translate("alert_delete_title"));
		this.setContentText(TranslationController.translate("alert_delete_main"));
	}

}
