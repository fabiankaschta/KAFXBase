package org.openjfx.kafx.view.dialog;

import org.openjfx.kafx.controller.AutoSaveController;
import org.openjfx.kafx.controller.CloseController;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.controller.FileController;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class DialogFirstStart extends Dialog<Boolean> {

	public final static ButtonType NEW_FILE = new ButtonType(
			TranslationController.translate("dialog_firstStart_newFile"), ButtonData.OTHER);
	public final static ButtonType OPEN_FILE = new ButtonType(
			TranslationController.translate("dialog_firstStart_openFile"), ButtonData.OTHER);
	public final static ButtonType CLOSE = new ButtonType(TranslationController.translate("dialog_firstStart_cancel"),
			ButtonData.CANCEL_CLOSE);

	public DialogFirstStart() {
		this(TranslationController.translate("dialog_firstStart_title"),
				TranslationController.translate("dialog_firstStart_message"));
	}

	public DialogFirstStart(String title) {
		this(title, TranslationController.translate("dialog_firstStart_message"));
	}

	public DialogFirstStart(String title, String message) {
		this.setTitle(title);
		FontSizeController.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
		this.initOwner(Controller.getPrimaryStage());
		Label startMessage = new Label(message);
		startMessage.setWrapText(true);
		startMessage.setTextAlignment(TextAlignment.CENTER);
		this.getDialogPane().setContent(startMessage);
		this.getDialogPane().getButtonTypes().addAll(NEW_FILE, OPEN_FILE, CLOSE);
		this.setResultConverter(buttonType -> {
			if (buttonType == DialogFirstStart.NEW_FILE) {
				return FileController.newFile();
			} else if (buttonType == DialogFirstStart.OPEN_FILE) {
				return FileController.openFile();
			} else {
				return false;
			}
		});
		this.showAndWait().ifPresent(result -> {
			if (!result) {
				CloseController.close();
			} else {
				Controller.getPrimaryStage().show();
				AutoSaveController.start();
			}
		});
	}

}
