package org.openjfx.kafx.view.imageview;

import java.io.InputStream;

import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class EmojiImageView extends ImageView {

	/**
	 * 
	 * @param emoji unicode number (excluding u+)
	 * @throws IllegalArgumentException if no emoji can be found for the given
	 *                                  parameter
	 */
	public EmojiImageView(String emoji) {
		this(emoji, null);
	}

	/**
	 * 
	 * @param emoji          unicode number (excluding u+)
	 * @param observableSize observable to adapt size (width and height)
	 *                       simultaneously
	 * @throws IllegalArgumentException if no emoji can be found for the given
	 *                                  parameter
	 */
	public EmojiImageView(String emoji, ObservableValue<? extends Number> observableSize) {
		InputStream emojiResource = EmojiImageView.class
				.getResourceAsStream("/emojitwo/png/" + emoji.toLowerCase() + ".png");
		if (emojiResource == null) {
			throw new IllegalArgumentException("No emoji was found for unicode number " + emoji);
		} else {
			this.setImage(new Image(emojiResource));
		}
		if (observableSize != null) {
			this.bindSize(observableSize);
		}
	}

	public void bindSize(ObservableValue<? extends Number> observableSize) {
		this.fitWidthProperty().bind(observableSize);
		this.fitHeightProperty().bind(observableSize);
	}

}
