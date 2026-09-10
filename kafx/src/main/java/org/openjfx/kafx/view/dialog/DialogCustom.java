package org.openjfx.kafx.view.dialog;

import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public abstract class DialogCustom<T> extends Dialog<T> {

	public final static ButtonType CANCEL = ButtonType.CANCEL;
	public final static ButtonType ADD = new ButtonType(TranslationController.translate("dialog_add_button_add"),
			ButtonData.OK_DONE);
	public final static ButtonType ADD_MORE = ButtonType.NEXT;
	public final static ButtonType EDIT = new ButtonType(TranslationController.translate("dialog_edit_button_edit"),
			ButtonData.OK_DONE);
	public final static ButtonType DONE = new ButtonType(TranslationController.translate("dialog_button_done"),
			ButtonData.OK_DONE);

	public DialogCustom() {
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
		this.initOwner(Controller.getPrimaryStage());
	}

}
