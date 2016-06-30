package org.diylc.app.view.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.diylc.app.utils.AppIconLoader;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.measures.AbstractMeasure;
import org.diylc.core.measures.Unit;
import org.diylc.core.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeasureEditor extends JPanel {

    private static Logger LOG = LoggerFactory.getLogger(MeasureEditor.class);

    private static final long serialVersionUID = 1L;

    private Color oldBg;
    
    private DoubleTextField valueField;
    
    private JComboBox<Object> unitBox;

    private PropertyWrapper property;

    private AbstractMeasure<?> oldValue;

    @SuppressWarnings("unchecked")
    public MeasureEditor(final PropertyWrapper property) {
        this.property = property;
        setLayout(new BorderLayout());

        final AbstractMeasure<?> measure = ((AbstractMeasure<?>) property.getValue());

        valueField = new DoubleTextField(measure == null ? null : measure.getValue(), AppIconLoader.Warning.getIcon());
        this.oldValue = measure;
        oldBg = valueField.getBackground();
        valueField.addPropertyChangeListener(DoubleTextField.VALUE_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    Constructor<?> ctor = MeasureEditor.this.property.getType().getConstructors()[0];
                    AbstractMeasure<?> newMeasure = (AbstractMeasure<?>) ctor.newInstance((Double) evt.getNewValue(),
                            unitBox.getSelectedItem());
                    MeasureEditor.this.property.setValue(newMeasure);
                    MeasureEditor.this.property.setChanged(true);
                    valueField.setBackground(oldBg);
                    unitBox.setBackground(oldBg);
                } catch (Exception e) {
                    LOG.warn("Error handling property change!", e);
                }
            }
        });
        valueField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateValue();
            }
        });
        valueField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                if (unitBox.getSelectedItem() == null) {
                    unitBox.setSelectedIndex(0);
                }
                updateValue();
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
                        oldValue = (AbstractMeasure<?>) MeasureEditor.this.property.getValue();
                        Constructor<?> ctor = MeasureEditor.this.property.getType().getConstructors()[0];
                        AbstractMeasure<?> newMeasure = (AbstractMeasure<?>) ctor.newInstance(valueField.getValue(),
                                (Enum<? extends Unit>) unitBox.getSelectedItem());
                        MeasureEditor.this.property.setValue(newMeasure);
                        MeasureEditor.this.property.setChanged(true);
                        valueField.setBackground(oldBg);
                        unitBox.setBackground(oldBg);
                        firePropertyChange(MeasureEditor.this.property.getName(), oldValue, MeasureEditor.this.property.getValue());
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

    protected void updateValue() {
        AbstractMeasure<?> newValue = (AbstractMeasure<?>) property.getValue();
        firePropertyChange(property.getName(), oldValue, newValue);
        oldValue = newValue;
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
