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
		log(DEBUG, "init exception controller");
		ExceptionController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void exception(Exception e) {
		if (isInitialized()) {
			log(Level.SEVERE, e.getMessage());
			controller.handleException(e);
		}
	}

	protected void handleException(Exception e) {
		new AlertException(e).showAndWait();
	}

}
