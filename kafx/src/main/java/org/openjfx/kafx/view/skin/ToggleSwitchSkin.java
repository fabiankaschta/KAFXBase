package org.openjfx.kafx.view.skin;

import org.openjfx.kafx.view.control.ToggleSwitch;

import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.skin.LabeledSkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ToggleSwitchSkin extends LabeledSkinBase<ToggleSwitch> {

	private static final Duration ANIMATION_DURATION = Duration.seconds(0.1);
	private static final double heightToWidth = 1.75;

	private final StackPane track;
	private final Button knob;
	private final TranslateTransition onTransition;
	private final TranslateTransition offTransition;

	public ToggleSwitchSkin(ToggleSwitch control) {
		super(control);

		this.track = new StackPane();
		this.track.getStyleClass().add("track");

		this.knob = new Button();
		this.knob.getStyleClass().add("knob");
		this.knob.setShape(new Circle(1));

		DoubleBinding trackEnd = Bindings.createDoubleBinding(() -> {
			return this.track.getWidth() - this.track.getHeight();
		}, this.track.heightProperty(), this.track.widthProperty());

		// define animations
		this.onTransition = new TranslateTransition(ANIMATION_DURATION, knob);
		this.onTransition.setFromX(0);
		this.onTransition.toXProperty().bind(trackEnd);

		this.offTransition = new TranslateTransition(ANIMATION_DURATION, knob);
		this.offTransition.fromXProperty().bind(trackEnd);
		this.offTransition.setToX(0);

		// add event handling
		EventHandler<Event> click = _ -> getSkinnable().fire();
		this.track.setOnMouseClicked(click);
		this.knob.setOnMouseClicked(click);

		control.onProperty().addListener((_, _, nowOn) -> updateState(nowOn));
		updateState(control.isOn());

		updateChildren();
	}

	private void updateState(Boolean nowOn) {
		this.onTransition.stop();
		this.offTransition.stop();

		if (nowOn != null && nowOn) {
			this.onTransition.play();
		} else {
			this.offTransition.play();
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void updateChildren() {
		super.updateChildren();
		if (this.track != null && this.knob != null) {
			this.getChildren().addAll(this.track, this.knob);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return leftInset + snapSizeY(height * heightToWidth) + rightInset;
	}

	/** {@inheritDoc} */
	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return topInset + snapSizeX(width / heightToWidth) + bottomInset;
	}

	@Override
	protected void layoutChildren(double x, double y, double w, double h) {
		final ToggleSwitch toggleSwitch = getSkinnable();
		final double trackWidth = snapSizeX(toggleSwitch.prefWidth(h));
		final double trackHeight = snapSizeY(toggleSwitch.prefHeight(w));
		final double computeWidth = Math.max(toggleSwitch.prefWidth(h), toggleSwitch.minWidth(h));
		final double labelWidth = Math.min(computeWidth - trackWidth, w - snapSizeX(trackWidth));
		final double labelHeight = Math.min(toggleSwitch.prefHeight(labelWidth), h);
		final double maxHeight = Math.max(trackHeight, labelHeight);
		final double xOffset = computeXOffset(w, labelWidth + trackWidth, toggleSwitch.getAlignment().getHpos()) + x;
		final double yOffset = computeYOffset(h, maxHeight, toggleSwitch.getAlignment().getVpos()) + y;

		this.layoutLabelInArea(xOffset + trackWidth, yOffset, labelWidth, maxHeight, toggleSwitch.getAlignment());
		this.track.resize(trackWidth, trackHeight);
		this.knob.resize(trackHeight, trackHeight);
		this.positionInArea(this.track, xOffset, yOffset, trackWidth, maxHeight, 0,
				toggleSwitch.getAlignment().getHpos(), toggleSwitch.getAlignment().getVpos());
		if (toggleSwitch.isOn()) {
			this.knob.setTranslateX(trackWidth - trackHeight);
		}
		this.positionInArea(this.knob, xOffset, yOffset, trackWidth, maxHeight, 0,
				toggleSwitch.getAlignment().getHpos(), toggleSwitch.getAlignment().getVpos());
	}

	private static double computeXOffset(double width, double contentWidth, HPos hpos) {
		if (hpos == null) {
			return 0;
		}

		switch (hpos) {
		case LEFT:
			return 0;
		case CENTER:
			return (width - contentWidth) / 2;
		case RIGHT:
			return width - contentWidth;
		default:
			return 0;
		}
	}

	private static double computeYOffset(double height, double contentHeight, VPos vpos) {
		if (vpos == null) {
			return 0;
		}

		switch (vpos) {
		case TOP:
			return 0;
		case CENTER:
			return (height - contentHeight) / 2;
		case BOTTOM:
			return height - contentHeight;
		default:
			return 0;
		}
	}
}
