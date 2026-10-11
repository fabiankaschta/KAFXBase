package org.openjfx.kafx.view.skin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openjfx.kafx.view.tableview.TableViewKafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.MouseEvent;

public class TableViewKafxSkin<S> extends TableViewSkin<S> {

	public static final String FIXED_STYLE = "fixed";
	public static final String SELECTED_STYLE = "selected";
	public static final String NO_SELECTION = "no-selection";
	public static final String LEAF_STYLE = "leaf-header";
	public static final PseudoClass SELECTED = PseudoClass.getPseudoClass("SELECTED_STYLE");
	public static final PseudoClass LEFT_CELL = PseudoClass.getPseudoClass("left");
	public static final PseudoClass RIGHT_CELL = PseudoClass.getPseudoClass("right");
	public static final PseudoClass SINGLE_CELL = PseudoClass.getPseudoClass("single");
	public static final PseudoClass FIXED_CELL = PseudoClass.getPseudoClass("FIXED_STYLE");

	private final TableViewKafx<S> tableView;
	private final TableFooterRowKafx tableFooterRow;
	private final VirtualFlow<TableRow<S>> flow;

	/**
	 * When we add some tableCell to some topRow in order for them to be on top in
	 * term of z-order. We may end up with the situation where the row that put the
	 * cell is not in the ViewPort anymore. For example when a fixedRow has taken
	 * over the real row when scrolling down. Then, the tableCell added is still
	 * hanging out in the topRow. That tableCell has no clue that its "creator" has
	 * been destroyed or re-used since that tableCell was not technically belonging
	 * to its "creator". Therefore, we need to track those cells in order to remove
	 * them each time.
	 */
	final Map<TableRowKafx<S>, Set<TableCell<S, ?>>> deportedCells = new HashMap<>();

	private final ObservableList<Integer> selectedColumns = FXCollections.observableArrayList();

	public TableViewKafxSkin(TableViewKafx<S> tableView) {
		super(tableView);
		this.tableView = tableView;
		this.tableFooterRow = createTableFooterRow();
		this.flow = getVirtualFlow();
		// override this to use TableRowKafx always
		this.flow.setCellFactory(_ -> this.createCell());
		this.getChildren().add(tableFooterRow);

		((TableViewKafxVirtualFlow<?>) flow).getHorizontalBar().valueProperty().addListener(_ -> {
			tableFooterRow.updateScrollX();
		});

		this.tableView.fixedColumnsIndex().addListener(_ -> getFlow().requestLayout());

		getTableHeaderRow().init();
		getTableFooterRow().init();

		@SuppressWarnings("rawtypes")
		ListChangeListener<TablePosition> selectionListener = _ -> {
			this.selectedColumns.clear();
			tableView.getSelectionModel().getSelectedCells().forEach(pos -> this.selectedColumns.add(pos.getColumn()));
		};

		tableView.selectionModelProperty().subscribe(model -> {
			if (model != null) {
				model.cellSelectionEnabledProperty().subscribe(enabled -> {
					if (enabled) {
						model.getSelectedCells().addListener(selectionListener);
						selectionListener.onChanged(null);
					} else {
						model.getSelectedCells().removeListener(selectionListener);
					}
				});
			}
		});

		EventHandler<MouseEvent> ml = _ -> {
			// JDK-8114594: cancel editing on scroll. This is a bit extreme
			// (we are cancelling editing on touching the scrollbars).
			// This can be improved at a later date.
			if (tableView.getEditingCell() != null) {
				tableView.edit(-1, null);
			}

			// This ensures that the table maintains the focus, even when the vbar
			// and hbar controls inside the flow are clicked. Without this, the
			// focus border will not be shown when the user interacts with the
			// scrollbars, and more importantly, keyboard navigation won't be
			// available to the user.
			tableView.requestFocus();
		};

		getFlow().getVerticalBar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);
		getFlow().getHorizontalBar().addEventFilter(MouseEvent.MOUSE_PRESSED, ml);

