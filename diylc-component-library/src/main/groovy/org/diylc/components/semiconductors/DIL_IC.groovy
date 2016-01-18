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

@ComponentDescriptor(name = "DIP IC", author = "Branislav Stojkovic", category = "Semiconductors", instanceNamePrefix = "IC", description = "Dual-in-line package IC", stretchable = false, zOrder = IDIYComponent.COMPONENT)
public class DIL_IC extends AbstractTransparentComponent<String> implements Geometry {

	private static final long serialVersionUID = 1L

	public static Color BODY_COLOR = Color.gray
	public static Color BORDER_COLOR = Color.gray.darker()
	public static Color PIN_COLOR = Color.decode("#00B2EE")
	public static Color PIN_BORDER_COLOR = PIN_COLOR.darker()
	public static Color INDENT_COLOR = Color.gray.darker()
	public static Color LABEL_COLOR = Color.white
	public static int EDGE_RADIUS = 6
	public static Size PIN_SIZE = new Size(0.04d, SizeUnit.in)
	public static Size INDENT_SIZE = new Size(0.07d, SizeUnit.in)

	private Point[] controlPoints = points(point(0, 0))

	@EditableProperty
	String value = ""

	@EditableProperty
	Orientation orientation = Orientation.DEFAULT

	@EditableProperty(name = "Pins")
	SIL_IC.PinCount pinCount = SIL_IC.PinCount._8

	@EditableProperty(name = "Pin spacing")
	Size pinSpacing = new Size(0.1d, SizeUnit.in)

	@EditableProperty(name = "Row spacing")
	Size rowSpacing = new Size(0.3d, SizeUnit.in)

	@EditableProperty
	Display display = Display.NAME

	@EditableProperty(name = "Body")
	Color bodyColor = BODY_COLOR

	@EditableProperty(name = "Border")
	Color borderColor = BORDER_COLOR

	@EditableProperty(name = "Label")
	Color labelColor = LABEL_COLOR

	@EditableProperty(name = "Indent")
	Color indentColor = INDENT_COLOR

	// point(0, pinSpacing.convertToPixels()),
	// point(0, 2 * pinSpacing.convertToPixels()),
	// point(0, 3 * pinSpacing.convertToPixels()),
	// point(3 * pinSpacing.convertToPixels(), 0),
	// point(3 * pinSpacing.convertToPixels(),
	// pinSpacing.convertToPixels()),
	// point(3 * pinSpacing.convertToPixels(), 2 *
	// pinSpacing.convertToPixels()),
	// point(3 * pinSpacing.convertToPixels(), 3 *
	// pinSpacing.convertToPixels()) };
	transient private Area[] body

	public DIL_IC() {
		super()
		updateControlPoints()
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation
		updateControlPoints()
		// Reset body shape.
		body = null
	}

	public void setPinCount(PinCount pinCount) {
		this.pinCount = pinCount
		updateControlPoints()
		// Reset body shape;
		body = null
	}

	public void setRowSpacing(Size rowSpacing) {
		this.rowSpacing = rowSpacing
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
		Point firstPoint = controlPoints[0]
		controlPoints = new Point[pinCount.getValue()]
		controlPoints[0] = firstPoint
		int pinSpacing = (int) this.pinSpacing.convertToPixels()
		int rowSpacing = (int) this.rowSpacing.convertToPixels()
		// Update control points.
		int dx1
		int dy1
		int dx2
		int dy2
		for (int i = 0; i < pinCount.getValue() / 2; i++) {
			switch (orientation) {
			case Orientation.DEFAULT:
				dx1 = 0
				dy1 = i * pinSpacing
				dx2 = rowSpacing
				dy2 = i * pinSpacing
				break
			case Orientation._90:
				dx1 = -i * pinSpacing
				dy1 = 0
				dx2 = -i * pinSpacing
				dy2 = rowSpacing
				break
			case Orientation._180:
				dx1 = 0
				dy1 = -i * pinSpacing
				dx2 = -rowSpacing
				dy2 = -i * pinSpacing
				break
			case Orientation._270:
				dx1 = i * pinSpacing
				dy1 = 0
				dx2 = i * pinSpacing
				dy2 = -rowSpacing
				break
			default:
				throw new RuntimeException("Unexpected orientation: " + orientation)
			}
			controlPoints[i] = point(firstPoint.x + dx1, firstPoint.y + dy1)
			controlPoints[i + pinCount.getValue() / 2] = point(firstPoint.x + dx2, firstPoint.y
					+ dy2)
		}
	}

