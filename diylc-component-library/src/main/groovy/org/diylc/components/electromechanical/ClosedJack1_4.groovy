package org.diylc.components.electromechanical

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath

import org.diylc.common.HorizontalAlignment
import org.diylc.common.ObjectCache
import org.diylc.common.Orientation
import org.diylc.common.VerticalAlignment
import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.components.JackType
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.utils.Constants

@ComponentDescriptor(name = "Closed 1/4\" Jack", category = "Electromechanical", author = "Branislav Stojkovic", description = "Enclosed panel mount 1/4\" phono jack", stretchable = false, zOrder = IDIYComponent.COMPONENT, instanceNamePrefix = "J", autoEdit = false)
class ClosedJack1_4 extends AbstractTransparentComponent<String> implements Geometry {

	private static final long serialVersionUID = 1L

	private static Size SPACING = new Size(0.1d, SizeUnit.in)
	private static Size LUG_WIDTH = new Size(0.1d, SizeUnit.in)
	private static Size LUG_LENGTH = new Size(0.12d, SizeUnit.in)
	private static Size LUG_HOLE_SIZE = new Size(1d, SizeUnit.mm)
	private static Color BODY_COLOR = Color.decode("#666666")
	private static Color SHAFT_COLOR = Color.decode("#AAAAAA")
	private static Size SHAFT_LENGTH = new Size(0.25d, SizeUnit.in)
	private static Size SHAFT_WIDTH = new Size(3d / 8, SizeUnit.in)
	private static Color BORDER_COLOR = Color.black
	private static Color LABEL_COLOR = Color.white
	private static Size BODY_WIDTH = new Size(0.65d, SizeUnit.in)
	private static Size BODY_LENGTH = new Size(0.8d, SizeUnit.in)

	private Point[] controlPoints = points(point(0, 0))
	private JackType type = JackType.MONO
	private Orientation orientation = Orientation.DEFAULT
	transient private Shape[] body
	private String value = ""

	public ClosedJack1_4() {
		super()
		updateControlPoints()
	}

	private void updateControlPoints() {
		// invalidate body shape
		body = null
		int x = controlPoints[0].x
		int y = controlPoints[0].y
		int spacing = (int) SPACING.convertToPixels()
		int bodyLength = (int) BODY_LENGTH.convertToPixels()
		controlPoints = new Point[type == JackType.STEREO ? 3 : 2]

		controlPoints[0] = point(x, y)
		controlPoints[1] = point(x + bodyLength, y)
		if (type == JackType.STEREO) {
			controlPoints[2] = point(x, y + 2 * spacing)
		}

		// Apply rotation if necessary
		double angle = getAngle()
		if (angle != 0) {
			AffineTransform rotation = AffineTransform.getRotateInstance(angle, x, y)
			for (int i = 1; i < controlPoints.length; i++) {
				rotation.transform(controlPoints[i], controlPoints[i])
			}
		}
	}

	private double getAngle() {
		// Apply rotation if necessary
		double angle
		switch (orientation) {
		case Orientation._90:
			angle = Math.PI / 2
			break
		case Orientation._180:
			angle = Math.PI
			break
		case Orientation._270:
			angle = Math.PI * 3 / 2
			break
		default:
			angle = 0
		}

		return angle
	}

