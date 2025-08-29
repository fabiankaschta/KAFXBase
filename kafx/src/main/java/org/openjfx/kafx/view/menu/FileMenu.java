package org.openjfx.kafx.view.menu;

import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.lang.Translator;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class FileMenu extends Menu {

	public FileMenu() {
		super(Translator.get("menu_file_title"));

		MenuItem menuItemNew = new MenuItem(Translator.get("menu_file_new"));
		menuItemNew.setOnAction(e -> Controller.newFile(e));
		this.getItems().add(menuItemNew);

		MenuItem menuItemOpen = new MenuItem(Translator.get("menu_file_open"));
		menuItemOpen.setOnAction(e -> Controller.openFile(e));
		this.getItems().add(menuItemOpen);

		MenuItem menuItemSave = new MenuItem(Translator.get("menu_file_save"));
		menuItemSave.setOnAction(_ -> Controller.saveFile());
		this.getItems().add(menuItemSave);

		MenuItem menuItemSaveAs = new MenuItem(Translator.get("menu_file_save_as"));
		menuItemSaveAs.setOnAction(_ -> Controller.saveAs());
		this.getItems().add(menuItemSaveAs);

		this.getItems().add(new SeparatorMenuItem());

		MenuItem menuItemPrint = new MenuItem(Translator.get("menu_file_print"));
		menuItemPrint.setOnAction(_ -> Controller.print());
		this.getItems().add(menuItemPrint);

		if (Controller.encryptionSupported()) {
			this.getItems().add(new SeparatorMenuItem());

			MenuItem menuItemChangePassword = new MenuItem(Translator.get("menu_file_changePassword"));
			menuItemChangePassword.setOnAction(_ -> Controller.changePassword());
			this.getItems().add(menuItemChangePassword);
		}
	}

}
