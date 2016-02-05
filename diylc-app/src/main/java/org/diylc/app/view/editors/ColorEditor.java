package org.diylc.app.view.editors;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.diylc.core.PropertyWrapper;

public class ColorEditor extends JLabel  {

	private static final long serialVersionUID = 1L;

	private static final String title = "Click to edit";

    private PropertyWrapper property;

	public ColorEditor(final PropertyWrapper property) {
		super(property.isUnique() ? title : ("(multi value) " + title));
        this.property = property;
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setBorder(BorderFactory.createEtchedBorder());
		setBackground((Color) property.getValue());
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
			    Color oldColor = (Color) ColorEditor.this.property.getValue();
				Color newColor = JColorChooser.showDialog(ColorEditor.this, "Choose Color",
						getBackground());
				if (newColor != null) {
				    ColorEditor.this.property.setChanged(true);
				    ColorEditor.this.property.setValue(newColor);
					setBackground(newColor);
					firePropertyChange(ColorEditor.this.property.getName(), oldColor, newColor);
				}
			}
		});
	}

	@Override
	public void setBackground(Color bg) {
		if (bg.getRed() < 127 || bg.getBlue() < 127 || bg.getGreen() < 127) {
			setForeground(Color.white);
		} else {
			setForeground(Color.black);
		}
		super.setBackground(bg);
	}
}
