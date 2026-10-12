package org.openjfx.kafx.view.skin;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openjfx.kafx.view.tableview.TableViewKafx;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.skin.CellSkinBase;
import javafx.util.Callback;

public class TableRowKafxSkin<S> extends CellSkinBase<TableRow<S>> {

	private final TableViewKafx<S> tableView;
	private final TableViewKafxSkin<S> skin;

	private Reference<HashMap<TableColumnBase<S, ?>, TableCell<S, ?>>> cellsMap;

	private final List<TableCell<S, ?>> cells = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public TableRowKafxSkin(TableViewKafx<S> tableView, TableRow<S> tableRow) {
		super(tableRow);
		this.tableView = tableView;

		getSkinnable().setPickOnBounds(false);

		registerChangeListener(tableRow.itemProperty(), _ -> requestCellUpdate());
		registerChangeListener(tableRow.indexProperty(), _ -> {
			// Fix for JDK-8095357, where empty table cells were showing content, as they
			// had incorrect table cell indices (but the table row index was correct).
			// Note that we only do the update on empty cells to avoid the issue
			// noted below in requestCellUpdate().
			if (getSkinnable().isEmpty()) {
				requestCellUpdate();
			}
		});

		this.skin = (TableViewKafxSkin<S>) tableView.getSkin();
		this.skin.getSelectedCells().addListener((ListChangeListener<TablePosition<S, ?>>) _ -> updateFaintSelection());
	}

	private void updateFaintSelection() {
		List<TablePosition<S, ?>> selectedRowCells = this.skin.getSelectedCells()
				.filtered(pos -> pos.getRow() == this.getSkinnable().getIndex());
		this.getSkinnable().pseudoClassStateChanged(TableViewKafxSkin.FAINT_SELCTION, !selectedRowCells.isEmpty());
	}

