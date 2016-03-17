package org.diylc.core.components.properties;

import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JComponent;

import org.diylc.core.components.properties.CompositePropertyDescriptor;
import org.diylc.core.components.properties.PropertyEditor;
import org.diylc.core.components.properties.PropertyModel;

/**
 * Interface implemented by a Specification Editor.
 * Look at the DefaultSpecificationEditor for an example, and a class that
 * can be extended to specialize a new Specification Editor.
 * 
 * @author neko
 */
public interface PropertyModelEditor extends ItemListener {

    public void setPropertyDescriptor(CompositePropertyDescriptor specificationProperty);

    public CompositePropertyDescriptor getPropertyDescriptor();

    public void setPropertyEditors(Map<String, PropertyEditor> propertyEditors);

    public void setComponent(JComponent component);

    public void init();

    public PropertyModel getSelectedSpecification();
    
    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    public PropertyChangeListener[] getPropertyChangeListeners();

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName);

}
