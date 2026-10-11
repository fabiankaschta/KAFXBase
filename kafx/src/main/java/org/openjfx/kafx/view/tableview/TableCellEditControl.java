package org.openjfx.kafx.view.tableview;

import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.view.skin.TableViewKafxSkin;

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
	public final void startEdit() {
		super.startEdit();
		if (!isEditing()) {
			return;
		}

		this.setControlValue();
		this.setText(null);

		this.setGraphic(this.getControl());
		startEditControl();
	}

	@Override
	public final void cancelEdit() {
		if (this.canceled) {
			super.cancelEdit();
			this.setCellText();
			this.setGraphic(this.graphic);
			this.canceled = false;
		} else {
			commitEdit(getFromControl());
		}
	}

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (this.isEmpty()) {
			this.setText(null);
			this.setGraphic(null);
		} else {
			if (this.isEditing()) {
				this.setControlValue();
				this.setText(null);
				this.setGraphic(this.getControl());
			} else {
				this.setCellText();
				this.setGraphic(this.graphic);
			}
		}
		this.canceled = false;
	}

	protected abstract void setCellText();

	protected abstract void setControlValue();

	protected abstract T getFromControl();

	protected abstract void startEditControl();

	public final static EventType<Event> FOCUS_LOST = new EventType<>("FOCUS_LOST");

	private Control createControlHelper() {
		Control control = createControl();
		control.focusedProperty().addListener((_, wasFocused, _) -> {
			// if user clicks outside of table
			if (wasFocused && isEditing()) {
				commitEdit(getFromControl());
				this.getTableView().fireEvent(new Event(FOCUS_LOST));
			}
		});
		control.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getTarget() == control) {
				TablePosition<S, ?> current = getTableView().getEditingCell();
				int row = current.getRow();
				int col = getTableView().getVisibleLeafIndex(current.getTableColumn());
				// enter => confirm edit
				if (event.getCode() == KeyCode.ENTER) {
					String enterModeConfig = ConfigController.get("TABLECELL_ENTER_MODE");
					if (enterModeConfig != null) {
						event.consume();
						switch (EnterMode.valueOf(enterModeConfig)) {
						case CONFIRM:
							commitEdit(getFromControl());
							break;
						case HORIZONTAL:
							if (event.isShiftDown()) {
								editPreviousCell(row, col);
							} else {
								editNextCell(row, col);
							}
							break;
						case VERTICAL:
							if (event.isShiftDown()) {
								editAboveCell(row, col);
							} else {
								editBelowCell(row, col);
							}
							break;
						}
					}
				} else if (event.getCode() == KeyCode.ESCAPE) {
					event.consume();
					canceled = true;
					cancelEdit();
				} else if (event.getCode() == KeyCode.RIGHT
						|| (!event.isShiftDown() && event.getCode() == KeyCode.TAB)) {
					event.consume();
					editNextCell(row, col);
				} else if (event.getCode() == KeyCode.LEFT || (event.isShiftDown() && event.getCode() == KeyCode.TAB)) {
					event.consume();
					editPreviousCell(row, col);
				} else if (event.getCode() == KeyCode.UP) {
					event.consume();
					editAboveCell(row, col);
				} else if (event.getCode() == KeyCode.DOWN) {
					event.consume();
					editBelowCell(row, col);
				}
			}
		});
		return control;
	}

	private void editBelowCell(int row, int col) {
		commitEdit(getFromControl());
		getTableView().getSelectionModel().selectBelowCell();
		if (getTableView().getSkin() instanceof TableViewKafxSkin) {
			((TableViewKafxSkin<?>) getTableView().getSkin()).onSelectBelowCell();
		}
		getTableView().edit(row + 1, getTableView().getVisibleLeafColumn(col));
	}

	private void editAboveCell(int row, int col) {
		commitEdit(getFromControl());
		getTableView().getSelectionModel().selectAboveCell();
		if (getTableView().getSkin() instanceof TableViewKafxSkin) {
			((TableViewKafxSkin<?>) getTableView().getSkin()).onSelectAboveCell();
		}
		getTableView().edit(row - 1, getTableView().getVisibleLeafColumn(col));
	}

	private void editPreviousCell(int row, int col) {
		commitEdit(getFromControl());
		getTableView().getSelectionModel().selectPrevious();
		getTableView().scrollToColumn(getTableView().getVisibleLeafColumn(col - 1));
		getTableView().edit(row, getTableView().getVisibleLeafColumn(col - 1));
	}

	private void editNextCell(int row, int col) {
		commitEdit(getFromControl());
		getTableView().getSelectionModel().selectNext();
		getTableView().scrollToColumn(getTableView().getVisibleLeafColumn(col + 1));
		getTableView().edit(row, getTableView().getVisibleLeafColumn(col + 1));
	}

	protected abstract Control createControl();
}
