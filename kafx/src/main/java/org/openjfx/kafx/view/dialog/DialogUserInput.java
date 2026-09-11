package org.openjfx.kafx.view.dialog;

import org.openjfx.kafx.view.dialog.userinput.UserInput;
import org.openjfx.kafx.view.tableview.LabeledUserInputTableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

public abstract class DialogUserInput<T> extends DialogCustom<T> {

	private final ObservableList<UserInput<?>> userInputs = FXCollections.observableArrayList();
	private final LabeledUserInputTableView grid = new LabeledUserInputTableView();

	protected DialogUserInput(String title) {
		this(title, new DialogPane());
	}

	protected DialogUserInput(String title, DialogPane dialogPane) {
		this.setTitle(title);
		this.setDialogPane(dialogPane);
		this.grid.widthProperty().addListener((_, _, _) -> this.getDialogPane().getScene().getWindow().sizeToScene());
		this.grid.heightProperty().addListener((_, _, _) -> this.getDialogPane().getScene().getWindow().sizeToScene());
		this.getDialogPane().setContent(grid);
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
		this.grid.addInput(index, userInput, label);
		this.userInputs.add(index, userInput);
		if (index == 0) {
			Platform.runLater(() -> userInput.requestFocus());
		}
	}

}
