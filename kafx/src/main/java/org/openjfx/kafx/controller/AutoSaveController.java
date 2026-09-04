package org.openjfx.kafx.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class AutoSaveController extends Controller {

	private static AutoSaveController controller = new AutoSaveController();

	protected AutoSaveController() {
		ConfigController.putIfNotExists("AUTO_SAVE_INTERVAL", "5m");
		this.interval.set(Duration.valueOf(ConfigController.get("AUTO_SAVE_INTERVAL")));
		this.interval.subscribe(interval -> {
			ConfigController.set("AUTO_SAVE_INTERVAL", String.valueOf((int) interval.toMinutes()) + "m");
			this.saveService.setDelay(interval);
			this.saveService.setPeriod(interval);
			LogController.log(LogController.DEBUG, "autosave interval set " + interval);
		});
		this.active.set(Boolean.valueOf(ConfigController.get("USE_AUTO_SAVE")));
		this.active.subscribe(active -> {
			ConfigController.set("USE_AUTO_SAVE", String.valueOf(active));
			if (this.running) {
				if (active) {
					LogController.log(LogController.DEBUG, "autosave activated");
					this.saveService.restart();
				} else {
					LogController.log(LogController.DEBUG, "autosave deactivated");
					this.saveService.cancel();
				}
			}
		});
	}

	public static void init() {
		init(new AutoSaveController());
	}

	public static void init(AutoSaveController controller) {
		LogController.log(LogController.DEBUG, "init autosave controller");
		AutoSaveController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	private boolean running;
	protected BooleanProperty active = new SimpleBooleanProperty(this, "active", false);
	protected ObjectProperty<Duration> interval = new SimpleObjectProperty<>(this, "interval", Duration.ZERO);

	private final ScheduledService<Object> saveService = new ScheduledService<>() {

		@Override
		protected Task<Object> createTask() {
			return new Task<>() {

				@Override
				protected Object call() throws Exception {
					Platform.runLater(() -> {
						boolean success = FileController.saveFile();
						if (success) {
							LogController.log(LogController.DEBUG, "autosave successful");
						} else {
							LogController.log(LogController.DEBUG, "autosave unsuccessful");
						}
					});
					return null;
				}

			};
		}

	};

	public static void start() {
		if (isInitialized()) {
			controller.running = true;
			if (isActive()) {
				controller.saveService.restart();
			}
			LogController.log(LogController.DEBUG, "autosave controller started");
		}
	}

	public static void stop() {
		if (isInitialized()) {
			controller.running = false;
			if (isActive()) {
				controller.saveService.cancel();
			}
			LogController.log(LogController.DEBUG, "autosave controller stopped");
		}
	}

	public static BooleanProperty activeProperty() {
		if (!isInitialized()) {
			return null;
		} else {
			return controller.active;
		}
	}

	public static boolean isActive() {
		if (!isInitialized()) {
			return false;
		} else {
			return controller.active.get();
		}
	}

	public static void setActive(boolean active) {
		if (isInitialized()) {
			controller.active.set(active);
		}
	}

	public static ObjectProperty<Duration> intervalProperty() {
		if (!isInitialized()) {
			return null;
		} else {
			return controller.interval;
		}
	}

	public static Duration getInterval() {
		if (!isInitialized()) {
			return Duration.ZERO;
		} else {
			return controller.interval.get();
		}
	}

	public static void setInterval(Duration interval) {
		if (isInitialized()) {
			controller.interval.set(interval);
		}
	}

}
