package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.TranslationController;

public class AlertNoUpdates extends AlertCustom {

	public AlertNoUpdates() {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setTitle(TranslationController.translate("alert_no_updates_title"));
		this.setHeaderText(TranslationController.translate("alert_no_updates_header"));
		this.setContentText(TranslationController.translate("alert_no_updates_main"));
	}

}
