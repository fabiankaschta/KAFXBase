package org.openjfx.kafx.view.pane;

import org.openjfx.kafx.controller.Controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public class MenuBarMessage extends BorderPane {

	private final StringProperty message = new SimpleStringProperty();

	private final Label statusIndicator = new Label("status");

	public MenuBarMessage(Menu... menus) {
		Controller.setMenuBar(this);
		MenuBar menuBar = new MenuBar(menus);
		this.statusIndicator.textProperty().bind(message);
		// cant' use css menu-item because of transparent background
		this.statusIndicator.setStyle("-fx-padding: 0.333333em 0.41777em 0.333333em 0.41777em;");
		this.statusIndicator.getStyleClass().add("menu-bar");
		this.setCenter(menuBar);
		this.setRight(statusIndicator);
	}

	public void setMessage(String message) {
		this.message.set(message);
	}

}
