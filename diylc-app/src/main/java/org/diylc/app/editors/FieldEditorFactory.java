package org.diylc.app.editors;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.diylc.core.PropertyWrapper;
import org.diylc.core.measures.AbstractMeasure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldEditorFactory {

	private static final Logger LOG = LoggerFactory.getLogger(FieldEditorFactory.class);

	public static JComponent createFieldEditor(PropertyWrapper property) {
		if (property.getType().equals(String.class)) {
			StringEditor editor = new StringEditor(property);
			return editor;
		}
		if (property.getType().equals(Color.class)) {
			ColorEditor editor = new ColorEditor(property);
			return editor;
		}
		if (AbstractMeasure.class.isAssignableFrom(property.getType())) {
			MeasureEditor editor = new MeasureEditor(property);
			return editor;
		}
		if (ImageIcon.class.isAssignableFrom(property.getType())) {
			ImageEditor editor = new ImageEditor(property);
			return editor;
		}
		if (property.getType().isEnum()) {
			EnumEditor editor = new EnumEditor(property);
			return editor;
		}
		if (Byte.class.isAssignableFrom(property.getType())
				|| byte.class.isAssignableFrom(property.getType())) {
			ByteEditor editor = new ByteEditor(property);
			return editor;
		}
		if (Boolean.class.isAssignableFrom(property.getType())
				|| boolean.class.isAssignableFrom(property.getType())) {
			BooleanEditor editor = new BooleanEditor(property);
			return editor;
		}
		if (Font.class.isAssignableFrom(property.getType())) {
			FontEditor editor = new FontEditor(property);
			return editor;
		}
		if (Integer.class.isAssignableFrom(property.getType())
				|| int.class.isAssignableFrom(property.getType())) {
			IntEditor editor = new IntEditor(property);
			return editor;
		}
		LOG.error("Unrecognized parameter type: " + property.getType().getName());
		return new JLabel("Unrecognized");
	}
}
