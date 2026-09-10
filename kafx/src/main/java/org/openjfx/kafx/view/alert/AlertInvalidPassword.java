package org.openjfx.kafx.view.alert;

import org.openjfx.kafx.controller.TranslationController;

public class AlertInvalidPassword extends AlertCustom {

	public AlertInvalidPassword() {
		super(AlertType.ERROR);
		this.setGraphic(null);
		this.setHeaderText(null);
		this.setTitle(TranslationController.translate("alert_invalidPassword_title"));
		this.setContentText(TranslationController.translate("alert_invalidPassword_main"));
	}

}
