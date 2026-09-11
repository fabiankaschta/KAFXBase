package org.openjfx.kafx.view.tableview;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TableViewFullSizeFooter<T> extends VBox {

	private final TableViewFullSize<T> mainTable;
	private final List<TableColumn<T, ?>> skipColumns;
	private final HBox footer;
	private final Footer<T> footerTable;
	private final Label footerLabel;
	private final ListChangeListener<TableColumn<T, ?>> columnsChangeListener;

	public TableViewFullSizeFooter(TableViewFullSize<T> mainTable, List<TableColumn<T, ?>> skipColumns,
			String footerText, FooterData<T> footerItem) {
		this.mainTable = mainTable;
		this.skipColumns = skipColumns;
		this.footerTable = new Footer<>(footerItem);
		this.footerLabel = new Label(footerText);
		this.footerLabel.getStyleClass().add("footer-label");
		this.footerLabel.setMinWidth(USE_PREF_SIZE);
		this.footerLabel.setMaxWidth(USE_PREF_SIZE);
		List<Observable> footerLabelObservables = new ArrayList<>();
		footerLabelObservables.addAll(this.skipColumns.stream().map(c -> c.widthProperty()).toList());
		footerLabelObservables.addAll(this.skipColumns.stream().map(c -> c.visibleProperty()).toList());
		this.footerLabel.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			double width = 0;
			for (TableColumn<T, ?> fixedColumn : this.skipColumns) {
				if (fixedColumn.isVisible()) {
					width += fixedColumn.getWidth();
				}
			}
			return width;
		}, footerLabelObservables.toArray(n -> new Observable[n])));
		this.footerLabel.prefHeightProperty()
				.bind(this.footerTable.fixedCellSizeProperty().multiply(this.footerTable.getItems().size()));
		// FIXME text is a little too low compared to cell text
		this.footerLabel.setAlignment(Pos.CENTER_RIGHT);
		this.columnsChangeListener = _ -> {
			this.footerTable.getColumns().clear();
			for (TableColumn<T, ?> mainColumn : this.mainTable.getColumns()) {
				addFooterColumn(mainColumn);
			}
		};
		this.mainTable.getColumns().addListener(this.columnsChangeListener);
		for (TableColumn<T, ?> mainColumn : this.mainTable.getColumns()) {
			addFooterColumn(mainColumn);
		}

		this.footerTable.getStyleClass().add("tableview-hbar-invisible");
		
		this.footer = new HBox(0);
		this.footer.getStyleClass().add("footer");
		this.footer.getChildren().addAll(this.footerLabel, this.footerTable);
		this.footer.setMinHeight(USE_PREF_SIZE);
		this.footer.setMaxHeight(USE_PREF_SIZE);
		this.footer.prefHeightProperty().bind(this.footerTable.heightProperty());
		this.footer.prefWidthProperty().bind(this.mainTable.widthProperty());
		HBox.setHgrow(this.footerTable, Priority.ALWAYS);
		this.getChildren().addAll(this.mainTable, footer);
		VBox.setVgrow(footer, Priority.ALWAYS);
		this.footerTable.autosize();
	}

	private void addFooterColumn(TableColumn<T, ?> mainColumn) {
		if (!this.skipColumns.contains(mainColumn)) {
			mainColumn.getColumns().addListener(this.columnsChangeListener);
			if (mainColumn.getColumns().isEmpty()) {
				TableColumn<FooterData<T>, String> footerColumn = new TableColumn<>();
				footerColumn.setCellFactory(TableCellCustom.forTableColumn(Pos.CENTER));
				footerColumn.setCellValueFactory(cell -> {
					return cell.getValue().getDataForColumn(mainColumn);
				});
				footerColumn.prefWidthProperty().bind(mainColumn.widthProperty());
				footerColumn.visibleProperty().bind(mainColumn.visibleProperty());
				this.footerTable.getColumns().add(footerColumn);
			} else {
				for (TableColumn<T, ?> subColumn : mainColumn.getColumns()) {
					addFooterColumn(subColumn);
				}
			}
		}
	}

}
