package org.diylc.components.semiconductors

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.FontMetrics
import java.awt.Point
import java.awt.Rectangle
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D

import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.core.ComponentState
import org.diylc.core.Display;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Orientation;
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

@ComponentDescriptor(name = "Transistor (TO-1 package)", author = "Branislav Stojkovic", category = "Semiconductors", instanceNamePrefix = "Q", description = "Transistor with small metal body", stretchable = false, zOrder = IDIYComponent.COMPONENT)
public class TransistorTO1 extends AbstractTransparentComponent implements Geometry {

	private static final long serialVersionUID = 1L

	public static Color BODY_COLOR = Color.decode("#D0E0EF")
	public static Color BORDER_COLOR = BODY_COLOR.darker()
	public static Color PIN_COLOR = Color.decode("#00B2EE")
	public static Color PIN_BORDER_COLOR = PIN_COLOR.darker()
	public static Color LABEL_COLOR = Color.black
	public static Size PIN_SIZE = new Size(0.03d, SizeUnit.in)
	public static Size PIN_SPACING = new Size(0.05d, SizeUnit.in)
	public static Size BODY_DIAMETER = new Size(0.24d, SizeUnit.in)
	public static Size BODY_LENGTH = new Size(0.4d, SizeUnit.in)
	public static Size EDGE_RADIUS = new Size(2d, SizeUnit.mm)

	transient private Area body

	private Point[] controlPoints = points( point(0, 0),
			point(0, 0), point(0, 0))

	@EditableProperty
	String value = ""

	@EditableProperty
	Orientation orientation = Orientation.DEFAULT

	@EditableProperty(name = "Body")
	Color bodyColor = BODY_COLOR

	@EditableProperty(name = "Border")
	Color borderColor = BORDER_COLOR

	@EditableProperty(name = "Label")
	Color labelColor = LABEL_COLOR

	@EditableProperty
	Display display = Display.NAME

	@EditableProperty
	boolean folded = false

	@EditableProperty(name = "Pin spacing")
	Size pinSpacing = PIN_SPACING

	public TransistorTO1() {
		super()
		updateControlPoints()
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation
		updateControlPoints()
		// Reset body shape;
		body = null
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
	public boolean isControlPointSticky(int index) {
		return true
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.NEVER
	}

	@Override
	public void setControlPoint(Point point, int index) {
		controlPoints[index].setLocation(point)
		body = null
	}

	private void updateControlPoints() {
		int pinSpacing = (int) getPinSpacing().convertToPixels()
		// Update control points.
		int x = controlPoints[0].x
		int y = controlPoints[0].y
		switch (orientation) {
		case Orientation.DEFAULT:
			controlPoints[1].setLocation(x - (folded ? 0 : pinSpacing), y
					+ pinSpacing)
			controlPoints[2].setLocation(x, y + 2 * pinSpacing)
			break
		case Orientation._90:
			controlPoints[1].setLocation(x - pinSpacing, y
					- (folded ? 0 : pinSpacing))
			controlPoints[2].setLocation(x - 2 * pinSpacing, y)
			break
		case Orientation._180:
			controlPoints[1].setLocation(x + (folded ? 0 : pinSpacing), y
					- pinSpacing)
			controlPoints[2].setLocation(x, y - 2 * pinSpacing)
			break
		case Orientation._270:
			controlPoints[1].setLocation(x + pinSpacing, y
					+ (folded ? 0 : pinSpacing))
			controlPoints[2].setLocation(x + 2 * pinSpacing, y)
			break
		default:
			throw new RuntimeException("Unexpected orientation: " + orientation)
		}
	}

	public Area getBody() {
		if (body == null) {
			int x = (controlPoints[0].x + controlPoints[1].x + controlPoints[2].x) / 3
			int y = (controlPoints[0].y + controlPoints[1].y + controlPoints[2].y) / 3
			int bodyDiameter = getClosestOdd(BODY_DIAMETER.convertToPixels())
			int bodyLength = getClosestOdd(BODY_LENGTH.convertToPixels())
			int edgeRadius = (int) EDGE_RADIUS.convertToPixels()

			if (folded) {
				switch (orientation) {
				case DEFAULT:
					body = new Area(new RoundRectangle2D.Double(x - bodyLength,
							y - bodyDiameter / 2, bodyLength, bodyDiameter,
							edgeRadius, edgeRadius))
					body.add(new Area(new Rectangle2D.Double(
							x - bodyLength / 2, y - bodyDiameter / 2,
							bodyLength / 2, bodyDiameter)))
					break
				case _90:
					body = new Area(new RoundRectangle2D.Double(x
							- bodyDiameter / 2, y - bodyLength, bodyDiameter,
							bodyLength, edgeRadius, edgeRadius))
					body.add(new Area(new Rectangle2D.Double(x - bodyDiameter / 2, y - bodyLength / 2, bodyDiameter,
							bodyLength / 2)))
					break
				case _180:
					body = new Area(new RoundRectangle2D.Double(x, y
							- bodyDiameter / 2, bodyLength, bodyDiameter,
							edgeRadius, edgeRadius))
					body.add(new Area(new Rectangle2D.Double(x, y
							- bodyDiameter / 2, bodyLength / 2, bodyDiameter)))
					break
				case _270:
					body = new Area(new RoundRectangle2D.Double(x
							- bodyDiameter / 2, y, bodyDiameter, bodyLength,
							edgeRadius, edgeRadius))
					body.add(new Area(new Rectangle2D.Double(x - bodyDiameter / 2, y, bodyDiameter, bodyLength / 2)))
					break
				default:
					throw new RuntimeException("Unexpected orientation: "
							+ orientation)
				}
			} else {
				body = new Area(new Ellipse2D.Double(x - bodyDiameter / 2, y
						- bodyDiameter / 2, bodyDiameter, bodyDiameter))
			}

		}
		return body
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return
		}
		int pinSize = (int) PIN_SIZE.convertToPixels() / 2 * 2
		Area mainArea = getBody()
		Composite oldComposite = graphicsContext.getComposite()
		if (alpha < Colors.MAX_ALPHA) {
			graphicsContext.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
		}
		graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : bodyColor)
		graphicsContext.fill(mainArea)
		graphicsContext.setComposite(oldComposite)
		Color finalBorderColor
		Theme theme = Configuration.INSTANCE.getTheme()
		if (outlineMode) {
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
					: theme.getOutlineColor()
		} else {
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
					: borderColor
		}
		graphicsContext.setColor(finalBorderColor)
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.draw(mainArea)

