package kafx.view.pane;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import kafx.controller.Controller;

public abstract class AddTabPane extends CustomDragTabPane {

	private final AddTab addTab;

	private class AddTab extends Tab {
		private AddTab() {
			Label label = new Label("+");
			Controller.fontSizeProperty()
					.subscribe(fontSize -> label.setStyle("-fx-font-size: " + (fontSize.doubleValue() * 1.5)));
			setGraphic(label);
			setClosable(false);
			label.parentProperty().addListener(_ -> {
				getTabContainer(this).addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
					createNewTab();
					// consume, so that addTab does not become selected
					e.consume();
				});
			});
		}
	}

	protected AddTabPane() {
		this.addTab = new AddTab();
		super.addFixedTab(this.addTab);
		// fixes rare possibility to select add tab instead of adding a tab
		this.selectionModelProperty().subscribe(model -> model.selectedItemProperty().subscribe((oldTab, newTab) -> {
			if (oldTab != null && newTab instanceof AddTab) {
				model.select(oldTab);
			}
		}));
	}

	protected void setAddTabContent(Node content) {
		this.addTab.setContent(content);
	}

	@Override
	public void addFixedTab(Tab tab) {
		super.addFixedTab(this.getTabs().size() - 1, tab);
		getSelectionModel().select(tab);
	}

	protected void addTab(Tab tab) {
		getTabs().add(this.getTabs().size() - 1, tab);
		getSelectionModel().select(tab);
	}

	protected abstract boolean createNewTab();
}
