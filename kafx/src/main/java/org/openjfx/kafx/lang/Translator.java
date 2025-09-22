package org.openjfx.kafx.lang;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.openjfx.kafx.controller.ExceptionController;

public class Translator {

	private final List<ResourceBundle> bundles = new LinkedList<>();
	private final Locale locale;

	public Translator() {
		this(Locale.getDefault());
	}

	public Translator(Locale locale) {
		ResourceBundle defaultBundle = null;
		try {
			defaultBundle = ResourceBundle.getBundle(Translator.class.getPackageName() + ".kafx", locale);
		} catch (Exception e) {
			ExceptionController.exception(e);
		}
		this.bundles.add(defaultBundle);
		this.locale = locale;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void addBundle(ResourceBundle bundle) {
		this.bundles.addFirst(bundle);
	}

	public String get(String key) {
		for (ResourceBundle bundle : this.bundles) {
			if (bundle.containsKey(key)) {
				return bundle.getString(key);
			}
		}
		throw new MissingResourceException("key missing", "Translator", key);
	}

}
