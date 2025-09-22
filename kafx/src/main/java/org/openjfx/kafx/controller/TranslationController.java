package org.openjfx.kafx.controller;

import java.util.ResourceBundle;
import java.util.logging.Level;

import org.openjfx.kafx.lang.Translator;

public class TranslationController extends Controller {

	private static TranslationController controller;

	private final Translator translator;

	protected TranslationController(Translator translator) {
		this.translator = translator;
	}

	public static void init() {
		init(new TranslationController(new Translator()));
	}

	public static void init(Translator translator) {
		init(new TranslationController(translator));
	}

	public static void init(TranslationController controller) {
		log(DEBUG, "init translation controller");
		TranslationController.controller = controller;
	}

	public static boolean isInitialized() {
		return controller != null;
	}

	public static void addBundle(ResourceBundle bundle) {
		if (isInitialized()) {
			controller.translator.addBundle(bundle);
		}
	}

	public static String translate(String key) {
		if (!isInitialized()) {
			return '[' + key + ']';
		} else {
			try {
				return controller.translator.get(key);
			} catch (Exception e) {
				Controller.log(Level.WARNING,
						"missing lang for " + key + " in locale " + controller.translator.getLocale());
				return '[' + key + ']';
			}
		}
	}

}
