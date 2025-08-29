package org.openjfx.kafx.view.dialog.userinput;

import org.openjfx.kafx.view.control.ChoiceBoxTreeItem;

import javafx.scene.control.TreeItem;
import javafx.util.Subscription;

public class UserInputChoiceBoxTreeItem<S> extends UserInputSingle<TreeItem<S>> {

	private final ChoiceBoxTreeItem<S> choiceBox;
	private Subscription valueSubscription;

	public UserInputChoiceBoxTreeItem(ChoiceBoxTreeItem<S> choiceBox) {
		this(choiceBox, choiceBox.getItems().size() == 0 ? null : choiceBox.getItems().get(0));
	}

	public UserInputChoiceBoxTreeItem(ChoiceBoxTreeItem<S> choiceBox, TreeItem<S> defaultValue) {
		super(defaultValue, defaultValue, false);
		this.choiceBox = choiceBox;
		this.choiceBox.getSelectionModel().select(defaultValue);
		this.choiceBox.selectionModelProperty().subscribe(m -> {
			if (this.valueSubscription != null) {
				this.valueSubscription.unsubscribe();
			}
			this.valueSubscription = m.selectedItemProperty().subscribe(v -> this.valueProperty().set(v));
		});
		this.valueProperty().subscribe(
				v -> this.choiceBox.getSelectionModel().clearAndSelect(this.choiceBox.getItems().indexOf(v)));
		this.getChildren().add(this.choiceBox);
	}

	@Override
	public void requestFocus() {
		this.choiceBox.requestFocus();
	}

}
