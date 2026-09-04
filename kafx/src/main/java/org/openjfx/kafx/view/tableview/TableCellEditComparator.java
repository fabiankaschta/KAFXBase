package org.openjfx.kafx.view.tableview;

import java.util.Comparator;

import org.openjfx.kafx.view.control.ComparatorField;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class TableCellEditComparator<S, T> extends TableCellEditControl<S, T> {

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final StringConverter<T> converter) {
		return _ -> new TableCellEditComparator<>(comparator, converter);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final ObjectProperty<T> minValue, final ObjectProperty<T> maxValue,
			final StringConverter<T> converter) {
		return _ -> new TableCellEditComparator<>(comparator, minValue, maxValue, converter);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final T minValue, final T maxValue, final StringConverter<T> converter) {
		return _ -> new TableCellEditComparator<>(comparator, minValue, maxValue, converter);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final StringConverter<T> converter, final Pos alignment) {
		return _ -> new TableCellEditComparator<>(comparator, converter, alignment);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final ObjectProperty<T> minValue, final ObjectProperty<T> maxValue,
			final StringConverter<T> converter, final Pos alignment) {
		return _ -> new TableCellEditComparator<>(comparator, minValue, maxValue, converter, alignment);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final T minValue, final T maxValue, final StringConverter<T> converter,
			final Pos alignment) {
		return _ -> new TableCellEditComparator<>(comparator, minValue, maxValue, converter, alignment);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final StringConverter<T> converter, final boolean allowNull) {
		return _ -> new TableCellEditComparator<>(comparator, converter, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final ObjectProperty<T> minValue, final ObjectProperty<T> maxValue,
			final StringConverter<T> converter, final boolean allowNull) {
		return _ -> new TableCellEditComparator<>(comparator, minValue, maxValue, converter, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final T minValue, final T maxValue, final StringConverter<T> converter,
			final boolean allowNull) {
		return _ -> new TableCellEditComparator<>(comparator, minValue, maxValue, converter, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final StringConverter<T> converter, final Pos alignment,
			final boolean allowNull) {
		return _ -> new TableCellEditComparator<>(comparator, converter, alignment, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final ObjectProperty<T> minValue, final ObjectProperty<T> maxValue,
			final StringConverter<T> converter, final Pos alignment, final boolean allowNull) {
		return _ -> new TableCellEditComparator<>(comparator, minValue, maxValue, converter, alignment, allowNull);
	}

	public static <S, T extends Comparable<T>> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
			final Comparator<T> comparator, final T minValue, final T maxValue, final StringConverter<T> converter,
			final Pos alignment, final boolean allowNull) {
		return _ -> new TableCellEditComparator<>(comparator, minValue, maxValue, converter, alignment, allowNull);
	}

	private final Comparator<T> comparator;
	private final StringConverter<T> converter;
	private ObjectProperty<T> minValue = new SimpleObjectProperty<>();
	private ObjectProperty<T> maxValue = new SimpleObjectProperty<>();
	private BooleanProperty allowNull = new SimpleBooleanProperty();

	public TableCellEditComparator(Comparator<T> comparator, StringConverter<T> converter) {
		this(comparator, converter, Pos.CENTER_LEFT, false);
	}

	public TableCellEditComparator(Comparator<T> comparator, StringConverter<T> converter, boolean allowNull) {
		this(comparator, converter, Pos.CENTER_LEFT, allowNull);
	}

	public TableCellEditComparator(Comparator<T> comparator, ObjectProperty<T> minValue, ObjectProperty<T> maxValue,
			StringConverter<T> converter) {
		this(comparator, minValue, maxValue, converter, Pos.CENTER_LEFT, false);
	}

	public TableCellEditComparator(Comparator<T> comparator, ObjectProperty<T> minValue, ObjectProperty<T> maxValue,
			StringConverter<T> converter, boolean allowNull) {
		this(comparator, minValue, maxValue, converter, Pos.CENTER_LEFT, allowNull);
	}

	public TableCellEditComparator(Comparator<T> comparator, T minValue, T maxValue, StringConverter<T> converter) {
		this(comparator, minValue, maxValue, converter, Pos.CENTER_LEFT, false);
	}

	public TableCellEditComparator(Comparator<T> comparator, T minValue, T maxValue, StringConverter<T> converter,
			boolean allowNull) {
		this(comparator, minValue, maxValue, converter, Pos.CENTER_LEFT, allowNull);
	}

	public TableCellEditComparator(Comparator<T> comparator, StringConverter<T> converter, Pos alignment) {
		this(comparator, converter, alignment, false);
	}

	public TableCellEditComparator(Comparator<T> comparator, StringConverter<T> converter, Pos alignment,
			boolean allowNull) {
		this.comparator = comparator;
		this.minValue.set(null);
		this.maxValue.set(null);
		this.converter = converter;
		this.allowNull.set(allowNull);
		this.setAlignment(alignment);
		this.getStyleClass().add("text-input-table-cell");
	}

	public TableCellEditComparator(Comparator<T> comparator, ObjectProperty<T> minValue, ObjectProperty<T> maxValue,
			StringConverter<T> converter, Pos alignment) {
		this(comparator, minValue, maxValue, converter, alignment, false);
	}

	public TableCellEditComparator(Comparator<T> comparator, ObjectProperty<T> minValue, ObjectProperty<T> maxValue,
			StringConverter<T> converter, Pos alignment, boolean allowNull) {
		this.comparator = comparator;
		this.minValue.bind(minValue);
		this.maxValue.bind(maxValue);
		this.converter = converter;
		this.allowNull.set(allowNull);
		this.setAlignment(alignment);
		this.getStyleClass().add("text-input-table-cell");
	}

	public TableCellEditComparator(Comparator<T> comparator, T minValue, T maxValue, StringConverter<T> converter,
			Pos alignment) {
		this(comparator, minValue, maxValue, converter, alignment, false);
	}

	public TableCellEditComparator(Comparator<T> comparator, T minValue, T maxValue, StringConverter<T> converter,
			Pos alignment, boolean allowNull) {
		this.comparator = comparator;
		this.minValue.set(minValue);
		this.maxValue.set(maxValue);
		this.converter = converter;
		this.allowNull.set(allowNull);
		this.setAlignment(alignment);
		this.getStyleClass().add("text-input-table-cell");
	}

	public T getMinValue() {
		return minValue.get();
	}

	public void setMinValue(T minValue) {
		this.minValue.set(minValue);
	}

	public ObjectProperty<T> minValueProperty() {
		return this.minValue;
	}

	public T getMaxValue() {
		return maxValue.get();
	}

	public void setMaxValue(T maxValue) {
		this.maxValue.set(maxValue);
	}

	public ObjectProperty<T> maxValueProperty() {
		return this.maxValue;
	}

	public boolean isAllowNull() {
		return this.allowNull.get();
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull.set(allowNull);
	}

	public BooleanProperty allowNullProperty() {
		return this.allowNull;
	}

	@Override
	protected void setCellText() {
		this.setText(this.getItem() == null ? ""
				: (this.converter == null ? this.getItem().toString() : this.converter.toString(this.getItem())));
	}

	@Override
	protected void setControlValue() {
		@SuppressWarnings("unchecked")
		ComparatorField<T> field = (ComparatorField<T>) getControl();
		field.setValue(this.getItem());
	}

	@Override
	protected T getFromControl() {
		@SuppressWarnings("unchecked")
		ComparatorField<T> field = (ComparatorField<T>) getControl();
		return field.getValue();
	}

	@Override
	protected void startEditControl() {
		@SuppressWarnings("unchecked")
		ComparatorField<T> field = (ComparatorField<T>) getControl();
		field.selectAll();
		field.requestFocus();
	}

	@Override
	protected ComparatorField<T> createControl() {
		ComparatorField<T> field = new ComparatorField<>(this.comparator, this.minValue, this.maxValue, this.getItem(),
				this.converter);
		field.alignmentProperty().bind(this.alignmentProperty());
		field.getStyleClass().add("text-input-table-cell");
		field.allowNullProperty().bind(this.allowNull);
		return field;
	}
}
