package org.diylc.app.dialogs.properties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.swing.JComboBox;

import org.diylc.app.AppIconLoader;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.measures.AbstractMeasure;
import org.diylc.core.measures.Unit;
import org.diylc.core.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasureEditor extends Container {

    private static Logger LOG = LoggerFactory.getLogger(MeasureEditor.class);
    
	private static final long serialVersionUID = 1L;

	private Color oldBg;
	private DoubleTextField valueField;
	private JComboBox<Object> unitBox;

	@SuppressWarnings("unchecked")
	public MeasureEditor(final PropertyWrapper property) {
		setLayout(new BorderLayout());
		
		final AbstractMeasure<?> measure = ((AbstractMeasure<?>) property.getValue());
		
		valueField = new DoubleTextField(measure == null ? null : measure
				.getValue(), AppIconLoader.Warning.getIcon());
		oldBg = valueField.getBackground();
		valueField.addPropertyChangeListener(DoubleTextField.VALUE_PROPERTY,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						try {
							Constructor<?> ctor = property.getType().getConstructors()[0];
							AbstractMeasure<?> newMeasure = (AbstractMeasure<?>) ctor.newInstance((Double) evt.getNewValue(), unitBox.getSelectedItem());
							property.setValue(newMeasure);
							property.setChanged(true);
							valueField.setBackground(oldBg);
							unitBox.setBackground(oldBg);
						} catch (Exception e) {
						    LOG.warn("Error handling property change!", e);
						}
					}
				});
		add(valueField, BorderLayout.CENTER);
		try {
			Type type = ((ParameterizedType) property.getType().getGenericSuperclass()).getActualTypeArguments()[0];
			Method method = ((Class<?>) type).getMethod("values");
			unitBox = new JComboBox<Object>((Object[]) method.invoke(null));
			unitBox.setSelectedItem(measure == null ? null : measure.getUnit());
			unitBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent evt) {
					try {
						Constructor<?> ctor = property.getType().getConstructors()[0];
						AbstractMeasure<?> newMeasure = (AbstractMeasure<?>) ctor.newInstance(valueField.getValue(), (Enum<? extends Unit>) unitBox.getSelectedItem());
						property.setValue(newMeasure);
						property.setChanged(true);
						valueField.setBackground(oldBg);
						unitBox.setBackground(oldBg);
					} catch (Exception e) {
                        LOG.warn("Error handling property change!", e);
					}
				}
			});
			
			add(unitBox, BorderLayout.EAST);

			if (!property.isUnique()) {
				valueField.setBackground(Constants.MULTI_VALUE_COLOR);
				unitBox.setBackground(Constants.MULTI_VALUE_COLOR);
			}
		} catch (Exception e) {
            LOG.warn("Error creating ComboBox!", e);
		}
	}

	@Override
	public void requestFocus() {
		this.valueField.requestFocus();
	}

	@Override
	public boolean requestFocusInWindow() {
		return this.valueField.requestFocusInWindow();
	}

	@Override
	public synchronized void addKeyListener(KeyListener l) {
		this.valueField.addKeyListener(l);
		this.unitBox.addKeyListener(l);
	}
}
