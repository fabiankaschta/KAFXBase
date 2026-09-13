package org.openjfx.kafx.view.tableview;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;

public class TableViewFullSize<T> extends TableView<T> {

	public TableViewFullSize(ObservableValue<? extends Number> fixedCellSize) {
		this(fixedCellSize.getValue().doubleValue());
		this.fixedCellSizeProperty().bind(fixedCellSize);
	}

	public TableViewFullSize(ObservableValue<? extends Number> fixedCellSize, ObservableList<T> items) {
		this(fixedCellSize.getValue().doubleValue(), items);
		this.fixedCellSizeProperty().bind(fixedCellSize);
	}

	public TableViewFullSize(double fixedCellSize) {
		this(fixedCellSize, FXCollections.observableArrayList());
	}

	public TableViewFullSize(double fixedCellSize, ObservableList<T> items) {
		super(items);
		// for initial sizes
		this.setColumnResizePolicy(_ -> true);
		this.setFixedCellSize(fixedCellSize);
		this.getStyleClass().add("table-view-full-size");
		this.skinProperty().addListener((_, _, _) -> {
			ScrollBar scrollBarVertical = (ScrollBar) this
					.queryAccessibleAttribute(AccessibleAttribute.VERTICAL_SCROLLBAR);
			scrollBarVertical.setPrefSize(0, 0);
			scrollBarVertical.setVisible(false);
			ScrollBar scrollBarHorizontal = (ScrollBar) this
					.queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
			scrollBarHorizontal.setPrefSize(0, 0);
			scrollBarHorizontal.setVisible(false);
		});
		
		this.setMinWidth(USE_PREF_SIZE);
		this.setMaxWidth(USE_PREF_SIZE);
		this.setMinHeight(USE_PREF_SIZE);
		this.setMaxHeight(USE_PREF_SIZE);
	}

	@Override
	protected double computePrefWidth(double height) {
		double width = 0;
		for (TableColumn<T, ?> column : super.getColumns()) {
			if (column.isVisible()) {
				width += snapSizeX(column.getWidth());
			}
		}
		return width + this.snappedLeftInset() + this.snappedRightInset();
	}

	@Override
	protected double computePrefHeight(double width) {
		double height = 0;
		TableHeaderRow header = (TableHeaderRow) this.queryAccessibleAttribute(AccessibleAttribute.HEADER);
		if (header != null) {
			if (header.getRootHeader() != null && !header.getRootHeader().getColumnHeaders().isEmpty()) {
				height = snapSizeY(calculateNestedColumnHeaderHeight(header.getRootHeader())) + header.snappedTopInset()
						+ header.snappedBottomInset();
			} else {
				height = snapSizeY(header.getHeight()) + header.snappedTopInset() + header.snappedBottomInset();
			}
		}
		height += snapSizeY(this.getFixedCellSize()) * this.getItems().size();
		return height + this.snappedTopInset() + this.snappedBottomInset();
	}

	private double calculateNestedColumnHeaderHeight(TableColumnHeader root) {
		if (root instanceof NestedTableColumnHeader) {
			NestedTableColumnHeader nestedRoot = (NestedTableColumnHeader) root;
			TableColumnHeader top = (TableColumnHeader) nestedRoot.getChildrenUnmodifiable().getFirst();
			double height = 0;
			for (TableColumnHeader child : nestedRoot.getColumnHeaders()) {
				height = Math.max(height, snapSizeY(calculateNestedColumnHeaderHeight(child)));
			}
			return top.prefHeight(-1) + height;
		} else {
			return root.getHeight();
		}
	}

}
