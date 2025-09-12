package org.openjfx.kafx.lang;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.openjfx.kafx.controller.ExceptionController;

public class Translator {

	private final ResourceBundle defaultBundle;
	private final ResourceBundle customBundle;
	private final Locale locale;

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
			ExceptionController.exception(e);
		}
		this.defaultBundle = defaultBundle;
		this.customBundle = customBundle;
		this.locale = locale;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public String get(String key) {
		if (this.customBundle != null && this.customBundle.containsKey(key)) {
			return this.customBundle.getString(key);
		} else if (this.defaultBundle != null && this.defaultBundle.containsKey(key)) {
			return this.defaultBundle.getString(key);
		} else {
			throw new MissingResourceException("key not in default or custom bundle", "Translator", key);
		}
	}

}
