package org.openjfx.kafx.view.control;

import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

// FIXME this does not properly resize
public class ToggleSwitch extends StackPane {
	
	private static final double TRACK_WIDTH = 20;
	private static final double TRACK_HEIGHT = 10;
	private static final double KNOB_DIAMETER = 15;
	private static final Duration ANIMATION_DURATION = Duration.seconds(0.1);

	private final TranslateTransition onTransition;
	private final TranslateTransition offTransition;

	private final StackPane track;
	private final Button knob;

	public ToggleSwitch() {
		getStyleClass().add("toggle-switch");

		track = new StackPane();
		track.getStyleClass().add("track");
		track.setMinHeight(TRACK_HEIGHT);
		track.setMaxHeight(TRACK_HEIGHT);
		track.setPrefHeight(TRACK_HEIGHT);

		knob = new Button();
		knob.getStyleClass().add("knob");
		knob.setShape(new Circle(KNOB_DIAMETER / 2));
		knob.setPrefSize(KNOB_DIAMETER, KNOB_DIAMETER);
		knob.setFocusTraversable(false);
		setAlignment(knob, Pos.CENTER_LEFT);

		getChildren().addAll(track, knob);
		setMinSize(TRACK_WIDTH, KNOB_DIAMETER);

		DoubleBinding trackEnd = Bindings.createDoubleBinding(() -> {
			return getWidth() - getHeight();
		}, heightProperty(), widthProperty());

		// define animations
		onTransition = new TranslateTransition(ANIMATION_DURATION, knob);
		onTransition.setFromX(0);
		onTransition.toXProperty().bind(trackEnd);

		offTransition = new TranslateTransition(ANIMATION_DURATION, knob);
		offTransition.fromXProperty().bind(trackEnd);
		offTransition.setToX(0);

		// add event handling
		EventHandler<Event> click = _ -> setOn(!isOn());
		setOnMouseClicked(click);
		knob.setOnMouseClicked(click);

		onProperty().addListener((_, _, nowOn) -> updateState(nowOn));
		updateState(isOn());
	}

	private String styleSheet;

	@Override
	public String getUserAgentStylesheet() {
		if (styleSheet == null) {
			styleSheet = ToggleSwitch.class.getResource("/org/openjfx/kafx/css/toggle-switch.css").toExternalForm();
		}
		return styleSheet;
	}

	private void updateState(Boolean nowOn) {
		onTransition.stop();
		offTransition.stop();

		if (nowOn != null && nowOn) {
			onTransition.play();
		} else {
			offTransition.play();
		}
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
	protected void layoutChildren() {
		final double width = getWidth();
		final double height = getHeight();
		final double top = getInsets().getTop();
		final double left = getInsets().getLeft();
		track.resize(width, height);
		track.relocate(left, top);
		knob.resize(height, height);
		knob.relocate(left, top);
	}

	/** {@inheritDoc} */
	@Override
	protected double computeMinWidth(double height) {
		return height * 2;
	}

	/** {@inheritDoc} */
	@Override
	protected double computeMinHeight(double width) {
		return width / 2;
	}

	/** {@inheritDoc} */
	@Override
	protected double computePrefWidth(double height) {
		return height * 2;
	}

	/** {@inheritDoc} */
	@Override
	protected double computePrefHeight(double width) {
		return width / 2;
	}

	/** {@inheritDoc} */
	@Override
	protected double computeMaxWidth(double height) {
		return Double.MAX_VALUE;
	}

	/** {@inheritDoc} */
	@Override
	protected double computeMaxHeight(double width) {
		return Double.MAX_VALUE;
	}
}
