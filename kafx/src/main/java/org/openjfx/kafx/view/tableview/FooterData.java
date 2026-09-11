package org.openjfx.kafx.view.tableview;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;

public abstract class FooterData<T> {

	protected abstract ObservableValue<String> getDataForColumn(TableColumn<T, ?> mainColumn);

}
