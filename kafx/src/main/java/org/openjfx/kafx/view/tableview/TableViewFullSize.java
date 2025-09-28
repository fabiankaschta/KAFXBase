package org.openjfx.kafx.view.tableview;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

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

	@SuppressWarnings("unchecked")
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
			TableHeaderRow header = (TableHeaderRow) this.queryAccessibleAttribute(AccessibleAttribute.HEADER);
			// consume drag (resize) attempts
			header.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
				if(e.getTarget() instanceof Rectangle) {
					e.consume();
				}
			});
			ObservableList<TableColumnHeader> columnHeaders = header.getRootHeader().getColumnHeaders();
			columnHeaders.addListener((ListChangeListener<TableColumnHeader>) _ -> {
				for (TableColumnHeader ch : columnHeaders) {
					ch.pseudoClassStateChanged(PseudoClass.getPseudoClass("last-column"),
							ch == columnHeaders.getLast());
				}
			});
			// allow programatical resize
			this.setColumnResizePolicy(r -> {
				if (r.getColumn() != null) {
					r.setColumnWidth(r.getColumn(), r.getColumn().getWidth() + r.getDelta());
				}
				return true;
			});
		});
		this.setRowFactory(_ -> {
			TableRow<T> row = new TableRow<>();
			row.indexProperty()
					.addListener((_, _, newValue) -> row.pseudoClassStateChanged(PseudoClass.getPseudoClass("last-row"),
							newValue.intValue() == TableViewFullSize.this.getItems().size() - 1));
			return row;
		});
	}

	@Override
	protected double computeMinWidth(double height) {
		return this.computePrefWidth(height);
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
	protected double computeMaxWidth(double height) {
		return this.computePrefWidth(height);
	}

	@Override
	protected double computeMinHeight(double width) {
		return computePrefHeight(width);
	}

	@Override
	protected double computePrefHeight(double width) {
		double height = 0;
		TableHeaderRow header = (TableHeaderRow) this.queryAccessibleAttribute(AccessibleAttribute.HEADER);
		if (header != null) {
			height = header.getHeight();
		}
		height += this.getFixedCellSize() * this.getItems().size();
		return height + this.snappedTopInset() + this.snappedBottomInset();
	}

	@Override
	protected double computeMaxHeight(double width) {
		return computePrefHeight(width);
	}

}
