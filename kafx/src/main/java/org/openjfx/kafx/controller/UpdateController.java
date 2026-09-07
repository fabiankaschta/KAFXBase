package org.openjfx.kafx.controller;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.openjfx.kafx.view.alert.AlertNoUpdates;
import org.openjfx.kafx.view.alert.AlertVersion;

import com.github.zafarkhaja.semver.Version;

import javafx.scene.control.ButtonType;

public class UpdateController extends Controller {

	private static UpdateController controller;

	private final Map<String, Callable<String>> localVersions = new HashMap<>();
	private final Map<String, String> urls = new HashMap<>();

	protected UpdateController() {
	}

	public static void init() {
		init(new UpdateController());
	}

	public static void init(UpdateController controller) {
		LogController.log(LogController.DEBUG, "init update controller");
		UpdateController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void register(String name, Callable<String> localVersion, String url) {
		if (isInitialized()) {
			controller.localVersions.put(name, localVersion);
			controller.urls.put(name, url);
		}
	}

	public static void checkForUpdates(boolean silent) {
		if (isInitialized()) {
			// TODO merge all updates into one dialog
			boolean updateFound = false;
			for (Entry<String, Callable<String>> entry : controller.localVersions.entrySet()) {
				try {
					Version local = Version.parse(entry.getValue().call());
					URL url = URI.create(controller.urls.get(entry.getKey())).toURL();
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("HEAD");
					con.connect();
					int status = con.getResponseCode();
					if (status == 200) {
						String[] tmp = con.getURL().getFile().split("/");
						Version remote = Version.parse(tmp[tmp.length - 1].substring(1));
						if (local.isLowerThan(remote)) {
							updateFound = true;
							new AlertVersion(entry.getKey(), local.toString(), remote.toString(), url).showAndWait()
									.ifPresent(buttonType -> {
										if (buttonType == ButtonType.OK) {
											Controller.getApplication().getHostServices()
													.showDocument(controller.urls.get(entry.getKey()));
										}
									});
						}
					}
					con.disconnect();
				} catch (Exception e) {
					LogController.log(LogController.DEBUG, "unable to check for updates");
				}
			}
			if (!silent && !updateFound) {
				new AlertNoUpdates().showAndWait();
			}
		}
	}

}
