package org.openjfx.kafx.view.dialog.userinput;

import org.openjfx.kafx.view.control.ToggleSwitch;

public class UserInputToggleSwitch extends UserInputSingle<Boolean> {

	private final ToggleSwitch toggleSwitch;

	public UserInputToggleSwitch(ToggleSwitch toggleSwitch) {
		this(toggleSwitch, false);
	}

	public UserInputToggleSwitch(ToggleSwitch toggleSwitch, boolean defaultValue) {
		super(defaultValue, defaultValue, false);
		this.toggleSwitch = toggleSwitch;
		this.toggleSwitch.setOn(defaultValue);
		this.valueProperty().bindBidirectional(this.toggleSwitch.onProperty());
		this.getChildren().add(this.toggleSwitch);
	}

	@Override
	public void requestFocus() {
		this.toggleSwitch.requestFocus();
	}

}
