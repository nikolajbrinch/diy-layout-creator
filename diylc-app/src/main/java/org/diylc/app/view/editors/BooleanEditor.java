package org.diylc.app.view.editors;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.Constants;

public class BooleanEditor extends JCheckBox {

	private static final long serialVersionUID = 1L;

	private Color oldBg = getBackground();

    private PropertyWrapper property;

	public BooleanEditor(final PropertyWrapper property) {
		super();
        this.property = property;
		setSelected((Boolean) property.getValue());
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				BooleanEditor.this.property.setChanged(true);
				setBackground(oldBg);
				BooleanEditor.this.property.setValue(isSelected());
				firePropertyChange(BooleanEditor.this.property.getName(), !isSelected(), isSelected());
			}
		});
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}
}
