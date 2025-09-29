package org.openjfx.kafx.controller;

import java.util.function.Supplier;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;

public class ChangeController extends Controller {

	private static ChangeController controller;

	protected ChangeController() {
	}

	public static void init() {
		init(new ChangeController());
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void init(ChangeController controller) {
		LogController.log(LogController.DEBUG, "init change controller");
		ChangeController.controller = controller;
	}

	private long changeCounter = 0;

	public final static ChangeListener<Object> LISTENER_UNSAVED_CHANGES = (o, a, b) -> {
		LogController.log(LogController.DEBUG, o + " " + a + " -> " + b);
		change();
	};
	public final static ListChangeListener<Object> LISTLISTENER_UNSAVED_CHANGES = c -> {
		LogController.log(LogController.DEBUG, c.toString());
		change();
	};
	public final static MapChangeListener<Object, Object> MAPLISTENER_UNSAVED_CHANGES = m -> {
		LogController.log(LogController.DEBUG, m.toString());
		change();
	};

	public static ChangeListener<Object> getConditionalListenerUnsavedChanges(Supplier<Boolean> condition) {
		ChangeListener<Object> listener = (o, a, b) -> {
			if (condition.get()) {
				LogController.log(LogController.DEBUG, o + " " + a + " -> " + b);
				change();
			}
		};
		return listener;
	}

	protected final void incChangeCounter() {
		changeCounter++;
	}

	protected final void resetChangeCounter() {
		changeCounter = 0;
	}

	protected final long getChangeCounter() {
		return changeCounter;
	}

	private static void change() {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "change registered");
			controller.incChangeCounter();
			controller.handleChange();
		}
	}

	protected void handleChange() {
	}

	public static void resetChanges() {
		if (isInitialized()) {
			LogController.log(LogController.DEBUG, "changes reset");
			controller.resetChangeCounter();
			controller.handleChange();
		}
	}

	public static boolean hasChanges() {
		if (isInitialized()) {
			return controller.getChangeCounter() > 0;
		} else {
			return false;
		}
	}

}