		registerChangeListener(tableView.fixedCellSizeProperty(), _ -> {
			tableView.refresh();
		});
		registerChangeListener(tableView.fixedColumnsIndex(), _ -> {
			tableView.refresh();
		});
	}

	public ObservableList<Integer> getSelectedColumns() {
		return selectedColumns;
	}

	// hide the super method here
	private TableRow<S> createCell() {
		TableRow<S> cell;
		if (tableView.getRowFactory() != null) {
			cell = new TableRowKafx<>(tableView, tableView.getRowFactory().call(tableView));
		} else {
			cell = new TableRowKafx<>(tableView);
		}
		cell.updateTableView(tableView);
		return cell;
	}

	public ScrollBar getHBar() {
		if (getFlow() != null) {
			return getFlow().getHorizontalBar();
		}
		return null;
	}

	public ScrollBar getVBar() {
		if (getFlow() != null) {
			return getFlow().getVerticalBar();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutChildren(double x, double y, double w, final double h) {
		super.layoutChildren(x, y, w, h);

		// position the table footer
		if (isColumnFooterVisible()) {
			final double baselineOffset = getSkinnable().getLayoutBounds().getHeight() / 2;
			double hBarHeight = getFlow().getHorizontalBar().isVisible() ? getFlow().getHorizontalBar().getHeight() : 0;

			double tableFooterRowHeight = getTableFooterRow().prefHeight(-1);
			layoutInArea(getTableFooterRow(), x, y + h - tableFooterRowHeight - hBarHeight, w, tableFooterRowHeight,
					baselineOffset, HPos.CENTER, VPos.CENTER);
		}
	}

	private TableViewKafx<S> getTableView() {
		return (TableViewKafx<S>) getSkinnable();
	}

	protected boolean isColumnHeaderVisible() {
		return getTableView().isHeaderVisible();
	}

	protected boolean isColumnFooterVisible() {
		return getTableView().isFooterVisible();
	}

	protected int getViewColumn(int modelColumn) {
		return (int) tableView.getColumns().stream().limit(modelColumn > -1 ? modelColumn : 0)
				.filter(TableColumn::isVisible).count();
	}

	/** {@inheritDoc} */
	@Override
	protected void onFocusAboveCell() {
		super.onFocusAboveCell();
		scrollHorizontally();
	}

	/** {@inheritDoc} */
	@Override
	protected void onFocusBelowCell() {
		super.onFocusBelowCell();
		scrollHorizontally();
	}

	/** {@inheritDoc} */
	@Override
	public void onSelectAboveCell() {
		super.onSelectAboveCell();
		scrollHorizontally();
	}

	/** {@inheritDoc} */
	@Override
	public void onSelectBelowCell() {
		super.onSelectBelowCell();
		scrollHorizontally();
	}

	/** {@inheritDoc} */
	@Override
	protected VirtualFlow<TableRow<S>> createVirtualFlow() {
		return new TableViewKafxVirtualFlow<TableRow<S>>(this);
	}

	@Override
	protected TableHeaderRow createTableHeaderRow() {
		return new TableHeaderRowKafx(this);
	}

	protected TableFooterRowKafx createTableFooterRow() {
		return new TableFooterRowKafx(this);
	}

	public TableHeaderRowKafx getTableHeaderRow() {
		return (TableHeaderRowKafx) super.getTableHeaderRow();
	}

	public TableFooterRowKafx getTableFooterRow() {
		return this.tableFooterRow;
	}

	/** {@inheritDoc} */
	@Override
	protected void scrollHorizontally(TableColumn<S, ?> col) {
		if (col == null || !col.isVisible()) {
			return;
		}

		final double tableWidth = tableView.getWidth() - snappedLeftInset() - snappedRightInset();
		final double fixedColumnWidth = tableView.computeFixedColumnsWidth();
		final double pos = getFlow().getHorizontalBar().getValue();
		final int index = tableView.getVisibleLeafColumns().indexOf(col);

		double start = 0;
		for (int columnIndex = 0; columnIndex < index; columnIndex++) {
			if (tableView.getVisibleLeafColumns().get(columnIndex).isVisible()) {
				TableColumn<S, ?> column = (TableColumn<S, ?>) tableView.getVisibleLeafColumns().get(columnIndex);
				start += column.getWidth();
			}
		}
		final double end = start + col.getWidth();

		if (start >= fixedColumnWidth && start < pos + fixedColumnWidth || end > tableWidth) {
			getFlow().getHorizontalBar().setValue(start - fixedColumnWidth < 0 ? start : start - fixedColumnWidth);
		}
	}

	private final TableViewKafxVirtualFlow<?> getFlow() {
		return (TableViewKafxVirtualFlow<?>) flow;
	}

	/**
	 * If the scene is not yet instantiated, we need to wait otherwise the
	 * VirtualFlow will not shift the cells properly.
	 *
	 * @param value the horizontal value
	 */
	public void setHbarValue(double value) {
		setHbarValue(value, 0);
	}

	private void setHbarValue(double value, int count) {
		if (count > 5) {
			return;
		}
		final int newCount = count + 1;
		if (flow.getScene() == null) {
			Platform.runLater(() -> {
				setHbarValue(value, newCount);
			});
			return;
		}
		getHBar().setValue(value);
	}

	// same as super, but excludes footer from calculation
	// without this, vertical resizing does not work
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset,
			double leftInset) {
		double minY = 0;
		double maxY = 0;
		boolean firstManagedChild = true;
		for (int i = 0; i < getChildren().size(); i++) {
			Node node = getChildren().get(i);
			if (node.isManaged()) {
				if (!(node instanceof TableFooterRowKafx)) {
					final double y = node.getLayoutBounds().getMinY() + node.getLayoutY();
					if (!firstManagedChild) { // branch prediction favors most often used condition
						minY = Math.min(minY, y);
						maxY = Math.max(maxY, y + node.minHeight(-1));
					} else {
						minY = y;
						maxY = y + node.minHeight(-1);
						firstManagedChild = false;
					}
				}
			}
		}
		double minHeight = maxY - minY;
		return topInset + minHeight + bottomInset;
	}

}
