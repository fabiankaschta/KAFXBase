package org.openjfx.kafx.view.tableview;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;

public class TableCellEditDatePicker<S> extends TableCellEditControl<S, LocalDate> {

	public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn() {
		return _ -> new TableCellEditDatePicker<>(new LocalDateStringConverter());
	}

	public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(
			final StringConverter<LocalDate> converter) {
		return _ -> new TableCellEditDatePicker<>(converter);
	}

	public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(final Pos alignment) {
		return _ -> new TableCellEditDatePicker<>(new LocalDateStringConverter(), alignment);
	}

	public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(
			final StringConverter<LocalDate> converter, final Pos alignment) {
		return _ -> new TableCellEditDatePicker<>(converter, alignment);
	}

	public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(
			final boolean allowNull) {
		return _ -> new TableCellEditDatePicker<>(new LocalDateStringConverter(), allowNull);
	}

	public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(
			final StringConverter<LocalDate> converter, final boolean allowNull) {
		return _ -> new TableCellEditDatePicker<>(converter, allowNull);
	}

	public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(final Pos alignment,
			final boolean allowNull) {
		return _ -> new TableCellEditDatePicker<>(new LocalDateStringConverter(), alignment, allowNull);
	}

	public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn(
			final StringConverter<LocalDate> converter, final Pos alignment, final boolean allowNull) {
		return _ -> new TableCellEditDatePicker<>(converter, alignment, allowNull);
	}

	private final StringConverter<LocalDate> converter;
	private final BooleanProperty allowNull = new SimpleBooleanProperty();

	public TableCellEditDatePicker(StringConverter<LocalDate> converter) {
		this(converter, Pos.CENTER_LEFT, false);
	}

	public TableCellEditDatePicker(StringConverter<LocalDate> converter, boolean allowNull) {
		this(converter, Pos.CENTER_LEFT, allowNull);
	}

	public TableCellEditDatePicker(StringConverter<LocalDate> converter, Pos alignment) {
		this(converter, alignment, false);
	}

	public TableCellEditDatePicker(StringConverter<LocalDate> converter, Pos alignment, boolean allowNull) {
		this.converter = converter;
		this.allowNull.set(allowNull);
		this.setAlignment(alignment);
		this.getStyleClass().add("text-input-table-cell");
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull.set(allowNull);
	}

	public boolean allowNull() {
		return this.allowNull.get();
	}

	@Override
	protected void setCellText() {
		this.setText(this.getItem() == null ? ""
				: (this.converter == null ? this.getItem().toString() : this.converter.toString(this.getItem())));
	}

	@Override
	protected void setControlValue() {
		((DatePicker) getControl()).setValue(this.getItem());
	}

	@Override
	protected LocalDate getFromControl() {
		return ((DatePicker) getControl()).getValue();
	}

	@Override
	protected void startEditControl() {
		((DatePicker) getControl()).requestFocus();
	}

	@Override
	protected DatePicker createControl() {
		DatePicker field = new CustomDatePicker(this.getItem());
		field.getStyleClass().add("date-picker-table-cell");
		return field;
	}

	private static class CustomDatePicker extends DatePicker {

		public CustomDatePicker(LocalDate localDate) {
			super(localDate);
			// without this, old value overrides delete after changing table cell
			this.getEditor().textProperty().addListener((_, _, text) -> {
				if (text == null || text.isEmpty()) {
					this.setValue(null);
				}
			});
		}

	}
}
