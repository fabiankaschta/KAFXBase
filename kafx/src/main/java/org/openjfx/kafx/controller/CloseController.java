package org.openjfx.kafx.controller;

import org.openjfx.kafx.view.alert.AlertSaveChanges;

import javafx.event.Event;
import javafx.scene.control.ButtonType;

public class CloseController extends Controller {

	private static CloseController controller;

	protected CloseController() {
	}

	public static void init() {
		init(new CloseController());
	}

	public static void init(CloseController controller) {
		log(DEBUG, "init close controller");
		CloseController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void close() {
		close(new Event(null));
	}

	public static void close(Event event) {
		if (isInitialized()) {
			log(DEBUG, "close event");
			controller.handleClose(event);
		}
	}

	protected void handleClose(Event event) {
		if (ChangeController.hasChanges()) {
			new AlertSaveChanges().showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK) {
					log(DEBUG, "close save changes - ok");
					if (!FileController.writeToFile()) {
						event.consume();
					}
				} else if (response == ButtonType.CANCEL) {
					event.consume();
					log(DEBUG, "close save changes - cancel");
				} else if (response == ButtonType.NO) {
					log(DEBUG, "close save changes - no");
				}
			});
		} else {
			log(DEBUG, "close no changes");
		}
		if (!event.isConsumed()) {
			ConfigController.store();
			// TODO AutoSave.stop();
		}
	}

}
