package org.diylc.specifications;

import javax.swing.JComponent;

import org.diylc.core.PropertyWrapper;

/**
 * A PropertyEditor contains the PropertyWrapper (that wraps the property, in a
 * JavaBeans way, with getter and setter access) and the corresponding
 * JComponent, for editing the property value.
 * 
 * @author neko
 */
public class PropertyEditor {

    private final PropertyWrapper property;

    private final JComponent editor;

    public PropertyEditor(PropertyWrapper property, JComponent editor) {
        this.editor = editor;
        this.property = property;
    }

    public PropertyWrapper getProperty() {
        return property;
    }

    public JComponent getEditor() {
        return editor;
    }

}
