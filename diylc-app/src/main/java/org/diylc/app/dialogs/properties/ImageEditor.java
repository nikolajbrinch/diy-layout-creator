package org.diylc.app.dialogs.properties;

import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.diylc.app.dialogs.DialogFactory;
import org.diylc.app.menus.file.FileFilterEnum;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.config.Configuration;

public class ImageEditor extends JButton {

	private static final long serialVersionUID = 1L;

	private static final String title = "Click to load image file";

	public ImageEditor(final PropertyWrapper property) {
		super(property.isUnique() ? title : ("(multi value) " + title));
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				Path path = DialogFactory.getInstance().showOpenDialog(
						FileFilterEnum.IMAGES.getFilter(), Configuration.INSTANCE.getLastPath(), null,
						FileFilterEnum.IMAGES.getExtensions()[0], null);
				if (path != null) {
					property.setChanged(true);
					ImageIcon image = new ImageIcon(path.toFile().getAbsolutePath());
					property.setValue(image);
				}
			}
		});
	}

	// @Override
	// public void setBackground(Color bg) {
	// if (bg.getRed() < 127 || bg.getBlue() < 127 || bg.getGreen() < 127) {
	// setForeground(Color.white);
	// } else {
	// setForeground(Color.black);
	// }
	// super.setBackground(bg);
	// }
}