	public Shape[] getBody() {
		if (body == null) {
			body = new Shape[5]

			// Create body.
			int x = controlPoints[0].x
			int y = controlPoints[0].y
			int lugWidth = (int) LUG_WIDTH.convertToPixels()
			int lugLength = (int) LUG_LENGTH.convertToPixels()
			int lugHoleSize = (int) LUG_HOLE_SIZE.convertToPixels()
			int bodyLength = (int) BODY_LENGTH.convertToPixels()
			int bodyWidth = (int) BODY_WIDTH.convertToPixels()
			body[0] = new Area(rectangle(x + lugLength, y - bodyWidth / 2, bodyLength,
					bodyWidth))

			int shaftLength = (int) SHAFT_LENGTH.convertToPixels()
			int shaftWidth = (int) SHAFT_WIDTH.convertToPixels()
			Area shaft = new Area(rectangle(x + lugLength + bodyLength, y - shaftWidth / 2,
					shaftLength, shaftWidth))
			body[1] = shaft

			double angle = getAngle()
			AffineTransform rotation = null
			if (angle != 0) {
				rotation = AffineTransform.getRotateInstance(angle, x, y)
			}

			GeneralPath path = new GeneralPath()
			int step = 4
			for (int i = x + lugLength + bodyLength + step; i <= x + lugLength + bodyLength
					+ shaftLength; i += step) {
				Point p = point(i, y - shaftWidth / 2 + 1)
				if (rotation != null) {
					rotation.transform(p, p)
				}
				path.moveTo(p.x, p.y)
				p = point(i - step, y + shaftWidth / 2 - 1)
				if (rotation != null) {
					rotation.transform(p, p)
				}
				path.lineTo(p.x, p.y)
			}
			Area pathArea = new Area(path)
			pathArea.intersect(shaft)
			body[2] = path

			// Create lugs.
			Area lugs = new Area()

			int spacing = (int) SPACING.convertToPixels()
			Point[] untransformedControlPoints = new Point[type == JackType.STEREO ? 3 : 2]

			untransformedControlPoints[0] = point(x, y)
			untransformedControlPoints[1] = point(x + bodyLength, y)
			if (type == JackType.STEREO) {
				untransformedControlPoints[2] = point(x, y + 2 * spacing)
			}

			for (int i = 0; i < untransformedControlPoints.length; i++) {
				Point point = untransformedControlPoints[i]
				Area lug = new Area(new Ellipse2D.Double(point.x - lugWidth / 2, point.y - lugWidth
						/ 2, lugWidth, lugWidth))
				lug.add(new Area(
						rectangle(point.x, point.y - lugWidth / 2, lugLength, lugWidth)))
				lug.subtract(new Area(new Ellipse2D.Double(point.x - lugHoleSize / 2, point.y
						- lugHoleSize / 2, lugHoleSize, lugHoleSize)))
				lugs.add(lug)
			}

			body[3] = lugs

			// Rotate everything that's of Area type, e.g. everything but lines.
			if (rotation != null) {
				for (Shape shape : body) {
					if (shape instanceof Area) {
						Area area = (Area) shape
						area.transform(rotation)
					}
				}
			}
		}
		return body
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		Shape[] body = getBody()

		// Rectangle bounds = body.getBounds()

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		if (componentState != ComponentState.DRAGGING) {
			Composite oldComposite = graphicsContext.getComposite()
			if (alpha < MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha
						/ MAX_ALPHA))
			}
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : BODY_COLOR)
			graphicsContext.fill(body[0])
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : SHAFT_COLOR)
			graphicsContext.fill(body[1])
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : METAL_COLOR)
			graphicsContext.fill(body[3])
			graphicsContext.setComposite(oldComposite)
		}

		Color finalBorderColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR : theme
					.getOutlineColor()
		} else {
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR : BORDER_COLOR
		}

		graphicsContext.setColor(finalBorderColor)
		graphicsContext.draw(body[0])
		graphicsContext.draw(body[1])
		if (!outlineMode) {
			graphicsContext.setColor(SHAFT_COLOR.darker())
			graphicsContext.fill(body[2])
			graphicsContext.draw(body[2])
		}

		// Pins are the last piece.
		graphicsContext.setColor(outlineMode ? finalBorderColor : METAL_COLOR.darker())
		graphicsContext.draw(body[3])

		Color finalLabelColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? LABEL_COLOR_SELECTED : theme
					.getOutlineColor()
		} else {
			finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? LABEL_COLOR_SELECTED
					: LABEL_COLOR
		}
		graphicsContext.setColor(finalLabelColor)
		graphicsContext.setFont(LABEL_FONT)
		Rectangle bounds = body[0].getBounds()
		int centerX = bounds.x + bounds.width / 2
		int centerY = bounds.y + bounds.height / 2
		drawCenteredText(graphicsContext, name, point(centerX, centerY), HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER)
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int bodyWidth = getClosestOdd(width * 3 / 5)
		int tailWidth = getClosestOdd(width * 3 / 10)

		graphicsContext.setColor(SHAFT_COLOR)
		graphicsContext.fillRect((width - tailWidth) / 2, 1, tailWidth, height / 2)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRect((width - tailWidth) / 2, 1, tailWidth, height / 2)

		graphicsContext.setColor(BODY_COLOR)
		graphicsContext.fillRect((width - bodyWidth) / 2, height / 7 + 1, bodyWidth, height * 5 / 7)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRect((width - bodyWidth) / 2, height / 7 + 1, bodyWidth, height * 5 / 7)

		graphicsContext.setColor(METAL_COLOR)

		graphicsContext.fillRect(width * 7 / 16, height * 6 / 7 + 1, width / 8, height / 7 - 1)
		graphicsContext.fillRect(width * 7 / 16, height / 7 + 2, width / 8, height / 7 - 1)
	}

	@Override
	public int getControlPointCount() {
		return controlPoints.length
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoints[index]
	}

	@Override
	public void setControlPoint(Point point, int index) {
		controlPoints[index].setLocation(point)
		body = null
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return true
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.NEVER
	}

	@EditableProperty
	@Override
	public String getValue() {
		return value
	}

	@Override
	public void setValue(String value) {
		this.value = value
	}

	@EditableProperty
	public JackType getType() {
		return type
	}

	public void setType(JackType type) {
		this.type = type
		updateControlPoints()
	}

	@EditableProperty
	public Orientation getOrientation() {
		return orientation
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation
		updateControlPoints()
	}
}
