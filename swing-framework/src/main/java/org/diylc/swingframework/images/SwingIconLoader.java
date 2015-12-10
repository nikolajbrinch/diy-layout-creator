package org.diylc.swingframework.images;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads image resources as Icons.
 * 
 * @author Branislav Stojkovic
 */
public enum SwingIconLoader {

	Error("error.png"), Warning("warning.png"), LightBulbOn("lightbulb_on.png"), LightBulbOff(
			"lightbulb_off.png"), MoveSmall("move_small.png"), Undo("undo.png"), Redo("redo.png"), ;

	private static final Logger LOG = LoggerFactory.getLogger(SwingIconLoader.class);

	protected String name;

	private SwingIconLoader(String name) {
		this.name = name;
	}

	public Icon getIcon() {
		URL imgURL = getClass().getResource("/icons/" + name);
		if (imgURL != null) {
			return new ImageIcon(imgURL, name);
		} else {
			LOG.error("Couldn't find file: " + imgURL);
			return null;
		}
	}
}
