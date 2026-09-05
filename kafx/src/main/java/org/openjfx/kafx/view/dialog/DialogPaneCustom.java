package org.openjfx.kafx.view.dialog;

import org.openjfx.kafx.controller.TranslationController;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Allows setting the text of the details button. <br>
 * Prevents resizing on expansion/retraction.
 */
public class DialogPaneCustom extends DialogPane {

	private final StringProperty moreText = new SimpleStringProperty(this, "moreText",
			TranslationController.translate("dialog_detail_button_more"));
	private final StringProperty lessText = new SimpleStringProperty(this, "lessText",
			TranslationController.translate("dialog_detail_button_less"));

	public void setDetailsButtonMoreText(String moreText) {
		this.moreText.set(moreText);
	}

	public void setDetailsButtonLessText(String lessText) {
		this.lessText.set(lessText);
	}

	// copy from DialogPane, only Strings changed
	@Override
	protected Node createDetailsButton() {
		final Hyperlink detailsButton = new Hyperlink();

		InvalidationListener expandedListener = _ -> {
			final boolean isExpanded = isExpanded();
			detailsButton.textProperty().unbind();
			detailsButton.textProperty().bind(isExpanded ? lessText : moreText);
			detailsButton.getStyleClass().setAll("details-button", (isExpanded ? "less" : "more"));
		};

		// we call the listener immediately to ensure the state is correct at start up
		expandedListener.invalidated(null);
		expandedProperty().addListener(expandedListener);

		detailsButton.setOnAction(_ -> setExpanded(!isExpanded()));
		return detailsButton;
	}

	// copy from com.sun.javafx.scene.control.skin.Utils
	private static double boundedSize(double value, double min, double max) {
		// if max < value, return max
		// if min > value, return min
		// if min > max, return min
		return Math.min(Math.max(value, min), Math.max(min, max));
	}

	// adapted copy from DialogPane (circumvented some private methods)
	private double oldHeight = -1;

	@Override
	protected void layoutChildren() {
		// create the nodes up front so we can work out sizing
		Node expandableContent = getExpandableContent();
		Node header = getHeader();
		Node content = getContent();
		Node graphic = null;
		Node buttonBar = null;
		for (Node child : getChildren()) {
			if (child instanceof ButtonBar) {
				buttonBar = child;
			} else if (content == null && child instanceof Label) {
				content = child;
			} else if (child instanceof GridPane) {
				graphic = child;
			}
		}
		if (header == null) {
			header = graphic;
		}

		String headerText = getHeaderText();
		final boolean hasHeader = getHeader() != null || (headerText != null && !headerText.isEmpty());

		// fix: getWidth & getHeight jump values after extension/retraction
		// therefore, these values are replaced with scene values, if available

		final double dialogWidth = getScene() == null ? 0 : getScene().getWidth();
		// snapped insets code commented out to resolve JDK-8095678
		// - (snappedLeftInset() + snappedRightInset());
		final double w = dialogWidth != 0 ? dialogWidth : Math.max(minWidth(-1), getWidth());

		final double minHeight = minHeight(w);
		final double prefHeight = prefHeight(w);
		final double maxHeight = maxHeight(w);
		final double dialogHeight = getScene() == null ? 0 : getScene().getHeight();
		final double currentHeight = dialogHeight != 0 ? dialogHeight : getHeight();
		double h;

		if (prefHeight > currentHeight && prefHeight > minHeight && (prefHeight <= dialogHeight || dialogHeight == 0)) {
			h = boundedSize(prefHeight, minHeight, maxHeight);
			resize(w, h);
		} else {
			boolean isDialogGrowing = currentHeight > oldHeight;

			if (isDialogGrowing) {
				double _h = currentHeight < prefHeight ? Math.min(prefHeight, currentHeight)
						: Math.max(prefHeight, dialogHeight);
				h = boundedSize(_h, minHeight, maxHeight);
			} else {
				h = boundedSize(Math.min(currentHeight, dialogHeight), minHeight, maxHeight);
			}
			resize(w, h);
		}

		h -= (snappedTopInset() + snappedBottomInset());

		oldHeight = h;

		final double leftPadding = snappedLeftInset();
		final double topPadding = snappedTopInset();
		final double rightPadding = snappedRightInset();

		final double graphicPrefWidth = hasHeader || graphic == null ? 0 : graphic.prefWidth(-1);
		final double headerPrefHeight = hasHeader ? header.prefHeight(w) : 0;
		final double buttonBarPrefHeight = buttonBar == null ? 0 : buttonBar.prefHeight(w);
		final double graphicPrefHeight = hasHeader || graphic == null ? 0 : graphic.prefHeight(-1);

		final double expandableContentPrefHeight;
		final double contentAreaHeight;
		final double contentAndGraphicHeight;

		final double availableContentWidth = w - graphicPrefWidth - leftPadding - rightPadding;

		// changed here content always gets the lowest precedence
		expandableContentPrefHeight = isExpanded() ? expandableContent.prefHeight(w) : 0;
		contentAreaHeight = h - (headerPrefHeight + expandableContentPrefHeight + buttonBarPrefHeight);
		contentAndGraphicHeight = hasHeader ? contentAreaHeight : Math.max(graphicPrefHeight, contentAreaHeight);

		double x = leftPadding;
		double y = topPadding;

		if (!hasHeader) {
			if (graphic != null) {
				graphic.resizeRelocate(x, y, graphicPrefWidth, graphicPrefHeight);
				x += graphicPrefWidth;
			}
		} else {
			header.resizeRelocate(x, y, w - (leftPadding + rightPadding), headerPrefHeight);
			y += headerPrefHeight;
		}

		content.resizeRelocate(x, y, availableContentWidth, contentAreaHeight);
		y += hasHeader ? contentAreaHeight : contentAndGraphicHeight;

		if (expandableContent != null) {
			expandableContent.resizeRelocate(leftPadding, y, w - rightPadding, expandableContentPrefHeight);
			y += expandableContentPrefHeight;
		}

		if (buttonBar != null) {
			buttonBar.resizeRelocate(leftPadding, y, w - (leftPadding + rightPadding), buttonBarPrefHeight);
		}
	}

}
