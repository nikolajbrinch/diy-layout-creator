package org.diylc.swingframework.images;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * Loads image resources as Icons.
 * 
 * @author Branislav Stojkovic
 */
public enum IconLoader {

	Error("error.png"), Warning("warning.png"), LightBulbOn("lightbulb_on.png"), LightBulbOff(
			"lightbulb_off.png"), MoveSmall("move_small.png"), Undo("undo.png"), Redo("redo.png"), ;

	private static final Logger LOG = Logger.getLogger(IconLoader.class);

	protected String name;

	private IconLoader(String name) {
		this.name = name;
	}

	public Icon getIcon() {
		java.net.URL imgURL = getClass().getResource("/icons/" + name);
		if (imgURL != null) {
			return new ImageIcon(imgURL, name);
		} else {
			LOG.error("Couldn't find file: " + imgURL);
			return null;
		}
	}
}