	private void requestCellUpdate() {
		getSkinnable().requestLayout();

		// update the index of all children cells (JDK-8119094).
		// Note that we do this after the TableRow item has been updated,
		// rather than when the TableRow index has changed (as this will be
		// before the row has updated its item). This will result in the
		// issue highlighted in JDK-8115269, where the table cell had the correct
		// item whilst the row had the old item.
		final int newIndex = getSkinnable().getIndex();
		for (int i = 0, max = cells.size(); i < max; i++) {
			cells.get(i).updateIndex(newIndex);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutChildren(double x, final double y, final double w, final double h) {
		final ObservableList<TableColumn<S, ?>> columns = tableView.getVisibleLeafColumns();
		if (columns.isEmpty()) {
			super.layoutChildren(x, y, w, h);
			return;
		}

		final TableRowKafx<S> control = (TableRowKafx<S>) getSkinnable();
		final int index = control.getIndex();

		if (index < 0) {
			getChildren().clear();
			putCellsInCache();
			return;
		}

		getSkinnable().setVisible(true);

		double width;
		double height = h;

		final double verticalPadding = snappedTopInset() + snappedBottomInset();
		final double horizontalPadding = snappedLeftInset() + snappedRightInset();

		final double tableWidth = this.tableView.getWidth();
		final double hbarValue = this.skin.getHBar().getValue();

		double fixedColumnWidth = 0;
		List<TableCell<S, ?>> fixedCells = new ArrayList<>();

		// We compute the cells here
		putCellsInCache();

		boolean firstVisibleCell = false;
		TableCell<S, ?> lastCell = null;
		boolean needToBeShifted;
		boolean isFirstColumn = false;
		for (int indexColumn = 0; indexColumn < columns.size(); indexColumn++) {
			TableColumn<S, ?> column = columns.get(indexColumn);
			if (!column.isVisible()) {
				continue;
			}

			width = snapSizeX(column.getWidth()) - snapSizeX(horizontalPadding);

			boolean isFixed = tableView.isFixedColumn(column);
			boolean isVisible = isFixed || (x + width >= hbarValue && x <= hbarValue + tableWidth);

			if (!isVisible) {
				TableCell<S, ?> cell = getCellsMap().remove(column);
				if (cell != null) {
					getChildren().remove(cell);
				}
				if (firstVisibleCell) {
					break;
				}
				x += width;
				continue;
			}

			final TableCell<S, ?> tableCell = getCell(column);
			if (!isFirstColumn) {
				tableCell.pseudoClassStateChanged(TableViewKafxSkin.LEFT_CELL, true);
				isFirstColumn = true;
			}

			cells.add(0, tableCell);

			// In case the node was treated previously
			tableCell.setManaged(true);

			double tableCellX = 0;

			if (isFixed) {
				if (hbarValue + fixedColumnWidth > x) {
					tableCellX = Math.abs(hbarValue - x + fixedColumnWidth);
					fixedColumnWidth += width;
					fixedCells.add(tableCell);
				}
			}

			if (tableCell.getIndex() != index) {
				tableCell.updateIndex(index);
			} else {
				tableCell.updateIndex(index);
				tableCell.requestLayout();
			}
			/**
			 * Here we need to add the cells on the first position because this row may
			 * contain some deported cells from other rows in order to be on top in term of
			 * z-order. So the cell we're currently adding must not recover them. We must
			 * check that the parent is indeed the getSkinnable because of the
			 * deportedCells.
			 */
			if (!tableCell.isEditing() && tableCell.getParent() != getSkinnable()) {
				getChildren().add(0, tableCell);
			}

			// FIXME
			/**
			 * Check selection. Fixes a bug in cell selection after a column is hidden
			 */
//			if (tableView.getSelectionModel() != null && tableView.getSelectionModel().isCellSelectionEnabled()) {
//				final int ic = indexColumn;
//				boolean select = !tableView.getSelectionModel().getSelectedCells().stream()
//						.noneMatch(cell -> cell.getRow() == index && cell.getColumn() == ic);
//				tableCell.pseudoClassStateChanged(TableViewKafxSkin.SELECTED, select);
//			}

			height = snapSizeY(height) - snapSizeY(verticalPadding);

			needToBeShifted = false;
			/**
			 * If the current cell has no left border, and the previous cell had no right
			 * border. We may have the problem where there is a tiny gap between the cells
			 * when scrolling horizontally. Thus we must enlarge this cell a bit, and shift
			 * it a bit in order to mask that gap. If the cell has a border defined, the
			 * problem seems not to happen. If the cell is not added to its parent, it has
			 * no border by default so we must not check it.
			 */
			if (lastCell != null && !hasRightBorder(lastCell) && !hasLeftBorder(tableCell)) {
				tableCell.resize(width + 1, height);
				needToBeShifted = true;
			} else {
				tableCell.resize(width, height);
			}
			lastCell = tableCell;

			tableCell.relocate(snapPositionX(x + tableCellX + (needToBeShifted ? -1 : 0)), snappedTopInset());

			x += width;
		}

		// When the table is wider than the columns, the empty space to the
		// right is visible and can be part of the row selection or scrolled.
		double paneWidth = tableView.getWidth() - x - tableView.snappedLeftInset() - tableView.snappedRightInset();
		if (skin.getVBar().isVisible()) {
			paneWidth -= skin.getVBar().getWidth();
		}

		final int paneBorderInset = 1; // selection border inset in pane
		final boolean paneBorderVisible = paneWidth > paneBorderInset;
		final boolean singleColumn = columns.size() == 1;

		if (lastCell != null) {
			// In case of row selection, when the table is wider than the columns,
			// the cells don't require a right selection border: the tableRow
			// itself provides this border in case of regular rows, or the above
			// pane will provide it in case of fixed rows.
			lastCell.pseudoClassStateChanged(TableViewKafxSkin.RIGHT_CELL, !singleColumn && !paneBorderVisible);
			lastCell.pseudoClassStateChanged(TableViewKafxSkin.SINGLE_CELL, singleColumn && !paneBorderVisible);
		}

		removeDeportedCells();
		removeUselessCell(index);
	}

	private boolean hasRightBorder(TableCell<S, ?> tableCell) {
		return tableCell.getBorder() != null && !tableCell.getBorder().isEmpty()
				&& tableCell.getBorder().getStrokes().get(0).getWidths().getRight() > 0;
	}

	private boolean hasLeftBorder(TableCell<S, ?> tableCell) {
		return tableCell.getBorder() != null && !tableCell.getBorder().isEmpty()
				&& tableCell.getBorder().getStrokes().get(0).getWidths().getLeft() > 0;
	}

	/**
	 * Here we want to remove of the sceneGraph cells that are not used.
	 *
	 * Before we were removing the cells that we were getting from the cache. But
	 * that is not enough because some cells can be added somehow, and stay within
	 * the row. Since we do not often clear the children because of some
	 * deportedCell present inside, we must use that Predicate to clear all
	 * TableCell not contained in cells and with the same index. Thus we preserve
	 * the deported cell.
	 */
	@SuppressWarnings("unchecked")
	private void removeUselessCell(int index) {
		getChildren().removeIf((Node t) -> {
			if (t instanceof TableCell) {
				return !cells.contains((TableCell<S, ?>) t) && ((TableCell<S, ?>) t).getIndex() == index;
			}
			return false;
		});
	}

	private void removeDeportedCells() {
		for (Map.Entry<TableRowKafx<S>, Set<TableCell<S, ?>>> entry : skin.deportedCells.entrySet()) {
			ArrayList<TableCell<S, ?>> toRemove = new ArrayList<>();
			for (TableCell<S, ?> cell : entry.getValue()) {
				/**
				 * If that cell is mine, I can remove it because I will replace it if necessary.
				 * If that cell is mine and I'm the top row, no need to remove it because I may
				 * remove cells that were deported but are not anymore and create blank space.
				 */
				if (!cell.isEditing() && cell.getTableRow() == getSkinnable() && entry.getKey() != getSkinnable()) {
					entry.getKey().removeCell(cell);
					toRemove.add(cell);
				}
			}
			entry.getValue().removeAll(toRemove);
		}
	}

	/**
	 * Return the Cache. Here we use a WeakReference because the WeakHashMap is not
	 * working. TableCell added to it are not removed if the GC wants them. So we
	 * put the whole cache in WeakReference. In normal condition, the cache is not
	 * trashed that much and is efficient. In the case where the user scroll
	 * horizontally a lot, that cache can then be trashed in order to avoid
	 * OutOfMemoryError.
	 *
	 * @return
	 */
	private HashMap<TableColumnBase<S, ?>, TableCell<S, ?>> getCellsMap() {
		if (cellsMap == null || cellsMap.get() == null) {
			HashMap<TableColumnBase<S, ?>, TableCell<S, ?>> map = new HashMap<>();
			cellsMap = new WeakReference<>(map);
			return map;
		}
		return cellsMap.get();
	}

	/**
	 * This will put all current displayed cell into the cache.
	 */
	private void putCellsInCache() {
		for (TableCell<S, ?> cell : cells) {
			getCellsMap().put(cell.getTableColumn(), cell);
		}
		cells.clear();
	}

	/**
	 * This will retrieve a cell for the specified column. If the cell exists in the
	 * cache, it's extracted from it. Otherwise, a cell is created.
	 *
	 * @param tcb
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TableCell<S, ?> getCell(TableColumnBase<S, ?> tcb) {
		TableColumn tableColumn = (TableColumn<TableCell<S, ?>, ?>) tcb;
		TableCell<S, ?> cell;
		if (getCellsMap().containsKey(tableColumn)) {
			cell = getCellsMap().remove(tableColumn);
		} else {
			Callback cellFactory = tableColumn.getCellFactory();
			if (cellFactory == null) {
				cellFactory = TableColumn.DEFAULT_CELL_FACTORY;
			}
			cell = (TableCell<S, ?>) cellFactory.call(tableColumn);
			cell.updateTableColumn(tableColumn);
			cell.updateTableView(tableColumn.getTableView());
			cell.updateTableRow(getSkinnable());
		}

		cell.pseudoClassStateChanged(TableViewKafxSkin.LEFT_CELL, false);
		cell.pseudoClassStateChanged(TableViewKafxSkin.RIGHT_CELL, false);
		cell.pseudoClassStateChanged(TableViewKafxSkin.SINGLE_CELL, false);
		return cell;
	}

	/** {@inheritDoc} */
	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		double prefWidth = 0.0;

		final List<? extends TableColumnBase<S, ?>> visibleLeafColumns = tableView.getVisibleLeafColumns();
		for (int i = 0, max = visibleLeafColumns.size(); i < max; i++) {
			prefWidth += visibleLeafColumns.get(i).getWidth();
		}

		return prefWidth;
	}

	/** {@inheritDoc} */
	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return getSkinnable().getPrefHeight();
	}

	/** {@inheritDoc} */
	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return getSkinnable().getPrefHeight();
	}

	/** {@inheritDoc} */
	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
	}
}
