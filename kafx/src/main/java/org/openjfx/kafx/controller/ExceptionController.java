package org.openjfx.kafx.controller;

import java.util.logging.Level;

import org.openjfx.kafx.view.alert.AlertException;

public class ExceptionController extends Controller {

	private static ExceptionController controller;

	protected ExceptionController() {
	}

	public static void init() {
		init(new ExceptionController());
	}

	public static void init(ExceptionController controller) {
		LogController.log(LogController.DEBUG, "init exception controller");
		ExceptionController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void exception(Throwable t) {
		if (isInitialized()) {
			LogController.log(Level.SEVERE, t.getMessage());
			controller.handleException(t);
		}
	}

	protected void handleException(Throwable t) {
		new AlertException(t).showAndWait();
	}

}
