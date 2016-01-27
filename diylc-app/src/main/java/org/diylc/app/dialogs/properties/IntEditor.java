package org.diylc.app.dialogs.properties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.diylc.app.utils.AppIconLoader;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.Constants;

public class IntEditor extends DoubleTextField {

	private static final long serialVersionUID = 1L;

	private Color oldBg = getBackground();

    private PropertyWrapper property;

    private Integer oldValue;

	public IntEditor(final PropertyWrapper property) {
		super(AppIconLoader.Warning.getIcon());
        this.property = property;
        this.oldValue = (Integer) property.getValue();
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
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateValue();
            }
        });
        addFocusListener(new FocusAdapter() {
            
            @Override
            public void focusLost(FocusEvent e) {
                updateValue();
            }
        });
		if (!property.isUnique()) {
			setBackground(Constants.MULTI_VALUE_COLOR);
		}
	}

    protected void updateValue() {
        Integer newValue = (Integer) property.getValue();
        firePropertyChange(property.getName(), oldValue, newValue);
        oldValue = newValue;
    }
}
