package org.openjfx.kafx.view.style;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;

public class Styles {

	public static void subscribeThemeColor(Tab tab, ObjectProperty<Color> colorProperty) {
		tab.getStyleClass().add("themed");
		colorProperty.subscribe(color -> {
			StringBuilder style = new StringBuilder();
			style.append("-theme-color: " + toHexString(color) + ";");
			style.append("-theme-color-bright-heavy: " + toHexString(deriveBrightHeavy(color)) + ";");
			style.append("-theme-color-bright-mid: " + toHexString(deriveBrightMid(color)) + ";");
			style.append("-theme-color-faded-heavy: " + toHexString(deriveFadedHeavy(color)) + ";");
			style.append("-theme-color-faded-mid: " + toHexString(deriveFadedMid(color)) + ";");
			tab.setStyle((tab.getStyle() == null ? "" : tab.getStyle()) + style.toString());
		});
	}

	public static void subscribeThemeColor(Node node, ObjectProperty<Color> colorProperty) {
		node.getStyleClass().add("themed");
		colorProperty.subscribe(color -> {
			StringBuilder style = new StringBuilder();
			style.append("-theme-color: " + toHexString(color) + ";");
			style.append("-theme-color-bright-heavy: " + toHexString(deriveBrightHeavy(color)) + ";");
			style.append("-theme-color-bright-mid: " + toHexString(deriveBrightMid(color)) + ";");
			style.append("-theme-color-faded-heavy: " + toHexString(deriveFadedHeavy(color)) + ";");
			style.append("-theme-color-faded-mid: " + toHexString(deriveFadedMid(color)) + ";");
			node.setStyle((node.getStyle() == null ? "" : node.getStyle()) + style.toString());
		});
	}

	private static Color deriveBrightMid(Color color) {
		return color.interpolate(Color.WHITE, 0.5);
	}

	private static Color deriveBrightHeavy(Color color) {
		return color.interpolate(Color.WHITE, 0.9);
	}

	private static Color deriveFadedMid(Color color) {
		return color.interpolate(Color.WHITESMOKE, 0.5);
	}

	private static Color deriveFadedHeavy(Color color) {
		return color.interpolate(Color.WHITESMOKE, 0.8);
	}

	private Styles() {
	}

	private static String format(double val) {
		String in = Integer.toHexString((int) Math.round(val * 255));
		return in.length() == 1 ? "0" + in : in;
	}

	public static String toHexString(Color value) {
		return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue())
				+ format(value.getOpacity())).toUpperCase();
	}

}
