package org.diylc.app.dialogs.properties;

import java.awt.Color;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.Constants;

public class ByteEditor extends JSlider {

	private static final long serialVersionUID = 1L;

	private Color oldBg = getBackground();

	public ByteEditor(final PropertyWrapper property) {
		super();
		setMinimum(0);
		setMaximum(127);
		setValue((Byte) property.getValue());
		addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				property.setChanged(true);
				setBackground(oldBg);
				property.setValue(new Integer(getValue()).byteValue());
			}
		});
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}
}
