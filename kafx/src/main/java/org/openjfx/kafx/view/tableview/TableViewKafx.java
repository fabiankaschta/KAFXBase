package org.openjfx.kafx.view.tableview;

import java.util.List;

import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.view.skin.TableFooterRowKafx;
import org.openjfx.kafx.view.skin.TableHeaderRowKafx;
import org.openjfx.kafx.view.skin.TableViewKafxSkin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;

public class TableViewKafx<S> extends TableView<S> {

	private final IntegerProperty fixedColumnsIndex = new SimpleIntegerProperty(this, "fixedColumnsIndex", -1);
	private final StringProperty fixedColumnsFooterText = new SimpleStringProperty(this, "fixedColumnsFooterText",
			null);
	private final ObjectProperty<Node> fixedColumnsFooterGraphic = new SimpleObjectProperty<>(this,
			"fixedColumnsFooterGraphic", null);
	private final ObjectProperty<ContextMenu> tableMenuButtonContextMenu = new SimpleObjectProperty<>(this,
			"tableMenuButtonContextMenu", null);
	private final BooleanProperty footerVisible = new SimpleBooleanProperty(this, "footerVisible", false);
	private final BooleanProperty headerVisible = new SimpleBooleanProperty(this, "headerVisible", true);

	public TableViewKafx() {
		this(FXCollections.<S>observableArrayList());
	}