	public Area[] getBody() {
		if (body == null) {
			body = new Area[2]
			int x = controlPoints[0].x
			int y = controlPoints[0].y
			int width
			int height
			int pinSize = (int) PIN_SIZE.convertToPixels()
			int pinSpacing = (int) this.pinSpacing.convertToPixels()
			int rowSpacing = (int) this.rowSpacing.convertToPixels()
			Area indentation = null
			int indentationSize = getClosestOdd(INDENT_SIZE.convertToPixels())
			switch (orientation) {
			case Orientation.DEFAULT:
				width = rowSpacing - pinSize
				height = (pinCount.getValue() / 2) * pinSpacing
				x += pinSize / 2
				y -= pinSpacing / 2
				indentation = new Area(new Ellipse2D.Double(x + width / 2 - indentationSize / 2, y
						- indentationSize / 2, indentationSize, indentationSize))
				break
			case Orientation._90:
				width = (pinCount.getValue() / 2) * pinSpacing
				height = rowSpacing - pinSize
				x -= (pinSpacing / 2) + width - pinSpacing
				y += pinSize / 2
				indentation = new Area(new Ellipse2D.Double(x + width - indentationSize / 2, y
						+ height / 2 - indentationSize / 2, indentationSize, indentationSize))
				break
			case Orientation._180:
				width = rowSpacing - pinSize
				height = (pinCount.getValue() / 2) * pinSpacing
				x -= rowSpacing - pinSize / 2
				y -= (pinSpacing / 2) + height - pinSpacing
				indentation = new Area(new Ellipse2D.Double(x + width / 2 - indentationSize / 2, y
						+ height - indentationSize / 2, indentationSize, indentationSize))
				break
			case Orientation._270:
				width = (pinCount.getValue() / 2) * pinSpacing
				height = rowSpacing - pinSize
				x -= pinSpacing / 2
				y += pinSize / 2 - rowSpacing
				indentation = new Area(new Ellipse2D.Double(x - indentationSize / 2, y + height / 2
						- indentationSize / 2, indentationSize, indentationSize))
				break
			default:
				throw new RuntimeException("Unexpected orientation: " + orientation)
			}
			body[0] = new Area(new RoundRectangle2D.Double(x, y, width, height, EDGE_RADIUS,
					EDGE_RADIUS))
			body[1] = indentation
			if (indentation != null) {
				indentation.intersect(body[0])
			}
		}
		return body
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return
		}
		Area mainArea = getBody()[0]
		if (!outlineMode) {
			int pinSize = (int) PIN_SIZE.convertToPixels() / 2 * 2
			for (Point point : controlPoints) {
				graphicsContext.setColor(PIN_COLOR)
				graphicsContext.fillRect(point.x - pinSize / 2, point.y - pinSize / 2, pinSize, pinSize)
				graphicsContext.setColor(PIN_BORDER_COLOR)
				graphicsContext.drawRect(point.x - pinSize / 2, point.y - pinSize / 2, pinSize, pinSize)
			}
		}
		Composite oldComposite = graphicsContext.getComposite()
		if (alpha < Colors.MAX_ALPHA) {
			graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toFloat(alpha / Colors.MAX_ALPHA)))
		}
		graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : getBodyColor())
		graphicsContext.fill(mainArea)
		graphicsContext.setComposite(oldComposite)

		Color finalBorderColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : theme.getOutlineColor()
		} else {
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : getBorderColor()
		}
		graphicsContext.setColor(finalBorderColor)
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		if (outlineMode) {
			Area area = new Area(mainArea)
			area.subtract(getBody()[1])
			graphicsContext.draw(area)
		} else {
			graphicsContext.draw(mainArea)
			if (getBody()[1] != null) {
				graphicsContext.setColor(getIndentColor())
				graphicsContext.fill(getBody()[1])
			}
		}
		// Draw label.
		graphicsContext.setFont(LABEL_FONT)
		Color finalLabelColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED : theme
					.getOutlineColor()
		} else {
			finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
					: getLabelColor()
		}
		graphicsContext.setColor(finalLabelColor)
		FontMetrics fontMetrics = graphicsContext.getFontMetrics(graphicsContext.getFont())
		String label = display == Display.NAME ? getName() : getValue()
		Rectangle2D rect = fontMetrics.getStringBounds(label, graphicsContext.graphics2D)
		int textHeight = (int) (rect.getHeight())
		int textWidth = (int) (rect.getWidth())
		// Center text horizontally and vertically
		Rectangle bounds = mainArea.getBounds()
		int x = bounds.x + (bounds.width - textWidth) / 2
		int y = bounds.y + (bounds.height - textHeight) / 2 + fontMetrics.getAscent()
		graphicsContext.drawString(label, x, y)
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int radius = 6 * width / 32
		graphicsContext.setColor(BODY_COLOR)
		graphicsContext.fillRoundRect(width / 6, 1, 4 * width / 6, height - 4, radius, radius)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRoundRect(width / 6, 1, 4 * width / 6, height - 4, radius, radius)
		int pinSize = 2 * width / 32
		graphicsContext.setColor(PIN_COLOR)
		for (int i = 0; i < 4; i++) {
			graphicsContext.fillRect(width / 6 - pinSize, (height / 5) * (i + 1) - 1, pinSize, pinSize)
			graphicsContext.fillRect(5 * width / 6 + 1, (height / 5) * (i + 1) - 1, pinSize, pinSize)
		}
	}
	
	public static enum PinCount {

		_4, _6, _8, _10, _12, _14, _16, _18, _20, _22, _24, _26, _28, _30, _32, _34, _36, _38, _40, _42, _44, _46, _48, _50

		@Override
		public String toString() {
			return name().replace("_", "")
		}

		public int getValue() {
			return Integer.parseInt(toString())
		}
	}
}
