package org.diylc.specifications;

import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.JComponent;

/**
 * Interface implemented by a Specification Editor.
 * Look at the DefaultSpecificationEditor for an example, and a class that
 * can be extended to specialize a new Specification Editor.
 * 
 * @author neko
 */
public interface SpecificationEditor extends ItemListener {

    public void setSpecificationProperty(SpecificationProperty specificationProperty);

    public SpecificationProperty getSpecificationProperty();

    public void setPropertyEditors(Map<String, PropertyEditor> propertyEditors);

    public void setComponent(JComponent component);

    public void init();

    public Specification getSelectedSpecification();
    
    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    public PropertyChangeListener[] getPropertyChangeListeners();

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName);

}
