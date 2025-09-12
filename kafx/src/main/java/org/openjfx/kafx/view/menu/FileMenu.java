package org.openjfx.kafx.view.menu;

import org.openjfx.kafx.controller.EncryptionController;
import org.openjfx.kafx.controller.FileController;
import org.openjfx.kafx.controller.PrintController;
import org.openjfx.kafx.controller.TranslationController;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class FileMenu extends Menu {

	public FileMenu() {
		super(TranslationController.translate("menu_file_title"));

		if (FileController.isInitialized()) {
			MenuItem menuItemNew = new MenuItem(TranslationController.translate("menu_file_new"));
			menuItemNew.setOnAction(e -> FileController.newFile(e));
			this.getItems().add(menuItemNew);

			MenuItem menuItemOpen = new MenuItem(TranslationController.translate("menu_file_open"));
			menuItemOpen.setOnAction(e -> FileController.openFile(e));
			this.getItems().add(menuItemOpen);

			MenuItem menuItemSave = new MenuItem(TranslationController.translate("menu_file_save"));
			menuItemSave.setOnAction(_ -> FileController.saveFile());
			this.getItems().add(menuItemSave);

			MenuItem menuItemSaveAs = new MenuItem(TranslationController.translate("menu_file_save_as"));
			menuItemSaveAs.setOnAction(_ -> FileController.saveAs());
			this.getItems().add(menuItemSaveAs);
		}

		if (PrintController.isInitialized()) {
			if (this.getItems().size() > 0) {
				this.getItems().add(new SeparatorMenuItem());
			}

			MenuItem menuItemPrint = new MenuItem(TranslationController.translate("menu_file_print"));
			menuItemPrint.setOnAction(_ -> PrintController.print());
			this.getItems().add(menuItemPrint);
		}

		if (EncryptionController.isInitialized()) {
			if (this.getItems().size() > 0) {
				this.getItems().add(new SeparatorMenuItem());
			}

			MenuItem menuItemChangePassword = new MenuItem(TranslationController.translate("menu_file_changePassword"));
			menuItemChangePassword.setOnAction(_ -> EncryptionController.changePassword());
			this.getItems().add(menuItemChangePassword);
		}
	}

}
