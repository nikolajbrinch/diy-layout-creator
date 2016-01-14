package org.diylc.components.tube

import org.diylc.components.Colors

import java.awt.Graphics2D
import java.awt.Point
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath

import org.diylc.common.ObjectCache
import org.diylc.components.AbstractTubeSymbol
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.core.IDIYComponent
import org.diylc.core.IPropertyValidator
import org.diylc.core.ValidationException
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "Diode Symbol", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "V", description = "Diode tube symbol", stretchable = false, zOrder = IDIYComponent.COMPONENT, rotatable = false)
public class DiodeSymbol extends AbstractTubeSymbol implements Geometry {

	private static final long serialVersionUID = 1L

	protected Point[] controlPoints = points(point(0, 0),
			point(0, 0), point(0, 0), point(0, 0), point(0, 0))

    @EditableProperty(name = "Directly heated")
	boolean directlyHeated = false

	public DiodeSymbol() {
		super()
		updateControlPoints()
	}

	public Shape[] getBody() {
		if (this.@body == null) {
			this.@body = new Shape[3]
			int x = controlPoints[0].x
			int y = controlPoints[0].y
			int pinSpacing = (int) PIN_SPACING.convertToPixels()

			// electrodes
			GeneralPath polyline = new GeneralPath()

			// plate
			polyline.moveTo((double) x + pinSpacing * 3 / 2, (double) y - pinSpacing)
			polyline.lineTo((double) x + pinSpacing * 9 / 2, (double) y - pinSpacing)

			// cathode
			if (directlyHeated) {
				polyline.moveTo((double) controlPoints[2].x, (double) controlPoints[2].y
						- pinSpacing)
				polyline.lineTo((double) controlPoints[2].x + pinSpacing,
						(double) controlPoints[2].y - pinSpacing * 2)
				polyline.lineTo((double) controlPoints[4].x, (double) controlPoints[4].y
						- pinSpacing)
			} else {
				polyline.moveTo((double) x + pinSpacing * 2, (double) y + pinSpacing)
				polyline.lineTo((double) x + pinSpacing * 4, (double) y + pinSpacing)
			}

			this.@body[0] = polyline

			// connectors
			polyline = new GeneralPath()

			// plate
			polyline.moveTo((double) controlPoints[1].x, (double) controlPoints[1].y)
			polyline.lineTo((double) x + pinSpacing * 3, (double) y - pinSpacing)

			// cathode
			if (directlyHeated) {
				polyline.moveTo((double) controlPoints[2].x, (double) controlPoints[2].y)
				polyline.lineTo((double) controlPoints[2].x, (double) controlPoints[2].y
						- pinSpacing)

				polyline.moveTo((double) controlPoints[4].x, (double) controlPoints[4].y)
				polyline.lineTo((double) controlPoints[4].x, (double) controlPoints[4].y
						- pinSpacing)
			} else {
				polyline.moveTo((double) controlPoints[2].x, (double) controlPoints[2].y)
				polyline.lineTo(x + pinSpacing * 2, y + pinSpacing)

				if (showHeaters) {
					polyline.moveTo((double) controlPoints[3].x, (double) controlPoints[3].y)
					polyline.lineTo((double) controlPoints[3].x, (double) controlPoints[3].y
							- pinSpacing)
					polyline.lineTo((double) controlPoints[3].x + pinSpacing / 2,
							(double) controlPoints[3].y - 3 * pinSpacing / 2)

					polyline.moveTo((double) controlPoints[4].x, (double) controlPoints[4].y)
					polyline.lineTo((double) controlPoints[4].x, (double) controlPoints[4].y
							- pinSpacing)
					polyline.lineTo((double) controlPoints[4].x - pinSpacing / 2,
							(double) controlPoints[4].y - 3 * pinSpacing / 2)
				}
			}

			this.@body[1] = polyline

			// bulb
			this.@body[2] = new Ellipse2D.Double(x + pinSpacing / 2, y - pinSpacing
					* 5 / 2, pinSpacing * 5, pinSpacing * 5)
		}
		return this.@body
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setColor(Colors.SCHEMATIC_COLOR)

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))

		graphicsContext.drawLine(width / 4, height / 4, width * 3 / 4, height / 4)
		graphicsContext.drawLine(width / 2, height / 4, width / 2, 0)

		graphicsContext.drawLine(width / 4 + 2 * width / 32, height * 3 / 4, width * 3 / 4
				- 4 * width / 32, height * 3 / 4)
		graphicsContext.drawLine(width / 4 + 2 * width / 32, height * 3 / 4, width / 4 + 2
				* width / 32, height - 1)

		graphicsContext.drawOval(1, 1, width - 1 - 2 * width / 32, height - 1 - 2 * width / 32)
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoints[index]
	}

	@Override
	public int getControlPointCount() {
		return controlPoints.length
	}

	protected void updateControlPoints() {
		int pinSpacing = (int) PIN_SPACING.convertToPixels()
		// Update control points.
		int x = controlPoints[0].x
		int y = controlPoints[0].y

		controlPoints[1].x = x + pinSpacing * 3
		controlPoints[1].y = y - pinSpacing * 3

		controlPoints[2].x = x + pinSpacing * 2
		controlPoints[2].y = y + pinSpacing * 3

		controlPoints[3].x = x + pinSpacing * 3
		controlPoints[3].y = y + pinSpacing * 3

		controlPoints[4].x = x + pinSpacing * 4
		controlPoints[4].y = y + pinSpacing * 3
	}

	@Override
	public void setControlPoint(Point point, int index) {
		controlPoints[index].setLocation(point)
		// Invalidate body
		body = null
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		if (directlyHeated) {
			return index > 0 && index != 3 ? VisibilityPolicy.WHEN_SELECTED
					: VisibilityPolicy.NEVER
		} else if (showHeaters) {
			return index > 0 ? VisibilityPolicy.WHEN_SELECTED
					: VisibilityPolicy.NEVER
		} else {
			return index < 3 && index > 0 ? VisibilityPolicy.WHEN_SELECTED
					: VisibilityPolicy.NEVER
		}
	}

	@Override
	public boolean isControlPointSticky(int index) {
		if (directlyHeated)
			return index > 0 && index != 3
		else if (showHeaters) {
			return index > 0
		} else {
			return index > 0 && index < 3
		}
	}

	@Override
	protected Point getTextLocation() {
		int pinSpacing = (int) PIN_SPACING.convertToPixels()
		return point(controlPoints[0].x + pinSpacing * 5,
				controlPoints[0].y + pinSpacing * 2)
	}

	public void setDirectlyHeated(boolean directlyHeated) {
		this.@directlyHeated = directlyHeated
		// Invalidate body
		body = null
	}

	public class HeaterValidator implements IPropertyValidator {

		@Override
		public void validate(Object value) throws ValidationException {
			if (value != null && value instanceof Boolean) {
				boolean b = (Boolean) value
				if (!b && getDirectlyHeated()) {
					throw new ValidationException(
							"Must show heaters for directly heated tubes.")
				}
			}
		}
	}
}
