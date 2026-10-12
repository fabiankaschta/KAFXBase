package org.openjfx.kafx.view.skin;

import org.openjfx.kafx.view.tableview.TableViewKafx;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.skin.VirtualFlow;

final class TableViewKafxVirtualFlow<T extends IndexedCell<?>> extends VirtualFlow<T> {

	private final TableViewKafx<?> tableView;

	public TableViewKafxVirtualFlow(TableViewKafxSkin<?> skin) {
		super();
		this.tableView = (TableViewKafx<?>) skin.getSkinnable();
	}

	protected ScrollBar getVerticalBar() {
		return getVbar();
	}

	protected ScrollBar getHorizontalBar() {
		return getHbar();
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		// hbar should not span over fixed columns
		ScrollBar vbar = getVbar();
		ScrollBar hbar = getHbar();
		final double viewportBreadth = getWidth() - (vbar.isVisible() ? vbar.getWidth() : 0);
		final double viewportLength = getViewportLength();
		if (hbar.isVisible() && this.tableView.getFixedColumnsIndex() != -1) {
			double fixedColumnWidth = 0;
			for (int i = 0; i <= this.tableView.getFixedColumnsIndex(); i++) {
				TableColumn<?, ?> col = this.tableView.getColumns().get(i);
				if (col.isVisible()) {
					fixedColumnWidth += col.getWidth();
				}
			}
			final double height = hbar.prefHeight(viewportBreadth);
			final double width = viewportBreadth - fixedColumnWidth;
			hbar.resizeRelocate(fixedColumnWidth, viewportLength, width, height);
		}
	}
}
