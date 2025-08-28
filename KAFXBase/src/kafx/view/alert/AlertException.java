package kafx.view.alert;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import kafx.controller.Controller;
import kafx.lang.Translator;

public class AlertException extends Alert {

	public AlertException(Exception e) {
		super(AlertType.ERROR);
		this.setGraphic(null);
		this.setHeaderText(e.toString());
		this.setTitle(Translator.get("alert_error_title"));
		this.setContentText(Translator.get("alert_error_main"));
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		TextArea area = new TextArea(sw.toString());
		area.setWrapText(true);
		area.setEditable(false);
		this.getDialogPane().setExpandableContent(area);
		this.setResizable(true);
		Controller.fontSizeProperty()
				.subscribe(fontSize -> this.getDialogPane().setStyle("-fx-font-size: " + fontSize));
	}

}
