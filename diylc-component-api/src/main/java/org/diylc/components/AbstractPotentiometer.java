package org.diylc.components;

import java.awt.Point;

import org.diylc.core.Orientation;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.annotations.PositiveMeasureValidator;
import org.diylc.core.measures.Resistance;

public abstract class AbstractPotentiometer extends
		AbstractTransparentComponent {

	private static final long serialVersionUID = 1L;

	private Point[] controlPoints;

	protected Resistance resistance = null;
	
	protected Orientation orientation = Orientation.DEFAULT;
	
	protected Taper taper = Taper.LIN;

	@Override
	public int getControlPointCount() {
		return getControlPoints().length;
	}

	@Override
	public Point getControlPoint(int index) {
		return getControlPoints()[index];
	}

	@Override
	public void setControlPoint(Point point, int index) {
		getControlPoints()[index].setLocation(point);
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

    public Point[] getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(Point[] controlPoints) {
        this.controlPoints = controlPoints;
    }
}
