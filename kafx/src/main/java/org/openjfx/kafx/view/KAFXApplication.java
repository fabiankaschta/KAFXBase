package org.openjfx.kafx.view;

import java.io.File;

import org.openjfx.kafx.controller.CloseController;
import org.openjfx.kafx.controller.ConfigController;
import org.openjfx.kafx.controller.Controller;
import org.openjfx.kafx.controller.FileController;
import org.openjfx.kafx.controller.FontSizeController;
import org.openjfx.kafx.controller.TranslationController;
import org.openjfx.kafx.controller.UpdateController;
import org.openjfx.kafx.view.dialog.DialogFirstStart;
import org.openjfx.kafx.view.pane.MenuBarMessage;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public abstract class KAFXApplication extends Application {

	private final Node center;
	private final Node menuBar;
	private final Image icon;

	protected KAFXApplication(Node center, MenuBar menuBar) {
		this(center, menuBar, null);
	}

	protected KAFXApplication(Node center, MenuBarMessage menuBar) {
		this(center, menuBar, null);
	}

	protected KAFXApplication(Node center, MenuBar menuBar, Image icon) {
		this.center = center;
		this.menuBar = menuBar;
		this.icon = icon;
		Controller.setApplication(this);
	}

	protected KAFXApplication(Node center, MenuBarMessage menuBar, Image icon) {
		this.center = center;
		this.menuBar = menuBar;
		this.icon = icon;
		Controller.setApplication(this);
	}

	@Override
	public void start(Stage primaryStage) {
		Controller.setPrimaryStage(primaryStage);

		primaryStage.setMaximized(Boolean.valueOf(ConfigController.get("MAXIMIZED")));
		primaryStage.maximizedProperty()
				.subscribe(maximized -> ConfigController.set("MAXIMIZED", String.valueOf(maximized)));
		primaryStage.setTitle(TranslationController.translate("app_title"));
		primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> CloseController.close(e));
		if (this.icon != null) {
			primaryStage.getIcons().add(this.icon);
		}

		BorderPane root = new BorderPane();
		root.setTop(this.menuBar);
		root.setCenter(this.center);

		Scene scene = new Scene(root, Double.valueOf(ConfigController.get("WIDTH")),
				Double.valueOf(ConfigController.get("HEIGHT")));
		scene.getStylesheets().add(Controller.getStylesheetURL().toExternalForm());
		scene.widthProperty().subscribe(width -> {
			if (!primaryStage.isMaximized()) {
				ConfigController.set("WIDTH", String.valueOf(width));
			}
		});
		scene.heightProperty().subscribe(height -> {
			if (!primaryStage.isMaximized()) {
				ConfigController.set("HEIGHT", String.valueOf(height));
			}
		});

		primaryStage.setScene(scene);
		primaryStage.setOnShown(_ -> UpdateController.checkForUpdates(true));

		FontSizeController.fontSizeProperty().subscribe(fontSize -> root.setStyle("-fx-font-size: " + fontSize));
		root.setOnScroll(event -> {
			if (event.isControlDown()) {
				event.consume();
				int fontSize = FontSizeController.getFontSize();
				if (event.getDeltaY() < 0 && fontSize > 0) {
					FontSizeController.setFontSize(fontSize - 1);
				} else if (event.getDeltaY() > 0) {
					FontSizeController.setFontSize(fontSize + 1);
				}
			}
		});

		if (!ConfigController.exists("LAST_FILE") || !(new File(ConfigController.get("LAST_FILE")).exists())
				|| !FileController.openLastFile()) {
			new DialogFirstStart();
		} else {
			primaryStage.show();
		}
	}

}
