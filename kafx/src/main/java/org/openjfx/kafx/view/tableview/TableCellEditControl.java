package org.openjfx.kafx.view.tableview;

import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.skin.TableView3Skin;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class TableCellEditControl<S, T> extends TableCell<S, T> {

	public enum EnterMode {
		CONFIRM, HORIZONTAL, VERTICAL;

		@Override
		public String toString() {
			switch (this) {
			case CONFIRM:
				return TranslationController.translate("enterMode_confirm");
			case HORIZONTAL:
				return TranslationController.translate("enterMode_horizontal");
			case VERTICAL:
				return TranslationController.translate("enterMode_vertical");
			default:
				return super.toString();
			}
		}
	}

	public final static EventType<Event> FOCUS_LOST = new EventType<>("FOCUS_LOST");

	protected Node graphic;
	private Control control;
	private boolean canceled;

	protected TableCellEditControl() {
		this(null);
	}

	protected TableCellEditControl(Node graphic) {
		this.setAlignment(Pos.CENTER_LEFT);
		this.graphic = graphic;
		this.setContentDisplay(ContentDisplay.RIGHT);
		this.setGraphicTextGap(0);
	}

	protected final Control getControl() {
		if (this.control == null) {
			this.control = createControlHelper();
		}
		return this.control;
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (this.isEmpty()) {
			this.setText(null);
			this.setGraphic(null);
		} else {
			updateGraphic(item);
			if (this.isEditing()) {
				if (this.control == null) {
					this.control = createControlHelper();
				}
				this.setControlValue();
				this.setText(null);
				this.setGraphic(this.control);
			} else {
				this.setCellText();
				this.setGraphic(this.graphic);
			}
		}
		this.canceled = false;
	}

	protected void updateGraphic(T item) {
	}

	@Override
	public final void startEdit() {
		super.startEdit();
		if (!isEditing()) {
			return;
		}

		if (this.control == null) {
			this.control = createControlHelper();
		}
		this.setControlValue();
		this.setText(null);

		this.setGraphic(this.control);
		startEditControl();
	}

	@Override
	public final void cancelEdit() {
		if (this.canceled) {
			super.cancelEdit();
			cancelEdit(null);
		} else {
			commitEdit(getFromControl());
		}
	}

	protected abstract void setCellText();

	protected abstract void setControlValue();

	protected abstract T getFromControl();

	protected void startEditControl() {
	}

	private void cancelEdit(Node graphic) {
		this.setCellText();
		this.setGraphic(graphic);
	}

	private Control createControlHelper() {
		Control control = createControl();
		control.focusedProperty().addListener((_, wasFocused, _) -> {
			// if user clicks outside of table
			if (wasFocused && isEditing()) {
				commitEdit(getFromControl());
			}
		});
		control.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getTarget() == control) {
				TablePosition<S, ?> current = getTableView().getEditingCell();
				int row = current.getRow();
				int col = getTableView().getVisibleLeafIndex(current.getTableColumn());
				// enter => confirm edit
				if (event.getCode() == KeyCode.ENTER) {
					commitEdit(getFromControl());
					String enterModeConfig = ConfigController.get("TABLECELL_ENTER_MODE");
					if (enterModeConfig != null) {
						switch (EnterMode.valueOf(enterModeConfig)) {
						case CONFIRM:
							break;
						case HORIZONTAL:
							if (event.isShiftDown()) {
								getTableView().getSelectionModel().selectPrevious();
								getTableView().scrollToColumn(getTableView().getVisibleLeafColumn(col - 1));
								getTableView().edit(row, getTableView().getVisibleLeafColumn(col - 1));
							} else {
								getTableView().getSelectionModel().selectNext();
								getTableView().scrollToColumn(getTableView().getVisibleLeafColumn(col + 1));
								getTableView().edit(row, getTableView().getVisibleLeafColumn(col + 1));
							}
							break;
						case VERTICAL:
							if (event.isShiftDown()) {
								getTableView().getSelectionModel().selectAboveCell();
								if (getTableView().getSkin() instanceof TableView3Skin) {
									((TableView3Skin<?>) getTableView().getSkin()).onSelectAboveCell();
								}
								getTableView().edit(row - 1, getTableView().getVisibleLeafColumn(col));
							} else {
								getTableView().getSelectionModel().selectBelowCell();
								if (getTableView().getSkin() instanceof TableView3Skin) {
									((TableView3Skin<?>) getTableView().getSkin()).onSelectBelowCell();
								}
								getTableView().edit(row + 1, getTableView().getVisibleLeafColumn(col));
							}
							break;
						}
					}
					event.consume();
				} else if (event.getCode() == KeyCode.ESCAPE) {
					canceled = true;
					cancelEdit();
					event.consume();
				} else if (event.getCode() == KeyCode.RIGHT
						|| (!event.isShiftDown() && event.getCode() == KeyCode.TAB)) {
					commitEdit(getFromControl());
					getTableView().getSelectionModel().selectNext();
					getTableView().scrollToColumn(getTableView().getVisibleLeafColumn(col + 1));
					getTableView().edit(row, getTableView().getVisibleLeafColumn(col + 1));
					event.consume();
				} else if (event.getCode() == KeyCode.LEFT || (event.isShiftDown() && event.getCode() == KeyCode.TAB)) {
					commitEdit(getFromControl());
					getTableView().getSelectionModel().selectPrevious();
					getTableView().scrollToColumn(getTableView().getVisibleLeafColumn(col - 1));
					getTableView().edit(row, getTableView().getVisibleLeafColumn(col - 1));
					event.consume();
				} else if (event.getCode() == KeyCode.UP) {
					commitEdit(getFromControl());
					getTableView().getSelectionModel().selectAboveCell();
					if (getTableView().getSkin() instanceof TableView3Skin) {
						((TableView3Skin<?>) getTableView().getSkin()).onSelectAboveCell();
					}
					getTableView().edit(row - 1, getTableView().getVisibleLeafColumn(col));
					event.consume();
				} else if (event.getCode() == KeyCode.DOWN) {
					commitEdit(getFromControl());
					getTableView().getSelectionModel().selectBelowCell();
					if (getTableView().getSkin() instanceof TableView3Skin) {
						((TableView3Skin<?>) getTableView().getSkin()).onSelectBelowCell();
					}
					getTableView().edit(row + 1, getTableView().getVisibleLeafColumn(col));
					event.consume();
				}
			}
		});
		return control;
	}

	protected abstract Control createControl();
}
