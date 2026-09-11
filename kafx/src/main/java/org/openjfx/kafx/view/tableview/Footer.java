package org.openjfx.kafx.view.tableview;

import org.openjfx.kafx.controller.FontSizeController;

import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;

class Footer<T> extends TableView<FooterData<T>> {

	Footer(FooterData<T> item) {
		this.fixedCellSizeProperty().bind(FontSizeController.fontSizeProperty().multiply(2).add(1));
		this.setPrefHeight(USE_COMPUTED_SIZE);
		this.setSelectionModel(null);
		this.getStyleClass().addAll("table-view-full-size", "table-view-no-focus", "tableview-footer");
		this.getItems().add(item);
	}

	@Override
	protected double computePrefHeight(double width) {
		ScrollBar footerScrollBarHorizontal = (ScrollBar) this
				.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
		double height = snapSizeY(this.getFixedCellSize()) * this.getItems().size();
		if (footerScrollBarHorizontal != null && footerScrollBarHorizontal.isVisible()) {
			height += footerScrollBarHorizontal.getHeight();
		}
		return height + this.snappedTopInset() + this.snappedBottomInset();
	}

	@Override
	protected double computeMinHeight(double width) {
		return computePrefHeight(width);
	}

	@Override
	protected double computeMaxHeight(double width) {
		return computePrefHeight(width);
	}

}
