package org.openjfx.kafx.view.dialog;

import java.util.List;

import org.openjfx.kafx.view.dialog.userinput.UserInput;

import javafx.beans.binding.BooleanExpression;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public abstract class DialogAdd<T> extends DialogUserInputButtonBinding<T> {

	private final Button addButton;
	private final Button addMoreButton;

	public DialogAdd(String title) {
		this(title, true);
	}

	public DialogAdd(String title, boolean addMoreButton) {
		super(title);

		this.getDialogPane().getButtonTypes().addAll(ADD_MORE, ADD, CANCEL);

		this.addButton = (Button) this.getDialogPane().lookupButton(ADD);

		this.addMoreButton = (Button) this.getDialogPane().lookupButton(ADD_MORE);
		this.addMoreButton.addEventFilter(ActionEvent.ACTION, e -> {
			create();
			userInputsUnmodifiable().get(0).requestFocus();
			resetInputsToDefault();
			e.consume();
		});
		this.addMoreButton.setVisible(addMoreButton);

		this.bindButtonDisable(this.addButton);
		this.bindButtonDisable(this.addMoreButton);

		this.setResultConverter(dialogButton -> {
			if (dialogButton == ADD) {
				return create();
			} else {
				return null;
			}
		});
	}

	public void setDefaultButtonAdd() {
		this.addButton.setDefaultButton(false);
		this.addMoreButton.setDefaultButton(true);
	}

	public void setDefaultButtonAddMore() {
		this.addButton.setDefaultButton(false);
		this.addMoreButton.setDefaultButton(true);
	}

	public void showAddMoreButton(boolean show) {
		this.addMoreButton.setVisible(show);
	}

	/**
	 * {@inheritDoc} <br>
	 * This method returns just {@code
	 * userInput.visibleProperty().and(userInput.isSelectedExpression().not()) }, so
	 * that the value is if the userInput is visible and no value is selected in it.
	 * 
	 * @param userInput any user Input already added to this dialog
	 * @see #bindButtonDisable(Button)
	 * @see #setCustomButtonDisableExpression(UserInput, BooleanExpression)
	 */
	protected BooleanExpression getDefaultButtonDisableExpression(UserInput<?> userInput) {
		return userInput.visibleProperty().and(userInput.isSelectedExpression().not());
	}

	/**
	 * {@inheritDoc} <br>
	 * This defaults to if any of the expressions is {@code true}.
	 * 
	 * @param buttonDisableExpressions
	 * @return whether the button should become disabled
	 */
	protected boolean isButtonDisabled(List<BooleanExpression> buttonDisableExpressions) {
		for (BooleanExpression observable : buttonDisableExpressions) {
			if (observable.get()) {
				return true;
			}
		}
		return false;
	}

	public abstract T create();
}
