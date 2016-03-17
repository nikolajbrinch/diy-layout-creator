package org.diylc.components.passive

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Shape
import java.awt.geom.RoundRectangle2D

import org.diylc.components.AbstractRadialComponent
import org.diylc.core.components.annotations.ComponentAutoEdit;
import org.diylc.core.components.annotations.ComponentCreationMethod;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.CreationMethod
import org.diylc.core.components.IDIYComponent
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.components.properties.PositiveMeasureValidator
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Capacitance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentAutoEdit
@ComponentCreationMethod(CreationMethod.POINT_BY_POINT)
@ComponentDescriptor(name = "Film Capacitor (radial)", author = "Branislav Stojkovic", category = "Passive", instanceNamePrefix = "C", description = "Radial film capacitor, similar to Sprague Orange Drop")
public class RadialFilmCapacitor extends AbstractRadialComponent {

    public static final String id = "73226f94-48d4-4b8b-88a6-9de4652740b8"
    
	private static final long serialVersionUID = 1L

	private static Size DEFAULT_WIDTH = new Size(1d / 4, SizeUnit.in)
	
    private static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in)
	
    private static Color BODY_COLOR = Color.decode("#FF8000")
	
    private static Color BORDER_COLOR = BODY_COLOR.darker()

    @EditableProperty(validatorClass = PositiveMeasureValidator.class)
	Capacitance value = null
	
    @Deprecated
	Voltage voltage = Voltage._63V

    @EditableProperty(name = "Voltage")
	org.diylc.core.measures.Voltage voltageNew = null

	public RadialFilmCapacitor() {
		super()
		this.bodyColor = BODY_COLOR
		this.borderColor = BORDER_COLOR
	}

	@Override
	public String getValueForDisplay() {
		return getValue().toString() + (getVoltageNew() == null ? "" : " " + getVoltageNew().toString())
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
		graphicsContext.setColor(Colors.LEAD_COLOR_ICON)
		graphicsContext.drawLine(0, height / 2, width, height / 2)
		graphicsContext.setColor(BODY_COLOR)
		graphicsContext.fillRoundRect(4, height / 2 - 3, width - 8, 6, 5, 5)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRoundRect(4, height / 2 - 3, width - 8, 6, 5, 5)
	}

	@Override
	protected Size getDefaultWidth() {
		return DEFAULT_HEIGHT
	}

	@Override
	protected Size getDefaultLength() {
		return DEFAULT_WIDTH
	}

	@Override
	protected Shape getBodyShape() {
		double radius = getWidth().convertToPixels() * 0.7
		return new RoundRectangle2D.Double(0f, 0f, getLength()
				.convertToPixels(),
				getClosestOdd(getWidth().convertToPixels()), radius, radius)
	}
}