		for (Point point : controlPoints) {
			if (!outlineMode) {
				graphicsContext.setColor(PIN_COLOR)
				graphicsContext.fillOval(point.x - pinSize / 2, point.y - pinSize / 2,
						pinSize, pinSize)
			}
			graphicsContext.setColor(outlineMode ? theme.getOutlineColor()
					: PIN_BORDER_COLOR)
			graphicsContext.drawOval(point.x - pinSize / 2, point.y - pinSize / 2, pinSize,
					pinSize)
		}

		// Draw label.
		graphicsContext.setFont(LABEL_FONT)
		Color finalLabelColor
		if (outlineMode) {
			finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
					: theme.getOutlineColor()
		} else {
			finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
					: getLabelColor()
		}
		graphicsContext.setColor(finalLabelColor)
		String label = (getDisplay() == Display.NAME) ? getName() : getValue()
		FontMetrics fontMetrics = graphicsContext.getFontMetrics(graphicsContext.getFont())
		Rectangle2D rect = fontMetrics.getStringBounds(label, graphicsContext.graphics2D)
		int textHeight = (int) (rect.getHeight())
		int textWidth = (int) (rect.getWidth())
		// Center text horizontally and vertically
		Rectangle bounds = mainArea.getBounds()
		int x = bounds.x + (bounds.width - textWidth) / 2
		int y = bounds.y + (bounds.height - textHeight) / 2
				+ fontMetrics.getAscent()
		graphicsContext.drawString(label, x, y)
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int margin = 2 * width / 32
		Area area = new Area(new Ellipse2D.Double(margin / 2, margin, width - 2
				* margin, width - 2 * margin))
		graphicsContext.setColor(BODY_COLOR)
		graphicsContext.fill(area)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.draw(area)
		graphicsContext.setColor(PIN_COLOR)
		int pinSize = 2 * width / 32
		for (int i = 0; i < 3; i++) {
			graphicsContext.fillOval((i == 1 ? width * 3 / 8 : width / 2) - pinSize / 2,
					(height / 4) * (i + 1), pinSize, pinSize)
		}
	}

	public void setFolded(boolean folded) {
		this.folded = folded
		updateControlPoints()
		// Reset body shape;
		body = null
	}

	public void setPinSpacing(Size pinSpacing) {
		this.pinSpacing = pinSpacing
		updateControlPoints()
		// Reset body shape;
		body = null
	}

}
