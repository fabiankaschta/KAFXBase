package org.openjfx.kafx.view.control;

import org.openjfx.kafx.view.skin.ToggleSwitchSkin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Skin;

public class ToggleSwitch extends ButtonBase {

	public ToggleSwitch() {
		getStyleClass().add("toggle-switch");
	}

	private String styleSheet;

	@Override
	public String getUserAgentStylesheet() {
		if (styleSheet == null) {
			styleSheet = ToggleSwitch.class.getResource("/org/openjfx/kafx/css/toggle-switch.css").toExternalForm();
		}
		return styleSheet;
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ToggleSwitchSkin(this);
	}

	private static final PseudoClass ON_PSEUDO_CLASS = PseudoClass.getPseudoClass("on");

	public void setOn(boolean on) {
		this.on.set(on);
	}

	public boolean isOn() {
		return on.get();
	}

	public BooleanProperty onProperty() {
		return on;
	}

	public BooleanProperty on = new BooleanPropertyBase(false) {
		@Override
		protected void invalidated() {
			pseudoClassStateChanged(ON_PSEUDO_CLASS, get());
		}

		@Override
		public Object getBean() {
			return ToggleSwitch.this;
		}

		@Override
		public String getName() {
			return "on";
		}
	};

	@Override
	public void fire() {
		setOn(!isOn());
	}
}
