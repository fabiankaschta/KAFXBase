package org.openjfx.kafx.view.dialog;

import java.util.List;

import org.openjfx.kafx.view.dialog.userinput.UserInput;

import javafx.beans.binding.BooleanExpression;
import javafx.scene.control.Button;

public abstract class DialogEdit<T> extends DialogUserInputButtonBinding<Boolean> {

	private final Button editButton;

	public DialogEdit(String title, T objectToEdit) {
		super(title);

		this.getDialogPane().getButtonTypes().addAll(EDIT, CANCEL);

		this.editButton = (Button) this.getDialogPane().lookupButton(EDIT);

		this.bindButtonDisable(this.editButton);

		this.setResultConverter(dialogButton -> {
			if (dialogButton == EDIT) {
				return edit(objectToEdit);
			} else {
				return false;
			}
		});
	}

	/**
	 * {@inheritDoc} <br>
	 * This method returns just {@code
	 * userInput.visibleProperty().and(userInput.isSelectedExpression())
				.and(userInput.isSelectedDefaultExpression().not()) }, so that the
	 * value is if the userInput is visible and any value but the default value is
	 * selected in it.
	 * 
	 * @param userInput any user Input already added to this dialog
	 * @see #bindButtonDisable(Button)
	 * @see #setCustomButtonDisableExpression(UserInput, BooleanExpression)
	 */
	protected BooleanExpression getDefaultButtonDisableExpression(UserInput<?> userInput) {
		return userInput.visibleProperty().and(userInput.isSelectedExpression())
				.and(userInput.isSelectedDefaultExpression().not());
	}

	/**
	 * {@inheritDoc} <br>
	 * This defaults to if all of the expressions are {@code false}.
	 * 
	 * @param buttonDisableExpressions
	 * @return whether the button should become disabled
	 */
	protected boolean isButtonDisabled(List<BooleanExpression> buttonDisableExpressions) {
		for (BooleanExpression observable : buttonDisableExpressions) {
			if (observable.get()) {
				return false;
			}
		}
		return true;
	}

	public abstract boolean edit(T objectToEdit);
}
