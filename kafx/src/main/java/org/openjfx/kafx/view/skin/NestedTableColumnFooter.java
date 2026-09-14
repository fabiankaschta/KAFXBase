// NEW CLASS
package org.openjfx.kafx.view.skin;

import org.openjfx.kafx.view.tableview.TableView3;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * A cell column footer.
 */
public class NestedTableColumnFooter extends NestedTableColumnHeader3 {

	private TableColumnHeader label;

	public NestedTableColumnFooter(TableColumnBase<?, ?> tc) {
		super(tc);
		// not root
		if (getTableColumn() != null) {
			// remove visibility of TableColumnHeader for columns width children
			getChildren().addListener((ListChangeListener<Node>) _ -> {
				if (label == null) {
					for (Node n : getChildren()) {
						// getColumnHeaders() only returns child columns, so this works
						if (n instanceof TableColumnHeader && !getColumnHeaders().contains(n)) {
							label = (TableColumnHeader) n;
						}
					}
					if (label != null) {
						label.setVisible(false);
					}
				}
			});
		}
		// remove all interactive features
		this.addEventFilter(MouseEvent.ANY, e -> e.consume());
		
		// fixed columns don't indicate selection
		this.getStyleClass().add("nested-column-footer");
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		// root
		if (getTableColumn() == null) {
			if (!getColumnHeaders().isEmpty()) {
				double h = getHeight() - snappedTopInset() - snappedBottomInset();
				int labelHeight = (int) getChildren().get(0).prefHeight(-1);
				double fixedColumnWidth = 0;
				for (TableColumnHeader c : getColumnHeaders()) {
					if (tableView.getFixedColumns().contains(c.getTableColumn())) {
						fixedColumnWidth += snapSizeX(c.prefWidth(-1));
					}
				}
				// use first column header to move to front
				final TableColumnHeader n = getColumnHeaders().get(0);
				n.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) c -> {
					while(c.next()) {
						if(c.wasAdded()) {
							for(Node node : c.getAddedSubList()) {
								// this should be the sort arrow
								if(node instanceof GridPane) {
									// this fixes the gap between label and where the arrow would be
									((GridPane) node).setPrefWidth(0);
								}
							}
						}
					}
				});
				n.resize(fixedColumnWidth, snapSizeY(h - labelHeight));
				n.toFront();
			}
		}
	}

	public void layoutFixedColumns() {
		if (skin == null && tableView == null) {
			init();
		}

		if (skin == null || getChildren().isEmpty()) {
			return;
		}
		double h = getHeight() - snappedTopInset() - snappedBottomInset();
		double hbarValue = skin.getHBar().getValue();

		int labelHeight = (int) getChildren().get(0).prefHeight(-1);
		double fixedColumnWidth = 0;
		double x = snappedLeftInset();
		int max = getColumnHeaders().size();
		max = max > tableView.getVisibleLeafColumns().size() ? tableView.getVisibleLeafColumns().size() : max;
		max = max > tableView.getColumns().size() ? tableView.getColumns().size() : max;
		for (int j = 0; j < max; j++) {
			final TableColumnHeader n = getColumnHeaders().get(j);
			if (!n.isVisible()) {
				continue;
			}

			final double prefWidth = snapSize(n.prefWidth(-1));
			// FIXME removing this line helps with laying out the footer somehow - what is
			// it for?
			// n.resize(prefWidth, snapSize(h - labelHeight));
			// If the column is fixed
			TableColumn<?, ?> column = (TableColumn<?, ?>) n.getTableColumn();
			boolean isLeafColumn = column.getParentColumn() != null;
			while (column.getParentColumn() != null) {
				column = (TableColumn<?, ?>) column.getParentColumn();
			}
			if (tableView.isColumnFixingEnabled() && tableView.getFixedColumns().contains(column)) {
				double tableCellX = 0;
				// If the column is hidden we have to translate it
				if (hbarValue + fixedColumnWidth > x) {

					tableCellX = Math.abs((isLeafColumn ? 0 : hbarValue) + fixedColumnWidth - x);

					n.toFront();
					fixedColumnWidth += prefWidth;
				}
				n.relocate(x + tableCellX, labelHeight + snappedTopInset());
			}

			x += prefWidth;
		}
	}

	/** {@inheritDoc} */
	@Override
	protected TableColumnHeader createTableColumnHeader(final TableColumnBase col) {
		if (col == null || col.getColumns().isEmpty() || col == getTableColumn()) {
			final TableColumnHeader tableColumnHeader = new TableColumnHeader(col);
			if (col != null) {
				TableView3<?> tableView = (TableView3<?>) (((TableColumn<?, ?>) col).getTableView());
				for (Node n : tableColumnHeader.getChildrenUnmodifiable()) {
					if (n instanceof Label) {
						Label l = ((Label) n);
						if (tableView.getFixedColumns().contains(col)) {
							l.textProperty().bind(tableView.footerTextFixedColumns());
							l.setGraphic(null);
							l.setAlignment(Pos.CENTER_RIGHT);
							l.setPadding(new Insets(0, 5, 0, 0));
						} else {
							l.textProperty().bind(tableView.footerTextForColumn((TableColumn<?, ?>) col));
							l.setGraphic(null);
						}
					}
				}
			}
			addMousePressedListener(tableColumnHeader);
			addMouseReleasedListener(tableColumnHeader);
			return tableColumnHeader;
		} else {
			final NestedTableColumnFooter rootHeader = new NestedTableColumnFooter(col);
			final ObservableList<Node> rootChildren = rootHeader.getChildren();
			rootChildren.addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable o) {
					if (rootChildren.size() > 0) {
						final TableColumnHeader tableColumnHeader = (TableColumnHeader) rootChildren.get(0);
						addMouseReleasedListener(tableColumnHeader);
						rootChildren.removeListener(this);
					}
				}
			});
			addMousePressedListener(rootHeader);
			return rootHeader;
		}
	}

    /** {@inheritDoc} */
    @Override protected double computePrefHeight(double width) {
//        checkState();
//
//        double height = 0.0F;
//
//        if (getColumnHeaders() != null) {
//            for (TableColumnHeader n : getColumnHeaders()) {
//                height = Math.max(height, n.prefHeight(-1));
//            }
//        }
//
//        double labelHeight = 0.0;
//        if (label.isVisible() && getTableColumn() != null) {
//            labelHeight = label.prefHeight(-1);
//        }

        return 25;
    }

}
