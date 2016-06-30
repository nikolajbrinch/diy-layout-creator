package org.diylc.specifications;

import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.diylc.core.PropertyWrapper;
import org.diylc.core.utils.ReflectionUtils;

public class DefaultSpecificationEditor<T extends Specification> implements SpecificationEditor {

    private final PropertyChangeSupport propertyChangeSupport;

    private SpecificationProperty property;

    private final Class<T> propertyType;

    private JComponent component;

    public DefaultSpecificationEditor(Class<T> propertyType) {
        this.propertyType = propertyType;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void init() {
    }

    public JComponent getComponent() {
        return component;
    }

    public void setComponent(JComponent component) {
        this.component = component;
    }

    public SpecificationProperty getSpecificationProperty() {
        return property;
    }

    public void setSpecificationProperty(SpecificationProperty property) {
        this.property = property;
    }

    public Map<String, PropertyEditor> getPropertyEditors() {
        return propertyEditors;
    }

    public void setPropertyEditors(Map<String, PropertyEditor> propertyEditors) {
        this.propertyEditors = propertyEditors;
    }

    private Map<String, PropertyEditor> propertyEditors = new HashMap<>();

    @Override
    public void itemStateChanged(ItemEvent event) {
        Object item = event.getItem();
        int stateChange = event.getStateChange();

        if (stateChange == ItemEvent.SELECTED) {
            if (item instanceof CustomSpecification) {
                customSelected();
            } else {
                itemSelected(ReflectionUtils.dynamicCast(item, propertyType));
            }
        } else if (stateChange == ItemEvent.DESELECTED) {
            if (item instanceof CustomSpecification) {
                customDeselected();
            } else {
                itemDeselected(ReflectionUtils.dynamicCast(item, propertyType));
            }
        }
    }

    protected void setEnabled(String propertyName, boolean enabled) {
        PropertyEditor propertyEditor = getPropertyEditors().get(propertyName);
        propertyEditor.getEditor().setEnabled(enabled);
    }

    protected Object getValue(String propertyName) {
        PropertyEditor propertyEditor = getPropertyEditors().get(propertyName);
        return propertyEditor.getProperty().getValue();
    }

    protected void setValue(String propertyName, Object value) {
        PropertyEditor propertyEditor = getPropertyEditors().get(propertyName);
        propertyEditor.getProperty().setValue(value);
    }

    @SuppressWarnings("unchecked")
    protected <E extends JComponent> E getEditor(String propertyName, Class<E> componentClass) {
        return (E) getPropertyEditors().get(propertyName).getEditor();
    }

    protected PropertyWrapper getProperty(String propertyName) {
        return getPropertyEditors().get(propertyName).getProperty();
    }
    
    @Override
    public Specification getSelectedSpecification() {
        return property.getSpecifications().get(0);
    }

    public void customSelected() {
    }

    public void itemSelected(T item) {
    }

    public void customDeselected() {
    }

    public void itemDeselected(T item) {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return propertyChangeSupport.getPropertyChangeListeners(propertyName);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

}
