package org.diylc.components;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;

import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

public abstract class AbstractDiodeSymbol extends AbstractSchematicLeadedSymbol {

	private static final long serialVersionUID = 1L;

	public static Size DEFAULT_SIZE = new Size(0.1, SizeUnit.in);	

	private String value = null;

	public AbstractDiodeSymbol() {
		super();
		this.bodyColor = Colors.SCHEMATIC_COLOR;
		this.borderColor = null;
	}

	@EditableProperty
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValueForDisplay() {
		return getValue();
	}

	@Override
	protected Size getDefaultWidth() {
		return DEFAULT_SIZE;
	}

	@Override
	protected Size getDefaultLength() {
		return DEFAULT_SIZE;
	}

	@Override
	protected Shape getBodyShape() {
		double width = getWidth().convertToPixels();
		Polygon p = new Polygon(
				new int[] { 0, 0, (int) (width / Math.sqrt(2)) }, new int[] {
						0, (int) (width), (int) (width / 2) }, 3);
		// Area a = new Area(p);
		// int bandSize = (int) BAND_SIZE.convertToPixels();
		// a.add(new Area(new Rectangle2D.Double((int) (width / Math.sqrt(2)) +
		// 1,
		// 0, bandSize, (int) width)));
		return p;
	}

	@Override
	protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
	}

	@Deprecated
	@Override
	public Size getLength() {
		return super.getLength();
	}

	@Deprecated
	@Override
	public Color getBorderColor() {
		return super.getBorderColor();
	}

	@Override
	@EditableProperty(name = "Color")
	public Color getBodyColor() {
		return super.getBodyColor();
	}
}
