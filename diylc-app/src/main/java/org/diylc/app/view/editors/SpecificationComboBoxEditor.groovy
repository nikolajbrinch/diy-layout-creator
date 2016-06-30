package org.diylc.app.view.editors

import groovy.transform.CompileStatic

import java.awt.Component
import java.beans.PropertyChangeListener
import java.util.List

import javax.swing.DefaultComboBoxModel
import javax.swing.Icon
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.plaf.basic.BasicComboBoxRenderer

import org.diylc.app.view.editors.FontChooserComboBox.FontChooserComboBoxRenderer
import org.diylc.app.view.editors.FontChooserComboBox.Item
import org.diylc.specifications.Specification
import org.diylc.specifications.SpecificationEditor
import org.diylc.specifications.SpecificationModel

@CompileStatic
class SpecificationComboBoxEditor extends JComboBox<Specification> {

    SpecificationEditor specificationEditor

    public SpecificationComboBoxEditor(List<Specification> specifications, SpecificationEditor specificationEditor) {
        this.specificationEditor = specificationEditor
        setModel(new DefaultComboBoxModel<Specification>(specifications.toArray(new Specification[0])))
        setSelectedItem(specificationEditor.getSelectedSpecification())
        addItemListener(specificationEditor)
        specificationEditor.setComponent(this)
        setMaximumRowCount(20)
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
