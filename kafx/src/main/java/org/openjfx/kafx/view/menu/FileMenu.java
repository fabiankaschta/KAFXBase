package org.openjfx.kafx.view.menu;

import org.openjfx.kafx.controller.Controller;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class FileMenu extends Menu {

	public FileMenu() {
		super(Controller.translate("menu_file_title"));

		MenuItem menuItemNew = new MenuItem(Controller.translate("menu_file_new"));
		menuItemNew.setOnAction(e -> Controller.newFile(e));
		this.getItems().add(menuItemNew);

		MenuItem menuItemOpen = new MenuItem(Controller.translate("menu_file_open"));
		menuItemOpen.setOnAction(e -> Controller.openFile(e));
		this.getItems().add(menuItemOpen);

		MenuItem menuItemSave = new MenuItem(Controller.translate("menu_file_save"));
		menuItemSave.setOnAction(_ -> Controller.saveFile());
		this.getItems().add(menuItemSave);

		MenuItem menuItemSaveAs = new MenuItem(Controller.translate("menu_file_save_as"));
		menuItemSaveAs.setOnAction(_ -> Controller.saveAs());
		this.getItems().add(menuItemSaveAs);

		this.getItems().add(new SeparatorMenuItem());

		MenuItem menuItemPrint = new MenuItem(Controller.translate("menu_file_print"));
		menuItemPrint.setOnAction(_ -> Controller.print());
		this.getItems().add(menuItemPrint);

		if (Controller.encryptionSupported()) {
			this.getItems().add(new SeparatorMenuItem());

			MenuItem menuItemChangePassword = new MenuItem(Controller.translate("menu_file_changePassword"));
			menuItemChangePassword.setOnAction(_ -> Controller.changePassword());
			this.getItems().add(menuItemChangePassword);
		}
	}

}
