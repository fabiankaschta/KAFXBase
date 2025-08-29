package org.openjfx.kafx.lang;

import java.util.Locale;
import java.util.ResourceBundle;

import org.openjfx.kafx.controller.Controller;

public class Translator {

	private final ResourceBundle defaultBundle;
	private final ResourceBundle customBundle;

	public Translator() {
		this(null, Locale.getDefault());
	}

	public Translator(Locale locale) {
		this(null, locale);
	}

	public Translator(ResourceBundle customBundle) {
		this(customBundle, Locale.getDefault());
	}

	public Translator(ResourceBundle customBundle, Locale locale) {
		ResourceBundle defaultBundle = null;
		try {
			defaultBundle = ResourceBundle.getBundle(Translator.class.getPackageName() + ".kafx", locale);
		} catch (Exception e) {
			System.out.println(e);
//			Controller.exception(e);
		}
		this.defaultBundle = defaultBundle;
		this.customBundle = customBundle;
	}

	public String get(String key) {
		try {
			if (this.customBundle != null && this.customBundle.containsKey(key)) {
				return this.customBundle.getString(key);
			} else if (this.defaultBundle != null && this.defaultBundle.containsKey(key)) {
				return this.defaultBundle.getString(key);
			} else {
				Controller.log(Controller.DEBUG, "missing lang " + key + " " + defaultBundle.getLocale());
				return '[' + key + ']';
			}
		} catch (Exception e) {
			Controller.log(Controller.DEBUG, "missing lang " + key + " " + defaultBundle.getLocale());
			return '[' + key + ']';
		}
	}

}
