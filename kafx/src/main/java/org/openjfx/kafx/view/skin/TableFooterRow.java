// NEW CLASS
package org.openjfx.kafx.view.skin;

import javafx.scene.control.skin.NestedTableColumnHeader;

public class TableFooterRow extends TableHeaderRow3 {

	public TableFooterRow(TableView3Skin<?> skin) {
		super(skin);
	}

    /** {@inheritDoc} */
    @Override protected NestedTableColumnHeader createRootHeader() {
        rootHeader2 = new NestedTableColumnFooter(null);
        return rootHeader2;
    }
}
