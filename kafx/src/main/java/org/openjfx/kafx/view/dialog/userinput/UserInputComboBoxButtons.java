package org.openjfx.kafx.view.dialog.userinput;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class UserInputComboBoxButtons<S> extends UserInputComboBox<S> {

	private final HBox pane;

	public UserInputComboBoxButtons(ComboBox<S> comboBox) {
		this(comboBox, comboBox.getItems().size() == 0 ? null : comboBox.getItems().get(0));
	}

	public UserInputComboBoxButtons(ComboBox<S> comboBox, S defaultValue) {
		super(comboBox, defaultValue);
		this.getChildren().clear();
		this.pane = new HBox(5, comboBox);
		HBox.setHgrow(comboBox, Priority.ALWAYS);
		this.getChildren().add(this.pane);
	}

	public void addButton(Button button) {
		this.pane.getChildren().add(button);
	}

}
