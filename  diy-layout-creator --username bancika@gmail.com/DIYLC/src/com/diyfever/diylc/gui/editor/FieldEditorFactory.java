package com.diyfever.diylc.gui.editor;

import java.awt.Color;
import java.awt.Component;

import com.diyfever.diylc.common.PropertyWrapper;
import com.diyfever.diylc.model.measures.AbstractMeasure;

public class FieldEditorFactory {

	public static Component createFieldEditor(PropertyWrapper property) {
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
		return null;
	}
}
