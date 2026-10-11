package org.openjfx.kafx.view.tableview;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;

public class TableColumnFooter<S, T> extends TableColumn<S, T> {

	private final StringProperty footerText = new SimpleStringProperty(this, "footerText", null);
	private final ObjectProperty<Node> footerGraphic = new SimpleObjectProperty<>(this, "footerGraphic", null);

	public TableColumnFooter() {
		super();
	}

	public TableColumnFooter(String headerText) {
		this();
		super.setText(headerText);
	}

	public TableColumnFooter(String headerText, String footerText) {
		this();
		super.setText(headerText);
		this.setFooterText(footerText);
	}

	public TableColumnFooter(String headerText, ObservableStringValue footerText) {
		this();
		super.setText(headerText);
		this.footerText.bind(footerText);
	}

	public void setFooterText(String text) {
		if (this.footerText.isBound()) {
			throw new IllegalStateException("footer text is bound and therefore cannot be set");
		} else {
			this.footerText.set(text);
		}
	}

	public String getFooterText() {
		return this.footerText.get();
	}

	public StringProperty footerText() {
		return this.footerText;
	}

	public void setFooterGraphic(Node graphic) {
		if (this.footerGraphic.isBound()) {
			throw new IllegalStateException("footer graphic is bound and therefore cannot be set");
		} else {
			this.footerGraphic.set(graphic);
		}
	}

	public Node getFooterGraphic() {
		return this.footerGraphic.get();
	}

	public ObjectProperty<Node> footerGraphic() {
		return this.footerGraphic;
	}

}
