package org.diylc.components;

import java.awt.Point;

import org.diylc.core.Orientation;
import org.diylc.core.components.VisibilityPolicy;
import org.diylc.core.components.properties.EditableProperty;

import org.diylc.core.components.properties.PositiveMeasureValidator;
import org.diylc.core.measures.Resistance;

public abstract class AbstractPotentiometer extends
		AbstractTransparentComponent {

	private static final long serialVersionUID = 1L;

	protected Point[] controlPoints;

	protected Resistance resistance = null;
	protected Orientation orientation = Orientation.DEFAULT;
	protected Taper taper = Taper.LIN;

	@Override
	public int getControlPointCount() {
		return controlPoints.length;
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoints[index];
	}

	@Override
	public void setControlPoint(Point point, int index) {
		controlPoints[index].setLocation(point);
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.NEVER;
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return true;
	}

	@Override
	public String getValueForDisplay() {
		return (resistance == null ? "" : resistance.toString()) + " "
				+ taper.toString();
	}

	@EditableProperty(validatorClass = PositiveMeasureValidator.class)	
	public Resistance getValue() {
		return resistance;
	}

	public void setValue(Resistance value) {
		this.resistance = value;
	}

	@EditableProperty
	public Taper getTaper() {
		return taper;
	}

	public void setTaper(Taper taper) {
		this.taper = taper;
	}

	@EditableProperty
	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
}
