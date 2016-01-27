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

    private PropertyWrapper property;

	public ByteEditor(final PropertyWrapper property) {
		super();
        this.property = property;
		setMinimum(0);
		setMaximum(127);
		setValue((Byte) property.getValue());
		addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				property.setChanged(true);
				setBackground(oldBg);
				byte newValue = new Integer(getValue()).byteValue();
				byte oldValue = (byte) property.getValue();
				property.setValue(newValue);
				firePropertyChange(property.getName(), oldValue, newValue);
			}
		});
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}
}
