package org.diylc.app.view.editors;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.diylc.core.components.properties.PropertyDescriptor;

import org.diylc.core.components.properties.PropertyModelEditor;
import org.diylc.core.components.properties.CompositePropertyDescriptor;
import org.diylc.core.measures.AbstractMeasure;
import org.diylc.core.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldEditorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(FieldEditorFactory.class);

    public static JComponent createFieldEditor(PropertyDescriptor propertyDescriptor, CompositePropertyDescriptor compositePropertyDescriptor) {
        if (propertyDescriptor.getType().equals(String.class)) {
            StringEditor editor = new StringEditor(propertyDescriptor);
            return editor;
        }
        if (propertyDescriptor.getType().equals(Color.class)) {
            ColorEditor editor = new ColorEditor(propertyDescriptor);
            return editor;
        }
        if (AbstractMeasure.class.isAssignableFrom(propertyDescriptor.getType())) {
            MeasureEditor editor = new MeasureEditor(propertyDescriptor);
            return editor;
        }
        if (ImageIcon.class.isAssignableFrom(propertyDescriptor.getType())) {
            ImageEditor editor = new ImageEditor(propertyDescriptor);
            return editor;
        }
        if (propertyDescriptor.getType().isEnum()) {
            EnumEditor editor = new EnumEditor(propertyDescriptor);
            return editor;
        }
        if (Byte.class.isAssignableFrom(propertyDescriptor.getType()) || byte.class.isAssignableFrom(propertyDescriptor.getType())) {
            ByteEditor editor = new ByteEditor(propertyDescriptor);
            return editor;
        }
        if (Boolean.class.isAssignableFrom(propertyDescriptor.getType()) || boolean.class.isAssignableFrom(propertyDescriptor.getType())) {
            BooleanEditor editor = new BooleanEditor(propertyDescriptor);
            return editor;
        }
        if (Font.class.isAssignableFrom(propertyDescriptor.getType())) {
            JComponent editor = Platform.getPlatform().createFontEditor(propertyDescriptor);
            return editor;
        }
        if (Integer.class.isAssignableFrom(propertyDescriptor.getType()) || int.class.isAssignableFrom(propertyDescriptor.getType())) {
            IntEditor editor = new IntEditor(propertyDescriptor);
            return editor;
        }
        if (compositePropertyDescriptor != null) {
            if (compositePropertyDescriptor.getSpecificationType().isAssignableFrom(propertyDescriptor.getType())) {
                Class<? extends PropertyModelEditor> specificationEditorClass = compositePropertyDescriptor.getSpecificationEditor();
                
                PropertyModelEditor propertyModelEditor = null;
                
                try {
                    propertyModelEditor = specificationEditorClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    LOG.error("Unable to instantiate editor for specification: " + compositePropertyDescriptor);
                }
                
                propertyModelEditor.setPropertyDescriptor(compositePropertyDescriptor);
                
                SpecificationComboBoxEditor editor = new SpecificationComboBoxEditor(compositePropertyDescriptor.getPropertyModels(), propertyModelEditor);
                return editor;
            }
        }

        LOG.error("Unrecognized parameter type: " + propertyDescriptor.getType().getName());
        return new JLabel("Unrecognized");
    }
}
