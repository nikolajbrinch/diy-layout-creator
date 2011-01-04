package org.diylc.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.diylc.core.ComponentState;
import org.diylc.core.IComponentInstance;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.BomName;
import org.diylc.core.annotations.BomValue;
import org.diylc.core.annotations.ControlPoint;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.measures.Capacitance;
import org.diylc.core.measures.CapacitanceUnit;
import org.diylc.core.measures.Resistance;
import org.diylc.core.measures.ResistanceUnit;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;


public class MockComponentInstance implements IComponentInstance {

	private static final long serialVersionUID = 1L;

	private String testField = "Hello World";
	private Capacitance c = new Capacitance(100d, CapacitanceUnit.uF);
	private Resistance r = new Resistance(123d, ResistanceUnit.K);
	private Size s = new Size(1d, SizeUnit.cm);
	private Color color = Color.green;
	private Point leftTopCorner = new Point(0, 0);
	private String name = "something";

	public MockComponentInstance(String testField, Capacitance c, Resistance r, Size s,
			Color color, Point leftTopCorner) {
		super();
		this.testField = testField;
		this.c = c;
		this.r = r;
		this.s = s;
		this.color = color;
		this.leftTopCorner = leftTopCorner;
	}

	public MockComponentInstance() {
		super();
	}

	@BomName
	@EditableProperty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ControlPoint(visibilityPolicy = VisibilityPolicy.WHEN_SELECTED)
	public Point getLeftTopCorner() {
		return leftTopCorner;
	}

	public void setLeftTopCorner(Point leftTopCorner) {
		this.leftTopCorner = leftTopCorner;
	}

	@EditableProperty(name = "Test Field")
	public String getTestField() {
		return testField;
	}

	public void setTestField(String testField) {
		this.testField = testField;
	}

	@BomValue
	@EditableProperty(defaultable = true)
	public Capacitance getC() {
		return c;
	}

	public void setC(Capacitance c) {
		this.c = c;
	}

	@EditableProperty(defaultable = true)
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@EditableProperty
	public Resistance getR() {
		return r;
	}

	// Setter is commented out, ClassProcessor should ignore this property.
	// public void setR(Resistance r) {
	// this.r = r;
	// }

	// No annotation, so this property should be ignored.
	public Size getS() {
		return s;
	}

	public void setS(Size s) {
		this.s = s;
	}

	@Override
	public void draw(Graphics2D g2d, ComponentState componentState) {
		g2d.setColor(componentState.equals(ComponentState.SELECTED) ? color : color.darker());
		g2d.fillRect(leftTopCorner.x, leftTopCorner.y, 200, 50);
	}

	@Override
	public MockComponentInstance clone() throws CloneNotSupportedException {
		MockComponentInstance newInstance = new MockComponentInstance(testField, c.clone(), r
				.clone(), s.clone(), color, (Point) leftTopCorner.clone());
		return newInstance;
	}
}
