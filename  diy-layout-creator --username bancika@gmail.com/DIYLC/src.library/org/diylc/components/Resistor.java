package org.diylc.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.measures.Resistance;
import org.diylc.core.measures.ResistanceUnit;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

@ComponentDescriptor(name = "Resistor", author = "bancika", category = "Passive", instanceNamePrefix = "R", desciption = "test")
public class Resistor extends AbstractLeadedDIYComponent {

	private static final long serialVersionUID = 1L;

	public static Size DEFAULT_WIDTH = new Size(1d / 2, SizeUnit.in);
	public static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in);
	public static Color BODY_COLOR = Color.decode("#82CFFD");
	public static Color BORDER_COLOR = Color.decode("#5D92B1");

	private Resistance r = new Resistance(123d, ResistanceUnit.K);

	public Resistor() {
		super();
	}

	@EditableProperty
	public Resistance getR() {
		return r;
	}

	public void drawIcon(Graphics2D g2d, int width, int height) {
		g2d.drawString("R", 10, 10);
	}

	@Override
	protected Size getDefaultHeight() {
		return DEFAULT_HEIGHT;
	}

	@Override
	protected Size getDefaultWidth() {
		return DEFAULT_WIDTH;
	}

	@Override
	protected Color getBodyColor() {
		return BODY_COLOR;
	}

	@Override
	protected Color getBorderColor() {
		return BORDER_COLOR;
	}

	@Override
	protected Shape getComponentShape() {
		return new Rectangle2D.Double(0f, 0f, getWidth().convertToPixels(), getHeight()
				.convertToPixels());
	}
}