	@SuppressWarnings("unchecked")
	public TableViewKafx(ObservableList<S> items) {
		super(items);

		this.setPadding(new Insets(0));

		FontSizeController.bindTableColumnWidthToFontSize(this);

		this.getStyleClass().addAll("table-view-cell-highlight", "table-view-no-focus", "table-view-hide-empty");

		// this fixes cell selection (visibly) changing when showing/hiding columns
		final ChangeListener<Boolean> selectionFix = (_, _, _) -> {
			TableViewSelectionModel<S> selectionModel = this.getSelectionModel();
			if (selectionModel != null) {
				// FIXME this might not work with multiple selection enabled
				for (TablePosition<S, ?> pos : selectionModel.getSelectedCells()) {
					selectionModel.select(pos.getRow(), (TableColumn<S, ?>) pos.getTableColumn());
				}
			}
		};
		this.getColumns().addListener((ListChangeListener<TableColumn<S, ?>>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					for (TableColumn<S, ?> column : c.getAddedSubList()) {
						column.visibleProperty().addListener(selectionFix);
					}
				}
				if (c.wasRemoved()) {
					for (TableColumn<S, ?> column : c.getRemoved()) {
						column.visibleProperty().removeListener(selectionFix);
					}
				}
			}
		});

		// both necessary to clear selection correctly when losing focus
		this.focusedProperty().addListener((_, _, isFocused) -> {
			if (!isFocused && this.getEditingCell() == null) {
				this.getSelectionModel().clearSelection();
			}
		});
		this.addEventHandler(TableCellEditControl.FOCUS_LOST, _ -> {
			if (!this.isFocused()) {
				this.getSelectionModel().clearSelection();
			}
		});
	}

	public void setFooterVisible(boolean visible) {
		this.footerVisible.set(visible);
	}

	public boolean isFooterVisible() {
		return this.footerVisible.get();
	}

	/**
	 * Whether the table footer is visible (default: false).
	 * 
	 * @return footerVisible property
	 */
	public BooleanProperty footerVisibleProperty() {
		return this.footerVisible;
	}

	public void setHeaderVisible(boolean visible) {
		this.headerVisible.set(visible);
	}

	public boolean isHeaderVisible() {
		return this.headerVisible.get();
	}

	/**
	 * Whether the table header is visible (default: true).
	 * 
	 * @return headerVisible property
	 */
	public BooleanProperty headerVisibleProperty() {
		return this.headerVisible;
	}

	public void setTableMenuButtonContextMenu(ContextMenu contextMenu) {
		this.tableMenuButtonContextMenu.set(contextMenu);
	}

	public ContextMenu getTableMenuButtonContextMenu() {
		return this.tableMenuButtonContextMenu.get();
	}

	/**
	 * Whether the table header is visible (default: true).
	 * 
	 * @return headerVisible property
	 */
	public ObjectProperty<ContextMenu> tableMenuButtonContextMenuProperty() {
		return this.tableMenuButtonContextMenu;
	}

	/**
	 * 
	 * @param index of the last fixed column
	 */
	public final void setFixedColumnsIndex(int index) {
		this.fixedColumnsIndex.set(index);
	}

	public final int getFixedColumnsIndex() {
		return this.fixedColumnsIndex.get();
	}

	public final IntegerProperty fixedColumnsIndex() {
		return this.fixedColumnsIndex;
	}

	public final boolean isFixedColumn(TableColumn<?, ?> column) {
		if (this.getFixedColumnsIndex() < 0) {
			return false;
		} else {
			TableColumn<?, ?> rootColumn = column;
			while (rootColumn.getParentColumn() != null) {
				rootColumn = (TableColumn<?, ?>) rootColumn.getParentColumn();
			}
			return this.getColumns().indexOf(rootColumn) <= this.getFixedColumnsIndex();
		}
	}

	public void setFixedColumnsFooterText(String text) {
		if (this.fixedColumnsFooterText.isBound()) {
			throw new IllegalStateException("fixed columns footer text is bound and therefore cannot be set");
		} else {
			this.fixedColumnsFooterText.set(text);
		}
	}

	public String getFixedColumnsFooterText() {
		return this.fixedColumnsFooterText.get();
	}

	public StringProperty fixedColumnsFooterText() {
		return this.fixedColumnsFooterText;
	}

	public void setFixedColumnsFooterGraphic(Node graphic) {
		if (this.fixedColumnsFooterGraphic.isBound()) {
			throw new IllegalStateException("fixed columns footer graphic is bound and therefore cannot be set");
		} else {
			this.fixedColumnsFooterGraphic.set(graphic);
		}
	}

	public Node getFixedColumnsFooterGraphic() {
		return this.fixedColumnsFooterGraphic.get();
	}

	public ObjectProperty<Node> fixedColumnsFooterGraphic() {
		return this.fixedColumnsFooterGraphic;
	}

	/** {@inheritDoc} */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new TableViewKafxSkin<>(this);
	}

	private String styleSheet;

	@Override
	public String getUserAgentStylesheet() {
		if (styleSheet == null) {
			styleSheet = TableViewKafx.class.getResource("/org/openjfx/kafx/css/tableviewkafx.css").toExternalForm();
		}
		return styleSheet;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected double computePrefHeight(double width) {
		double height = 0;
		if (this.getSkin() != null) {
			TableHeaderRowKafx header = ((TableViewKafxSkin<S>) this.getSkin()).getTableHeaderRow();
			TableFooterRowKafx footer = ((TableViewKafxSkin<S>) this.getSkin()).getTableFooterRow();
			if (header != null) {
				if (header.getRootHeader() != null && !header.getRootHeader().getColumnHeaders().isEmpty()) {
					height += snapSizeY(calculateNestedColumnHeaderHeight(header.getRootHeader()))
							+ header.snappedTopInset() + header.snappedBottomInset();
				} else {
					height += snapSizeY(header.getHeight()) + header.snappedTopInset() + header.snappedBottomInset();
				}
			}
			if (footer != null) {
				height += snapSizeY(footer.getHeight()) + footer.snappedTopInset() + footer.snappedBottomInset();
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

	public double computeFixedColumnsWidth() {
		double width = 0;
		for (int i = 0; i <= this.fixedColumnsIndex.get(); i++) {
			TableColumn<S, ?> col = this.getColumns().get(i);
			if (col.isVisible()) {
				width += col.getWidth();
			}
		}
		return width;
	}

	// sortOrder: "+:1/-:2:0" means first asc column index 1, then desc sub-column
	// index 0 of column index 2
	public String getSortOrderString() {
		StringBuilder sortOrderString = new StringBuilder();
		List<TableColumn<S, ?>> sortOrder = this.getSortOrder();
		for (TableColumn<S, ?> column : sortOrder) {
			if (sortOrderString.length() > 0) {
				sortOrderString.append('/');
			}
			String tmp = "";
			TableColumn<S, ?> root = column;
			while (root.getParentColumn() != null) {
				tmp = ':' + String.valueOf(root.getParentColumn().getColumns().indexOf(root)) + tmp;
				root = (TableColumn<S, ?>) root.getParentColumn();
			}
			sortOrderString.append(column.getSortType() == SortType.ASCENDING ? '+' : '-');
			sortOrderString.append(':');
			sortOrderString.append(this.getColumns().indexOf(root));
			sortOrderString.append(tmp);
		}
		return sortOrderString.toString();
	}

	public void setSortOrderString(String sortOrder) {
		if (sortOrder.length() > 0) {
			String[] columns = sortOrder.split("/");
			for (String column : columns) {
				String[] subColumns = column.split(":");
				SortType sortType = subColumns[0].equals("+") ? SortType.ASCENDING : SortType.DESCENDING;
				TableColumn<S, ?> tableColumn = this.getColumns().get(Integer.valueOf(subColumns[1]));
				for (int i = 2; i < subColumns.length; i++) {
					tableColumn = tableColumn.getColumns().get(Integer.valueOf(subColumns[i]));
				}
				tableColumn.setSortType(sortType);
				this.getSortOrder().add(tableColumn);
			}
		}
	}

}
