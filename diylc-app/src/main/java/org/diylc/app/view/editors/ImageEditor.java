package org.diylc.app.view.editors;

import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.diylc.app.FileFilterEnum;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.config.Configuration;

public class ImageEditor extends JButton {

	private static final long serialVersionUID = 1L;

	private static final String title = "Click to load image file";

    private PropertyWrapper property;

	public ImageEditor(final PropertyWrapper property) {
		super(property.isUnique() ? title : ("(multi value) " + title));
        this.property = property;
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    ImageIcon oldValue = (ImageIcon) ImageEditor.this.property.getValue();
				Path path = DialogFactory.getInstance().showOpenDialog(
						FileFilterEnum.IMAGES.getFilter(), Configuration.INSTANCE.getLastPath(), null,
						FileFilterEnum.IMAGES.getExtensions()[0], null);
				if (path != null) {
					ImageEditor.this.property.setChanged(true);
					ImageIcon image = new ImageIcon(path.toFile().getAbsolutePath());
					ImageEditor.this.property.setValue(image);
					firePropertyChange(ImageEditor.this.property.getName(), oldValue, image);
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
