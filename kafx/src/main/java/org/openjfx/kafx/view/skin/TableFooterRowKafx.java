package org.openjfx.kafx.view.skin;

public class TableFooterRowKafx extends TableHeaderRowKafx {

	public TableFooterRowKafx(TableViewKafxSkin<?> skin) {
		super(skin);
	}

	public void init() {
		super.init();
		this.visibleProperty().unbind();
		this.visibleProperty().bind(getTableView().footerVisibleProperty());
	}

	/** {@inheritDoc} */
	@Override
	protected NestedTableColumnFooterKafx createRootHeader() {
		return new NestedTableColumnFooterKafx(null);
	}

	/** {@inheritDoc} */
	@Override
	protected double computePrefHeight(double width) {
		if (!this.getSkin().isColumnFooterVisible()) {
			return 0.0;
		} else {
			return snappedTopInset() + getRootHeader().prefHeight(width) + snappedBottomInset();
		}
	}
}
