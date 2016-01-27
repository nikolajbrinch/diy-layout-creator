package org.diylc.app.dialogs.properties;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.Constants;

public class BooleanEditor extends JCheckBox {

	private static final long serialVersionUID = 1L;

	private Color oldBg = getBackground();

	public BooleanEditor(final PropertyWrapper property) {
		super();
		setSelected((Boolean) property.getValue());
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				property.setChanged(true);
				setBackground(oldBg);
				property.setValue(isSelected());
				firePropertyChange(property.getName(), !isSelected(), isSelected());
			}
		});
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}
}
