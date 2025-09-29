package org.openjfx.kafx.controller;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableView;

public class FontSizeController extends Controller {

	private static FontSizeController controller = new FontSizeController();

	protected FontSizeController() {
		fontSize.set(Integer.valueOf(ConfigController.get("FONT_SIZE")));
		fontSize.subscribe(fontSize -> ConfigController.set("FONT_SIZE", String.valueOf(fontSize)));
	}

	public static void init() {
		init(new FontSizeController());
	}

	public static void init(FontSizeController controller) {
		LogController.log(LogController.DEBUG, "init font size controller");
		FontSizeController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	protected IntegerProperty fontSize = new SimpleIntegerProperty(this, "fontSize", 12);

	public static IntegerProperty fontSizeProperty() {
		if (!isInitialized()) {
			return null;
		} else {
			return controller.fontSize;
		}
	}

	public static int getFontSize() {
		if (!isInitialized()) {
			return -1;
		} else {
			return controller.fontSize.get();
		}
	}

	public static void setFontSize(int fontSize) {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "set font size: " + fontSize);
			controller.fontSize.set(fontSize);
		}
	}

	public static <T> void bindTableColumnWidthToFontSize(TableView<T> tableView) {
		if (isInitialized()) {
			fontSizeProperty().subscribe((oldSize, newSize) -> {
				tableView.getColumns().forEach(column -> {
					double width = column.getWidth();
					double widthRatio = width / oldSize.doubleValue();
					double newWidth = widthRatio * newSize.doubleValue();
					tableView.resizeColumn(column, newWidth - width);
				});
			});
		}
	}

}
