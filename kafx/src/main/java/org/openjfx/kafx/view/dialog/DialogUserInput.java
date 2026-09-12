package org.openjfx.kafx.view.dialog;

import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.view.dialog.userinput.UserInput;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public abstract class DialogUserInput<T> extends DialogCustom<T> {

	private final ObservableList<UserInput<?>> userInputs = FXCollections.observableArrayList();
	private final GridPane grid = new GridPane(10, 10);

	protected DialogUserInput(String title) {
		this(title, new DialogPane());
	}

	protected DialogUserInput(String title, DialogPane dialogPane) {
		ColumnConstraints c = new ColumnConstraints();
		c.setHgrow(Priority.ALWAYS);
		this.grid.getColumnConstraints().add(c);
		this.grid.getColumnConstraints().add(c);
		dialogPane.widthProperty().addListener((_, _, _) -> this.getDialogPane().getScene().getWindow().sizeToScene());
		dialogPane.heightProperty().addListener((_, _, _) -> this.getDialogPane().getScene().getWindow().sizeToScene());
		dialogPane.setContent(this.grid);
		this.setTitle(title);
		this.setDialogPane(dialogPane);
	}

	protected ObservableList<UserInput<?>> userInputsUnmodifiable() {
		return FXCollections.unmodifiableObservableList(this.userInputs);
	}

	public void resetInputsToDefault() {
		this.userInputs.forEach(userInput -> userInput.selectDefault());
	}

	public void addInput(UserInput<?> userInput, String label) {
		this.addInput(this.userInputs.size(), userInput, label);
	}

	public void addInput(UserInput<?> userInput, Node label) {
		this.addInput(this.userInputs.size(), userInput, label);
	}

	public void addInput(int index, UserInput<?> userInput, String label) {
		this.addInput(index, userInput, new Label(label + ':'));
	}

	public void addInput(int index, UserInput<?> userInput, Node label) {
		this.grid.addRow(index, label, userInput);
		this.grid.getRowConstraints().add(index, new RowConstraints());
		userInput.visibleProperty().subscribe(v -> {
			if (!v) {
				this.grid.getRowConstraints().get(index).minHeightProperty().unbind();
				this.grid.getRowConstraints().get(index).setMinHeight(0);
				this.grid.getRowConstraints().get(index).setMaxHeight(0);
				this.grid.getRowConstraints().get(index).setPrefHeight(0);
			} else {
				this.grid.getRowConstraints().get(index).minHeightProperty()
						.bind(FontSizeController.fontSizeProperty().multiply(2).add(1));
				this.grid.getRowConstraints().get(index).setMaxHeight(GridPane.USE_COMPUTED_SIZE);
				this.grid.getRowConstraints().get(index).setPrefHeight(GridPane.USE_COMPUTED_SIZE);
			}
			this.getDialogPane().autosize();
		});
		label.visibleProperty().bind(userInput.visibleProperty());
		this.userInputs.add(index, userInput);
		if (index == 0) {
			Platform.runLater(() -> userInput.requestFocus());
		}
	}

}
