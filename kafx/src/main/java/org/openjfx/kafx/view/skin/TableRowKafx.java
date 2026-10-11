package org.openjfx.kafx.view.skin;

import org.openjfx.kafx.view.tableview.TableViewKafx;

import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;

public class TableRowKafx<S> extends TableRow<S> {

	private final TableViewKafx<S> tableView;

	public TableRowKafx(TableViewKafx<S> tableView, TableRow<S> row) {
		// FIXME
		this(tableView);
	}

	public TableRowKafx(TableViewKafx<S> tableView) {
		super();
		this.tableView = tableView;
	}
	
    void addCell(TableCell<S, ?> cell) {
        getChildren().add(cell);
    }

    void removeCell(TableCell<S, ?> gc) {
        getChildren().remove(gc);
    }

	/** {@inheritDoc} */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new TableRowKafxSkin<>(tableView, this);
	}
}
