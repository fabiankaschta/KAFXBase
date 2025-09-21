package org.openjfx.kafx.controller;

import java.util.List;

import org.pf4j.JarPluginManager;

public class PluginController extends Controller {

	private static PluginController controller;

	private final JarPluginManager pluginMananger;

	protected PluginController(JarPluginManager pluginMananger) {
		this.pluginMananger = pluginMananger;
		this.pluginMananger.loadPlugins();
		this.pluginMananger.startPlugins();
	}

	public static void init(JarPluginManager pluginMananger) {
		init(new PluginController(pluginMananger));
	}

	public static void init(PluginController controller) {
		log(DEBUG, "init plugin controller");
		PluginController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static <T> List<T> getExtensions(Class<T> type) {
		if (!isInitialized()) {
			return null;
		} else {
			log(DEBUG, "retrieving plugin extensions for class " + type);
			return controller.handleGetExtensions(type);
		}
	}

	protected <T> List<T> handleGetExtensions(Class<T> type) {
		return pluginMananger.getExtensions(type);
	}

}
