package org.openjfx.kafx.view.skin;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualFlow;

final class TableViewKafxVirtualFlow<T extends IndexedCell<?>> extends VirtualFlow<T> {

	private final TableViewKafxSkin<?> skin;

	public TableViewKafxVirtualFlow(TableViewKafxSkin<?> skin) {
		super();
		this.skin = skin;
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
		// vbar should end before footer
		ScrollBar vbar = getVbar();
		ScrollBar hbar = getHbar();
		if (vbar.isVisible() && this.skin.isColumnFooterVisible() && this.skin.getTableFooterRow() != null) {
			final double viewportBreadth = getHeight() - (hbar.isVisible() ? hbar.getHeight() : 0);
			final double viewportLength = getViewportLength();
			double prefWidth = vbar.prefWidth(viewportBreadth);
			double height = viewportLength;
			height -= this.skin.getTableFooterRow().getHeight();
			vbar.resize(prefWidth, snapSizeY(height));
		}
	}
}
