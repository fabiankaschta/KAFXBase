package org.openjfx.kafx.view.treeview;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class TreeCellCustomNodeAddRemove<T> extends TreeCell<T> {

	private String addSymbol = "+";
	private String removeSymbol = "-";
	private final Function<T, Node> converter;
	private final Consumer<TreeItem<T>> addHandler;
	private final Consumer<TreeItem<T>> removeHandler;

	public TreeCellCustomNodeAddRemove(Function<T, Node> converter, Consumer<TreeItem<T>> addHandler,
			Consumer<TreeItem<T>> removeHandler) {
		this.addHandler = addHandler;
		this.removeHandler = removeHandler;
		this.converter = converter;

		this.disclosureNodeProperty().addListener((_, oldValue, newValue) -> {
			if (oldValue instanceof StackPane) {
				final StackPane pane = (StackPane) oldValue;
				pane.translateYProperty().unbind();
			}
			if (newValue instanceof StackPane) {
				final StackPane pane = (StackPane) newValue;
				// these numbers work, for whatsoever reason
				pane.translateYProperty().bind(Bindings.createDoubleBinding(
						() -> getHeight() / 2.0 - pane.getHeight() / 1.5, heightProperty(), pane.heightProperty()));
			}
		});

		this.itemProperty().subscribe(item -> {
			if (item == null) {
				setGraphic(null);
			} else {
				BorderPane graphic = new BorderPane();
				Node node = this.converter.apply(item);
				ButtonBar buttons = new ButtonBar();
				Button addButton = new Button(addSymbol);
				addButton.setPrefWidth(30);
				buttons.getButtons().add(addButton);
				Button removeButton = new Button(removeSymbol);
				removeButton.setPrefWidth(30);
				buttons.getButtons().add(removeButton);
				// disable remove root
				removeButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
					return getTreeView().getRoot() == this.getTreeItem();
				}, getTreeView().rootProperty()));
				buttons.setButtonMinWidth(USE_COMPUTED_SIZE);
				graphic.setCenter(node);
				BorderPane.setAlignment(node, Pos.CENTER_LEFT);
				graphic.setRight(buttons);

				addButton.setOnAction(_ -> this.addHandler.accept(getTreeItem()));
				removeButton.setOnAction(_ -> this.removeHandler.accept(getTreeItem()));
				setGraphic(graphic);
			}
		});
	}

	public void setAddText(String addSymbol) {
		this.addSymbol = addSymbol;
	}

	public void setRemoveText(String removeSymbol) {
		this.removeSymbol = removeSymbol;
	}

}
