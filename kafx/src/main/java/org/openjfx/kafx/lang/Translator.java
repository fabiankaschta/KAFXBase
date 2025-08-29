package org.openjfx.kafx.lang;

import java.util.Locale;
import java.util.ResourceBundle;

import org.openjfx.kafx.controller.Controller;

public class Translator {

	private final static MultiResourceBundleControl control;
	private static ResourceBundle bundle;

	static {
		control = new MultiResourceBundleControl("lang");
		control.addBundleName("kafx.lang.kafx");
	}

	public static void addBundleName(String bundleName) {
		control.addBundleName(bundleName);
	}

	public static String get(String key) {
		if (bundle == null) {
			try {
				bundle = ResourceBundle.getBundle(control.getBaseName(), Locale.getDefault(), control);
			} catch (Exception e) {
				Controller.exception(e);
				return '[' + key + ']';
			}
		}
		try {
			if (bundle.containsKey(key)) {
				return bundle.getString(key);
			} else {
				Controller.log(Controller.DEBUG, "missing lang " + key + " " + bundle.getLocale());
				return '[' + key + ']';
			}
		} catch (Exception e) {
			Controller.log(Controller.DEBUG, "missing lang " + key + " " + bundle.getLocale());
			return '[' + key + ']';
		}
	}

	private Translator() {
	}

}
