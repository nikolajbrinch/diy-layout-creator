package org.diylc.components.tube

import java.awt.Graphics2D
import java.awt.Point
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath

import org.diylc.common.ObjectCache
import org.diylc.components.AbstractTubeSymbol
import org.diylc.components.ComponentDescriptor
import org.diylc.core.IDIYComponent
import org.diylc.core.IPropertyValidator
import org.diylc.core.ValidationException
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "Diode Symbol", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "V", description = "Diode tube symbol", stretchable = false, zOrder = IDIYComponent.COMPONENT, rotatable = false)
public class DiodeSymbol extends AbstractTubeSymbol {

	private static final long serialVersionUID = 1L

	protected Point[] controlPoints = [ new Point(0, 0),
			new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) ] as Point[]

	protected boolean directlyHeated = false

	public DiodeSymbol() {
		super()
		updateControlPoints()
	}

	public Shape[] getBody() {
		if (body == null) {
			body = new Shape[3]
			int x = controlPoints[0].x
			int y = controlPoints[0].y
			int pinSpacing = (int) PIN_SPACING.convertToPixels()

			// electrodes
			GeneralPath polyline = new GeneralPath()

			// plate
			polyline.moveTo(x + pinSpacing * 3 / 2, y - pinSpacing)
			polyline.lineTo(x + pinSpacing * 9 / 2, y - pinSpacing)

			// cathode
			if (directlyHeated) {
				polyline.moveTo(controlPoints[2].x, controlPoints[2].y
						- pinSpacing)
				polyline.lineTo(controlPoints[2].x + pinSpacing,
						controlPoints[2].y - pinSpacing * 2)
				polyline.lineTo(controlPoints[4].x, controlPoints[4].y
						- pinSpacing)
			} else {
				polyline.moveTo(x + pinSpacing * 2, y + pinSpacing)
				polyline.lineTo(x + pinSpacing * 4, y + pinSpacing)
			}

			body[0] = polyline

			// connectors
			polyline = new GeneralPath()

			// plate
			polyline.moveTo(controlPoints[1].x, controlPoints[1].y)
			polyline.lineTo(x + pinSpacing * 3, y - pinSpacing)

			// cathode
			if (directlyHeated) {
				polyline.moveTo(controlPoints[2].x, controlPoints[2].y)
				polyline.lineTo(controlPoints[2].x, controlPoints[2].y
						- pinSpacing)

				polyline.moveTo(controlPoints[4].x, controlPoints[4].y)
				polyline.lineTo(controlPoints[4].x, controlPoints[4].y
						- pinSpacing)
			} else {
				polyline.moveTo(controlPoints[2].x, controlPoints[2].y)
				polyline.lineTo(x + pinSpacing * 2, y + pinSpacing)

				if (showHeaters) {
					polyline.moveTo(controlPoints[3].x, controlPoints[3].y)
					polyline.lineTo(controlPoints[3].x, controlPoints[3].y
							- pinSpacing)
					polyline.lineTo(controlPoints[3].x + pinSpacing / 2,
							controlPoints[3].y - 3 * pinSpacing / 2)

					polyline.moveTo(controlPoints[4].x, controlPoints[4].y)
					polyline.lineTo(controlPoints[4].x, controlPoints[4].y
							- pinSpacing)
					polyline.lineTo(controlPoints[4].x - pinSpacing / 2,
							controlPoints[4].y - 3 * pinSpacing / 2)
				}
			}

			body[1] = polyline

			// bulb
			body[2] = new Ellipse2D.Double(x + pinSpacing / 2, y - pinSpacing
					* 5 / 2, pinSpacing * 5, pinSpacing * 5)
		}
		return body
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setColor(COLOR)

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))

		graphicsContext.drawLine(width / 4, height / 4, width * 3 / 4, height / 4)
		graphicsContext.drawLine(width / 2, height / 4, width / 2, 0)

		graphicsContext.drawLine(width / 4 + 2 * width / 32, height * 3 / 4, width * 3 / 4
				- 4 * width / 32, height * 3 / 4)
		graphicsContext.drawLine(width / 4 + 2 * width / 32, height * 3 / 4, width / 4 + 2
				* width / 32, height - 1)

		graphicsContext.drawOval(1, 1, width - 1 - 2 * width / 32, height - 1 - 2 * width
				/ 32)
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
		return new Point(controlPoints[0].x + pinSpacing * 5,
				controlPoints[0].y + pinSpacing * 2)
	}

	@EditableProperty(name = "Directly heated")
	public boolean getDirectlyHeated() {
		return directlyHeated
	}

	public void setDirectlyHeated(boolean directlyHeated) {
		this.directlyHeated = directlyHeated
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
