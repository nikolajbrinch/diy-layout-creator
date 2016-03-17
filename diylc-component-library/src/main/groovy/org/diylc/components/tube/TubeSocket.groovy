package org.diylc.components.tube

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Point
import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.Ellipse2D

import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.Colors
import org.diylc.components.Geometry
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache
import org.diylc.core.Orientation
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.components.ComponentState
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.annotations.ComponentAutoEdit
import org.diylc.core.components.annotations.ComponentDescriptor
import org.diylc.core.components.annotations.ComponentEditOptions
import org.diylc.core.components.properties.EditableProperty
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

@ComponentAutoEdit
@ComponentEditOptions(stretchable = false, rotatable = false)
@ComponentDescriptor(name = "Tube Socket", author = "Branislav Stojkovic", category = "Tubes", instanceNamePrefix = "V", description = "Various types of tube/valve sockets")
public class TubeSocket extends AbstractTransparentComponent implements Geometry {

    public static final String id = "cd07cf68-113f-490e-9015-1f91a7ad5040"
    
	private static final long serialVersionUID = 1L

	private static Color BODY_COLOR = Color.decode("#FFFFE0")
	
    private static Color BORDER_COLOR = Color.decode("#8E8E38")
	
    private static Color PIN_COLOR = Color.decode("#00B2EE")
	
    private static Color PIN_BORDER_COLOR = PIN_COLOR.darker()
	
    private static Size PIN_SIZE = new Size(1d, SizeUnit.mm)
	
    private static Size HOLE_SIZE = new Size(5d, SizeUnit.mm)
	
    private static Size TICK_SIZE = new Size(2d, SizeUnit.mm)

    @EditableProperty
	Base base = Base.B9A
    
    @EditableProperty
	Orientation orientation = Orientation.DEFAULT

    @EditableProperty(name = "Type")
    String value = ""
    
	private Point[] controlPoints = points(point(0, 0))

	transient private Shape body

	public TubeSocket() {
		super()
		updateControlPoints()
	}

	public void setBase(Base base) {
		this.base = base
		updateControlPoints()
		// Reset body shape
		body = null
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation
		updateControlPoints()
		// Reset body shape
		body = null
	}

	private void updateControlPoints() {
		Point firstPoint = controlPoints[0]
		int pinCount
		int pinCircleDiameter
		boolean hasEmptySpace
		switch (base) {
		case Base.B7G:
			pinCount = 7
			pinCircleDiameter = getClosestOdd(new Size(12d, SizeUnit.mm).convertToPixels())
			hasEmptySpace = true
			break
		case Base.OCTAL:
			pinCount = 8
			pinCircleDiameter = getClosestOdd(new Size(17.5d, SizeUnit.mm).convertToPixels())
			hasEmptySpace = false
			break
		case Base.B9A:
			pinCount = 9
			pinCircleDiameter = getClosestOdd(new Size(21d, SizeUnit.mm).convertToPixels())
			hasEmptySpace = true
			break
		default:
			throw new RuntimeException("Unexpected base: " + base)
		}
		double angleIncrement = Math.PI * 2 / (hasEmptySpace ? (pinCount + 1) : pinCount)
		double initialAngleOffset = hasEmptySpace ? angleIncrement : (angleIncrement / 2)
		double initialAngle
		switch (orientation) {
		case Orientation.DEFAULT:
			initialAngle = Math.PI / 2 + initialAngleOffset
			break
		case Orientation._90:
			initialAngle = Math.PI + initialAngleOffset
			break
		case Orientation._180:
			initialAngle = 3 * Math.PI / 2 + initialAngleOffset
			break
		case Orientation._270:
			initialAngle = initialAngleOffset
			break
		default:
			throw new RuntimeException("Unexpected orientation: " + orientation)
		}
		controlPoints = new Point[pinCount + 1]
		double angle = initialAngle
		controlPoints[0] = firstPoint
		for (int i = 0; i < pinCount; i++) {
			controlPoints[i + 1] = point((int) (firstPoint.getX() + Math.cos(angle)
					* pinCircleDiameter / 2), (int) (firstPoint.getY() + Math.sin(angle)
					* pinCircleDiameter / 2))
			angle += angleIncrement
		}
	}

