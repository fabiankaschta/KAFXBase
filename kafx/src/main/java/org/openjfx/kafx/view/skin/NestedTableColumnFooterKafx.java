package org.openjfx.kafx.view.skin;

import java.util.List;

import org.openjfx.kafx.view.tableview.TableColumnFooter;
import org.openjfx.kafx.view.tableview.TableViewKafx;

import javafx.collections.ListChangeListener;
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
public class NestedTableColumnFooterKafx extends NestedTableColumnHeaderKafx {

	private TableColumnHeader label;

	public NestedTableColumnFooterKafx(TableColumnBase<?, ?> tc) {
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
	}

	/** {@inheritDoc} */
	@Override
	public void layoutChildren() {
		super.layoutChildren();
		// root
		if (tableView != null && getTableColumn() == null) {
			if (!getColumnHeaders().isEmpty()) {
				if (tableView.getFixedColumnsFooterGraphic() != null || tableView.getFixedColumnsFooterText() != null) {
					double h = getHeight() - snappedTopInset() - snappedBottomInset();
					int labelHeight = (int) getChildren().get(0).prefHeight(-1);
					double fixedColumnWidth = 0;
					for (TableColumnHeader c : getColumnHeaders()) {
						if (tableView.isFixedColumn((TableColumn<?, ?>) c.getTableColumn())) {
							fixedColumnWidth += snapSizeX(c.prefWidth(-1));
						}
					}
					// use first column header to move to front
					final TableColumnHeader n = getColumnHeaders().get(0);
					n.resize(fixedColumnWidth, snapSizeY(h - labelHeight));
					n.toFront();
					// find sort arrow grid
					List<Node> arrow = n.getChildrenUnmodifiable().filtered(c -> c instanceof GridPane);
					if (!arrow.isEmpty()) {
						// hide sort arrow
						((GridPane) arrow.getFirst()).setVisible(false);
					}
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected TableColumnHeader createTableColumnHeader(@SuppressWarnings("rawtypes") final TableColumnBase col) {
		if (col == null || col.getColumns().isEmpty() || col == getTableColumn()) {
			final TableColumnHeader tableColumnHeader = new TableColumnHeader(col);
			if (col != null) {
				TableViewKafx<?> tableView = (TableViewKafx<?>) (((TableColumn<?, ?>) col).getTableView());
				// see TableColumnHeader#updateSortGrid()
				Label label = ((Label) tableColumnHeader.getChildrenUnmodifiable().getFirst());
				if (tableView.isFixedColumn((TableColumn<?, ?>) col)) {
					if (col instanceof TableColumnFooter) {
						tableView.fixedColumnsFooterText().subscribe(text -> {
							if (text == null) {
								label.setText(((TableColumnFooter<?, ?>) col).getFooterText());
								label.setGraphic(((TableColumnFooter<?, ?>) col).getFooterGraphic());
								label.setAlignment(Pos.CENTER);
								label.setPadding(Insets.EMPTY);
								tableColumnHeader.getStyleClass().remove(TableViewKafxSkin.NO_SELECTION);
							} else {
								label.setText(text);
								label.setGraphic(tableView.getFixedColumnsFooterGraphic());
								label.setAlignment(Pos.CENTER_RIGHT);
								label.setPadding(new Insets(0, 5, 0, 0));
								tableColumnHeader.getStyleClass().add(TableViewKafxSkin.NO_SELECTION);
							}
						});
						tableView.fixedColumnsFooterGraphic().subscribe(graphic -> {
							if (graphic == null) {
								label.setText(((TableColumnFooter<?, ?>) col).getFooterText());
								label.setGraphic(((TableColumnFooter<?, ?>) col).getFooterGraphic());
								label.setAlignment(Pos.CENTER);
								label.setPadding(Insets.EMPTY);
								tableColumnHeader.getStyleClass().remove(TableViewKafxSkin.NO_SELECTION);
							} else {
								label.setText(tableView.getFixedColumnsFooterText());
								label.setGraphic(graphic);
								label.setAlignment(Pos.CENTER_RIGHT);
								label.setPadding(new Insets(0, 5, 0, 0));
								tableColumnHeader.getStyleClass().add(TableViewKafxSkin.NO_SELECTION);
							}
						});
					} else {
						label.textProperty().bind(tableView.fixedColumnsFooterText());
						label.graphicProperty().bind(tableView.fixedColumnsFooterGraphic());
						label.setAlignment(Pos.CENTER_RIGHT);
						label.setPadding(new Insets(0, 5, 0, 0));
						tableColumnHeader.getStyleClass().add(TableViewKafxSkin.NO_SELECTION);
					}
				} else {
					tableColumnHeader.getStyleClass().remove(TableViewKafxSkin.NO_SELECTION);
					if (col instanceof TableColumnFooter) {
						label.textProperty().bind(((TableColumnFooter<?, ?>) col).footerText());
						label.graphicProperty().bind(((TableColumnFooter<?, ?>) col).footerGraphic());
					} else {
						label.textProperty().set(null);
						label.setGraphic(null);
					}
				}
			}
			return tableColumnHeader;
		} else {
			return new NestedTableColumnFooterKafx(col);
		}
	}

}
