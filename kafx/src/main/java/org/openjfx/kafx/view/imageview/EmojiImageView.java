package org.openjfx.kafx.view.imageview;

import java.io.InputStream;

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
		InputStream emojiResource = EmojiImageView.class.getResourceAsStream("/emojitwo/png/" + emoji.toLowerCase() + ".png");
		if (emojiResource == null) {
			throw new IllegalArgumentException("No emoji was found for unicode number " + emoji);
		} else {
			this.setImage(new Image(emojiResource));
		}
	}

}
