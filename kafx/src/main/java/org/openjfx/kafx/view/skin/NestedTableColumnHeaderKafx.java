package org.openjfx.kafx.view.skin;

import org.openjfx.kafx.view.tableview.TableViewKafx;

import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.shape.Rectangle;

public class NestedTableColumnHeaderKafx extends NestedTableColumnHeader {

	protected TableViewKafxSkin<?> skin;
	protected TableViewKafx<?> tableView;

	public NestedTableColumnHeaderKafx(TableColumnBase<?, ?> tc) {
		super(tc);
	}

	protected void init() {
		this.skin = (TableViewKafxSkin<?>) getTableSkin();
		this.tableView = (TableViewKafx<?>) skin.getSkinnable();
	}

	/** {@inheritDoc} */
	@Override
	protected TableColumnHeader createTableColumnHeader(@SuppressWarnings("rawtypes") final TableColumnBase col) {
		return col == null || col.getColumns().isEmpty() || col == getTableColumn() ? new TableColumnHeader(col)
				: new NestedTableColumnHeaderKafx(col);
	}

	// from NestedTableColumnHeader
	private static final int DRAG_RECT_WIDTH = 4;
	private static final String TABLE_COLUMN_KEY = "TableColumn";

	/** {@inheritDoc} */
	@Override
	public void layoutChildren() {
		if (skin == null) {
			init();
		}
		double w = getWidth() - snappedLeftInset() - snappedRightInset();
		double h = getHeight() - snappedTopInset() - snappedBottomInset();

		int labelHeight = 0;

		TableColumnHeader label = (TableColumnHeader) getChildren().filtered(n -> n instanceof TableColumnHeader)
				.getFirst();

		if (label.isVisible() && getTableColumn() != null) {
			labelHeight = (int) label.prefHeight(-1);
			// label gets to span whole width and sits at top
			label.resize(w, labelHeight);
			label.relocate(snappedLeftInset(), snappedTopInset());
		}

		// children columns need to share the total available width
		double x = snappedLeftInset();
		final double height = snapSizeY(h - labelHeight);
		final double hbarValue = this.skin.getHBar().getValue();
		int firstFixedIndex = getChildren().size();
		for (int i = 0, max = getColumnHeaders().size(); i < max; i++) {
			TableColumnHeader n = getColumnHeaders().get(i);
			if (!n.isVisible())
				continue;

			double prefWidth = n.prefWidth(height);

			TableColumn<?, ?> column = (TableColumn<?, ?>) n.getTableColumn();

			// position drag overlay to intercept column resize requests
			FilteredList<Node> dragRectCandidates = getChildren()
					.filtered(c -> c instanceof Rectangle && c.getProperties().get(TABLE_COLUMN_KEY) == column);
			Rectangle dragRect = dragRectCandidates.isEmpty() ? null : (Rectangle) dragRectCandidates.getFirst();

			double xPos;

			if (this.tableView.isFixedColumn(column)) {
				// remember lowest index of fixed column (most back position in z order)
				firstFixedIndex = Math.min(firstFixedIndex, getChildren().indexOf(n));
				xPos = snapPositionX(x + Math.abs(column.getParentColumn() != null ? 0 : hbarValue));

				// position the column header in the default location...
				n.resize(prefWidth, height);
				n.relocate(xPos, labelHeight + snappedTopInset());

				// shuffle along the x-axis appropriately
				x += prefWidth;

				if (dragRect != null) {
					dragRect.setHeight(n.getHeight());
					dragRect.relocate(xPos + prefWidth - DRAG_RECT_WIDTH / 2, snappedTopInset() + labelHeight);
				}
			} else {
				xPos = snapPositionX(x);

				// position the column header in the default location...
				n.resize(prefWidth, height);
				n.relocate(xPos, labelHeight + snappedTopInset());
				// check if is in front of most back fixed column (in z order)
				if(getChildren().indexOf(n) > firstFixedIndex) {
					n.toBack();
				}

				// shuffle along the x-axis appropriately
				x += prefWidth;

				if (dragRect != null) {
					dragRect.setHeight(n.getHeight());
					dragRect.relocate(xPos + prefWidth - DRAG_RECT_WIDTH / 2, snappedTopInset() + labelHeight);
				}
			}
		}
		// label should always have most back position (z order)
		if(getChildren().indexOf(label) > 0) {
			label.toBack();
		}
	}

}