	public Shape getBody() {
		if (body == null) {
			int bodyDiameter
			switch (base) {
			case Base.B7G:
				bodyDiameter = getClosestOdd(new Size(17d, SizeUnit.mm).convertToPixels())
				break
			case Base.B9A:
				bodyDiameter = getClosestOdd(new Size(19d, SizeUnit.mm).convertToPixels())
				break
			case Base.OCTAL:
				bodyDiameter = getClosestOdd(new Size(24.5d, SizeUnit.mm).convertToPixels())
				break
			default:
				throw new RuntimeException("Unexpected base: " + base)
			}
			body = new Ellipse2D.Double(controlPoints[0].x - bodyDiameter / 2, controlPoints[0].y
					- bodyDiameter / 2, bodyDiameter, bodyDiameter)
			Area bodyArea = new Area(body)
			int holeSize = getClosestOdd(HOLE_SIZE.convertToPixels())
			bodyArea.subtract(new Area(new Ellipse2D.Double(controlPoints[0].x - holeSize / 2,
					controlPoints[0].y - holeSize / 2, holeSize, holeSize)))
			if (base == Base.OCTAL) {
				int tickSize = getClosestOdd(TICK_SIZE.convertToPixels())
				double angle = 0
				switch (orientation) {
				case Orientation.DEFAULT:
					angle = Math.PI / 2
					break
				case Orientation._90:
					angle = Math.PI
					break
				case Orientation._180:
					angle = 3 * Math.PI / 2
					break
				case Orientation._270:
					angle = 0
					break
				}
				int centerX = (int) (controlPoints[0].x + Math.cos(angle) * holeSize / 2)
				int centerY = (int) (controlPoints[0].y + Math.sin(angle) * holeSize / 2)
				bodyArea.subtract(new Area(new Ellipse2D.Double(centerX - tickSize / 2, centerY
						- tickSize / 2, tickSize, tickSize)))
			}
			body = bodyArea
		}
		return body
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		// g2d.setColor(Color.black);
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		// for (int i = 0; i < controlPoints.length; i++) {
		// g2d.drawString(Integer.toString(i), controlPoints[i].x,
		// controlPoints[i].y);
		// }
		// Draw body
		Shape body = getBody()
		Composite oldComposite = graphicsContext.getComposite()
		if (alpha < Colors.MAX_ALPHA) {
			graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
		}
		if (componentState != ComponentState.DRAGGING) {
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : BODY_COLOR)
			graphicsContext.fill(body)
		}
		graphicsContext.setComposite(oldComposite)
		Color finalBorderColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : theme
					.getOutlineColor()
		} else {
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : BORDER_COLOR
		}
		graphicsContext.setColor(finalBorderColor)
		graphicsContext.draw(body)
		// Draw pins
		if (!outlineMode) {
			int pinSize = getClosestOdd(PIN_SIZE.convertToPixels())
			for (int i = 1; i < controlPoints.length; i++) {
				graphicsContext.setColor(PIN_COLOR)
				graphicsContext.fillOval(controlPoints[i].x - pinSize / 2, controlPoints[i].y - pinSize / 2,
						pinSize, pinSize)
				graphicsContext.setColor(PIN_BORDER_COLOR)
				graphicsContext.drawOval(controlPoints[i].x - pinSize / 2, controlPoints[i].y - pinSize / 2,
						pinSize, pinSize)
			}
		}
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		Area area = new Area(new Ellipse2D.Double(1, 1, width - 2, width - 2))
		int center = width / 2
		area.subtract(new Area(new Ellipse2D.Double(center - 2, center - 2, 5, 5)))
		graphicsContext.setColor(BODY_COLOR)
		graphicsContext.fill(area)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.draw(area)

		int radius = width / 2 - 6
		for (int i = 0; i < 8; i++) {
			int x = (int) (center + Math.cos(i * Math.PI / 4) * radius)
			int y = (int) (center + Math.sin(i * Math.PI / 4) * radius)
			graphicsContext.setColor(PIN_COLOR)
			graphicsContext.fillOval(x - 1, y - 1, 3, 3)
			graphicsContext.setColor(PIN_BORDER_COLOR)
			graphicsContext.drawOval(x - 1, y - 1, 3, 3)
		}
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoints[index]
	}

	@Override
	public int getControlPointCount() {
		return controlPoints.length
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.NEVER
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return index > 0
	}

	@Override
	public void setControlPoint(Point point, int index) {
		controlPoints[index].setLocation(point)
		body = null
	}

	static enum Base {
		B9A("Noval B9A"), OCTAL("Octal"), B7G("Small-button B7G")

		String name

		private Base(String name) {
			this.name = name
		}

		@Override
		public String toString() {
			return name
		}
	}

	static enum Mount {
		CHASSIS("Chassis"), PCB("PCB")

		String name

		private Mount(String name) {
			this.name = name
		}

		@Override
		public String toString() {
			return name
		}
	}
}
