package org.openjfx.kafx.view.skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openjfx.kafx.view.tableview.TableViewKafx;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class TableHeaderRowKafx extends TableHeaderRow {

	private final TableViewKafxSkin<?> skin;
	private final TableViewKafx<?> table;

	private final List<TableColumnHeader> visibleLeafHeaders = new ArrayList<>();

	public TableHeaderRowKafx(final TableViewKafxSkin<?> skin) {
		super(skin);
		this.skin = skin;
		this.table = (TableViewKafx<?>) skin.getSkinnable();
		this.buildVisibleLeafColumnHeaders(getRootHeader().getColumnHeaders());
	}

	public void init() {
		this.visibleProperty().bind(getTableView().headerVisibleProperty());
		updateColumnHeaderVisibility(this.skin.isColumnHeaderVisible());

		this.getSkin().getSelectedCells()
				.addListener((ListChangeListener<TablePosition<?, ?>>) _ -> updateHighlightSelection());

		Platform.runLater(() -> {
			updateVisibleLeafColumnHeaders();
		});

		getRootHeader().getColumnHeaders().addListener((Observable _) -> {
			updateVisibleLeafColumnHeaders();
		});

		this.getTableView().getVisibleLeafColumns()
				.addListener((ListChangeListener<TableColumn<?, ?>>) _ -> updateVisibleLeafColumnHeaders());
		this.getTableView().addEventHandler(KeyEvent.KEY_PRESSED, cancelDrag);
	}

	protected TableViewKafx<?> getTableView() {
		return this.table;
	}

	protected TableViewKafxSkin<?> getSkin() {
		return this.skin;
	}

	/** {@inheritDoc} */
	@Override
	protected void showColumnMenu(MouseEvent mouseEvent) {
		ContextMenu menu = table.getTableMenuButtonContextMenu();
		if (menu != null) {
			menu.show((Node) mouseEvent.getSource(), Side.BOTTOM, 0, 0);
			mouseEvent.consume();
		} else {
			super.showColumnMenu(mouseEvent);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void updateScrollX() {
		super.updateScrollX();
		((NestedTableColumnHeaderKafx) getRootHeader()).layoutChildren();
	}

	/** {@inheritDoc} */
	@Override
	protected NestedTableColumnHeader createRootHeader() {
		return new NestedTableColumnHeaderKafx(null);
	}

	private void updateVisibleLeafColumnHeaders() {
		this.visibleLeafHeaders.clear();
		this.buildVisibleLeafColumnHeaders(getRootHeader().getColumnHeaders());
		this.updateHighlightSelection();
	}

	private void removeStyleHeader(TableColumnHeader header, String style) {
		if (header instanceof NestedTableColumnHeader) {
			((NestedTableColumnHeader) header).getChildrenUnmodifiable().stream()
					.filter(TableColumnHeader.class::isInstance).map(TableColumnHeader.class::cast)
					.forEach(t -> removeStyleHeader(t, style));
		} else {
			if (header.getStyleClass().contains(style)) {
				header.getStyleClass().remove(style);
			}
		}
	}

	@SuppressWarnings("unused")
	private void addStyleHeader(TableColumnHeader header, String style) {
		if (header instanceof NestedTableColumnHeader) {
			((NestedTableColumnHeader) header).getChildrenUnmodifiable().stream()
					.filter(TableColumnHeader.class::isInstance).map(TableColumnHeader.class::cast)
					.forEach(t -> addStyleHeader(t, style));
		} else {
			if (!header.getStyleClass().contains(style)) {
				header.getStyleClass().add(style);
			}
		}
	}

	private void updateHighlightSelection() {
		removeStyleHeader(getRootHeader(), TableViewKafxSkin.SELECTED_STYLE);

		this.getSkin().getSelectedCells().stream().map(pos -> pos.getColumn()).forEach(i -> {
			if (this.visibleLeafHeaders.size() > i) {
				TableColumnHeader header = this.visibleLeafHeaders.get(i);
				if (!header.getStyleClass().contains(TableViewKafxSkin.SELECTED_STYLE)
						&& !header.getStyleClass().contains(TableViewKafxSkin.NO_SELECTION)) {
					header.getStyleClass().add(TableViewKafxSkin.SELECTED_STYLE);
				}
			}
		});
	}

	private void buildVisibleLeafColumnHeaders(List<TableColumnHeader> colHeaders) {
		colHeaders.stream().filter(Objects::nonNull).forEachOrdered(c -> {
			if (c instanceof NestedTableColumnHeader) {
				buildVisibleLeafColumnHeaders(((NestedTableColumnHeader) c).getColumnHeaders());
			} else if (c.isVisible()) {
				this.visibleLeafHeaders.add(c);
			}
		});
	}

	private void updateColumnHeaderVisibility(boolean visible) {
		setManaged(visible);
		if (!visible) {
			getStyleClass().add("invisible");
		} else {
			getStyleClass().remove("invisible");
			requestLayout();
			updateHighlightSelection();
		}
	}

	/**
	 * When a dragging event to reorder columns starts, reordering is set to true.
	 * If ESC is pressed, the event can be cancelled, by setting reordering to false
	 * and setting the column to not reorderable.
	 */
	private final EventHandler<KeyEvent> cancelDrag = e -> {
		if (isReordering() && e.getCode() == KeyCode.ESCAPE) {
			setReordering(false);
			TableColumnHeader columnHeader = getReorderingRegion();
			if (columnHeader != null) {
				columnHeader.getTableColumn().setReorderable(false);
			}
		}
	};

	/** {@inheritDoc} */
	@Override
	protected double computePrefHeight(double width) {
		if (!this.getSkin().isColumnHeaderVisible()) {
			return 0.0;
		} else {
			return snappedTopInset() + getRootHeader().prefHeight(width) + snappedBottomInset();
		}
	}
}
