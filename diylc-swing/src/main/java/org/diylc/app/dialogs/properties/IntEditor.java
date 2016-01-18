package org.diylc.app.dialogs.properties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.diylc.app.AppIconLoader;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.Constants;

public class IntEditor extends DoubleTextField {

	private static final long serialVersionUID = 1L;

	private Color oldBg = getBackground();

	public IntEditor(final PropertyWrapper property) {
		super(AppIconLoader.Warning.getIcon());
		setLayout(new BorderLayout());
		setValue((double) (Integer) property.getValue());
		addPropertyChangeListener(DoubleTextField.VALUE_PROPERTY, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				property.setChanged(true);
				setBackground(oldBg);
				property.setValue(((Double) evt.getNewValue()).intValue());
			}
		});
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}
}
