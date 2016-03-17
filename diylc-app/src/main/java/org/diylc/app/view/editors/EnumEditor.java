package org.diylc.app.view.editors;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import org.diylc.core.components.properties.PropertyDescriptor;

import org.diylc.core.utils.Constants;

public class EnumEditor extends JComboBox<Object> {

	private static final long serialVersionUID = 1L;

	private Color oldBg = getBackground();

	private final PropertyDescriptor property;

	public EnumEditor(final PropertyDescriptor property) {
		this.property = property;
		Object[] values = property.getType().getEnumConstants();
		setModel(new DefaultComboBoxModel<Object>(values));
		setSelectedItem(property.getValue());
		addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
			    Object oldValue = property.getValue();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					EnumEditor.this.property.setChanged(true);
					setBackground(oldBg);
					EnumEditor.this.property.setValue(e.getItem());
					firePropertyChange(EnumEditor.this.property.getName(), oldValue, EnumEditor.this.property.getValue());
				}
			}
		});
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}
}
