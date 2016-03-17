package org.diylc.app.view.editors;

import java.awt.Color;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.diylc.core.components.properties.PropertyDescriptor;

import org.diylc.core.utils.Constants;

public class ByteEditor extends JSlider {

	private static final long serialVersionUID = 1L;

	private Color oldBg = getBackground();

    private PropertyDescriptor property;

	public ByteEditor(final PropertyDescriptor property) {
		super();
        this.property = property;
		setMinimum(0);
		setMaximum(127);
		setValue((Byte) property.getValue());
		addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
			    ByteEditor.this.property.setChanged(true);
				setBackground(oldBg);
				byte newValue = new Integer(getValue()).byteValue();
				byte oldValue = (byte) ByteEditor.this.property.getValue();
				property.setValue(newValue);
				firePropertyChange(ByteEditor.this.property.getName(), oldValue, newValue);
			}
		});
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}
}
