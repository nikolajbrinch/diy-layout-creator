package org.diylc.components.passive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.diylc.components.AbstractLeadedComponent;
import org.diylc.core.CreationMethod;
import org.diylc.core.IDIYComponent;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.measures.Capacitance;
import org.diylc.core.measures.CapacitanceUnit;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.core.measures.VoltageUnit;

@ComponentDescriptor(name = "Film Capacitor (axial)", author = "Branislav Stojkovic", category = "Passive", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "C", description = "Axial film capacitor, similar to Mallory 150s", zOrder = IDIYComponent.COMPONENT)
public class AxialFilmCapacitor extends AbstractLeadedComponent<Capacitance> {

	private static final long serialVersionUID = 1L;

	public static Size DEFAULT_WIDTH = new Size(1d / 2, SizeUnit.in);
	public static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in);
	public static Color BODY_COLOR = Color.decode("#FFE303");
	public static Color BORDER_COLOR = BODY_COLOR.darker();

	private Capacitance value = new Capacitance(22d, CapacitanceUnit.nF);
	@Deprecated
	private Voltage voltage = Voltage._63V;
	private org.diylc.core.measures.Voltage voltageNew = new org.diylc.core.measures.Voltage(63d,
			VoltageUnit.V);

	public AxialFilmCapacitor() {
		super();
		this.bodyColor = BODY_COLOR;
		this.borderColor = BORDER_COLOR;
	}
	
	@Override
	protected boolean supportsStandingMode() {
		return true;
	}

	@EditableProperty
	public Capacitance getValue() {
		return value;
	}

	public void setValue(Capacitance value) {
		this.value = value;
	}
	
	@Deprecated
	public Voltage getVoltage() {
		return voltage;
	}

	@Deprecated
	public void setVoltage(Voltage voltage) {
		this.voltage = voltage;
	}

	@EditableProperty(name = "Voltage")
	public org.diylc.core.measures.Voltage getVoltageNew() {
		// Backward comptibility
		if (voltageNew == null) {
			voltageNew = voltage.convertToNewFormat();
			voltage = null;
			// Clear old value, don't need it anymore
		}
		return voltageNew;
	}

	public void setVoltageNew(org.diylc.core.measures.Voltage voltageNew) {
		this.voltageNew = voltageNew;
	}

	public void drawIcon(Graphics2D g2d, int width, int height) {
		g2d.rotate(-Math.PI / 4, width / 2, height / 2);
		g2d.setColor(LEAD_COLOR);
		g2d.drawLine(0, height / 2, width, height / 2);
		g2d.setColor(BODY_COLOR);
		g2d.fillRect(4, height / 2 - 3, width - 8, 6);
		g2d.setColor(BORDER_COLOR);
		g2d.drawRect(4, height / 2 - 3, width - 8, 6);
	}

	@Override
	protected Size getDefaultWidth() {
		return DEFAULT_HEIGHT;
	}

	@Override
	protected Size getDefaultLength() {
		return DEFAULT_WIDTH;
	}

	@Override
	protected Shape getBodyShape() {
		return new Rectangle2D.Double(0f, 0f, getLength().convertToPixels(),
				getClosestOdd(getWidth().convertToPixels()));
	}
}
