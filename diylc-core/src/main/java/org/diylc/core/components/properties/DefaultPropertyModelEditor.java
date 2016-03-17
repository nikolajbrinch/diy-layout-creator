package org.diylc.core.components.properties;

import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.diylc.core.components.CustomPropertyModel;

public class DefaultPropertyModelEditor<T extends PropertyModel> implements PropertyModelEditor {

    private final PropertyChangeSupport propertyChangeSupport;

    private CompositePropertyDescriptor propertyDescriptor;

    private final Class<T> propertyType;

    private JComponent component;

    public DefaultPropertyModelEditor(Class<T> propertyType) {
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

    public CompositePropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public void setPropertyDescriptor(CompositePropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
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
            if (item instanceof CustomPropertyModel) {
                customSelected();
            } else {
                itemSelected((T) item);
//                itemSelected(ReflectionUtils.dynamicCast(item, propertyType));
            }
        } else if (stateChange == ItemEvent.DESELECTED) {
            if (item instanceof CustomPropertyModel) {
                customDeselected();
            } else {
                itemDeselected((T) item);
//                itemDeselected(ReflectionUtils.dynamicCast(item, propertyType));
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

    protected PropertyDescriptor getProperty(String propertyName) {
        return getPropertyEditors().get(propertyName).getProperty();
    }
    
    @Override
    public PropertyModel getSelectedSpecification() {
        return propertyDescriptor.getPropertyModels().get(0);
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
    
    public Object getPropertyValue() {
        return propertyDescriptor.getValue();
    }

}
