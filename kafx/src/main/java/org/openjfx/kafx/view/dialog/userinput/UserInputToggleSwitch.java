package org.openjfx.kafx.view.dialog.userinput;

import org.controlsfx.control.ToggleSwitch;

public class UserInputToggleSwitch extends UserInputSingle<Boolean> {

	private final ToggleSwitch toggleSwitch;

	public UserInputToggleSwitch(ToggleSwitch checkBox) {
		this(checkBox, false);
	}

	public UserInputToggleSwitch(ToggleSwitch toggleSwitch, boolean defaultValue) {
		super(defaultValue, defaultValue, false);
		this.toggleSwitch = toggleSwitch;
		this.toggleSwitch.setSelected(defaultValue);
		this.valueProperty().bindBidirectional(this.toggleSwitch.selectedProperty());
		this.getChildren().add(this.toggleSwitch);
	}

	@Override
	public void requestFocus() {
		this.toggleSwitch.requestFocus();
	}

}
