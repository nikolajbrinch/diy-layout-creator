package org.diylc.app.view.editors

import groovy.transform.CompileStatic

import java.beans.PropertyChangeListener
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox

import org.diylc.core.components.properties.PropertyModel;
import org.diylc.core.components.properties.PropertyModelEditor

@CompileStatic
class SpecificationComboBoxEditor extends JComboBox<PropertyModel> {

    PropertyModelEditor specificationEditor

    public SpecificationComboBoxEditor(List<PropertyModel> specifications, PropertyModelEditor specificationEditor) {
        this.specificationEditor = specificationEditor
        setModel(new DefaultComboBoxModel<PropertyModel>(specifications.toArray(new PropertyModel[0])))
        setSelectedItem(specificationEditor.getSelectedSpecification())
        addItemListener(specificationEditor)
        specificationEditor.setComponent(this)
    }
    
    

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener)
        if (specificationEditor != null) {
            specificationEditor.addPropertyChangeListener(listener)
        }
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.addPropertyChangeListener(propertyName, listener)
        if (specificationEditor != null) {
            specificationEditor.addPropertyChangeListener(propertyName, listener)
        }
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(listener)
        if (specificationEditor != null) {
            specificationEditor.removePropertyChangeListener(listener)
        }
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        super.removePropertyChangeListener(propertyName, listener)
        if (specificationEditor != null) {
            specificationEditor.removePropertyChangeListener(propertyName, listener)
        }
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return super.getPropertyChangeListeners()
    }

    @Override
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return super.getPropertyChangeListeners(propertyName)
    }
}
