package org.diylc.core.components.properties;

import javax.swing.JComponent;

/**
 * A PropertyEditor contains the PropertyWrapper (that wraps the property, in a
 * JavaBeans way, with getter and setter access) and the corresponding
 * JComponent, for editing the property value.
 * 
 * @author neko
 */
public class PropertyEditor {

    private final PropertyDescriptor property;

    private final JComponent editor;

    public PropertyEditor(PropertyDescriptor property, JComponent editor) {
        this.editor = editor;
        this.property = property;
    }

    public PropertyDescriptor getProperty() {
        return property;
    }

    public JComponent getEditor() {
        return editor;
    }

}
