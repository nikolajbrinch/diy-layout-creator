package org.diylc.components.passive

import java.awt.Color
import java.awt.Shape
import java.awt.geom.Ellipse2D

import org.diylc.components.AbstractRadialComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.core.CreationMethod
import org.diylc.core.IDIYComponent
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.annotations.PositiveMeasureValidator
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Capacitance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Ceramic Capacitor (radial)", author = "Branislav Stojkovic", category = "Passive", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "C", description = "Standard radial ceramic capacitor", zOrder = IDIYComponent.COMPONENT)
public class RadialCeramicDiskCapacitor extends AbstractRadialComponent<Capacitance> {

	private static final long serialVersionUID = 1L

	public static Size DEFAULT_WIDTH = new Size(1d / 4, SizeUnit.in)
	public static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in)
	public static Color BODY_COLOR = Color.decode("#F0E68C")
	public static Color BORDER_COLOR = BODY_COLOR.darker()

	private Capacitance value = null
	@Deprecated
	private Voltage voltage = Voltage._63V
	private Voltage voltageNew = null

	public RadialCeramicDiskCapacitor() {
		super()
		this.bodyColor = BODY_COLOR
		this.borderColor = BORDER_COLOR
	}

	@EditableProperty(validatorClass = PositiveMeasureValidator.class)
	public Capacitance getValue() {
		return value
	}

	public void setValue(Capacitance value) {
		this.value = value
	}
	
	@Override
	public String getValueForDisplay() {
		return getValue().toString() + (getVoltageNew() == null ? "" : " " + getVoltageNew().toString())
	}
	
	@EditableProperty(name = "Voltage")
	public Voltage getVoltageNew() {
		return voltageNew
	}

	public void setVoltageNew(Voltage voltageNew) {
		this.voltageNew = voltageNew
	}
	
	@Deprecated
	public Voltage getVoltage() {
		return voltage
	}
	
	public void setVoltage(Voltage voltage) {
		this.voltage = voltage
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
		graphicsContext.setColor(LEAD_COLOR_ICON)
		graphicsContext.drawLine(0, height / 2, width, height / 2)
		graphicsContext.setColor(BODY_COLOR)
		graphicsContext.fillOval(4, height / 2 - 3, width - 8, 6)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawOval(4, height / 2 - 3, width - 8, 6)
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
		return new Ellipse2D.Double(0f, 0f, getLength().convertToPixels(), getClosestOdd(getWidth()
				.convertToPixels()))
	}
}
