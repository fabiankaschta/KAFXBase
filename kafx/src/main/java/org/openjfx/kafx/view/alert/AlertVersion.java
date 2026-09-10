package org.openjfx.kafx.view.alert;

import java.net.URL;

import org.openjfx.kafx.controller.TranslationController;

public class AlertVersion extends AlertCustom {

	public AlertVersion(String name, String local, String remote, URL url) {
		super(AlertType.CONFIRMATION);
		this.setGraphic(null);
		this.setTitle(TranslationController.translate("alert_version_title") + '(' + name + ')');
		this.setHeaderText(TranslationController.translate("alert_version_local") + ": " + local + " "
				+ TranslationController.translate("alert_version_remote") + ": " + remote);
		this.setContentText(TranslationController.translate("alert_version_main"));
	}

}
