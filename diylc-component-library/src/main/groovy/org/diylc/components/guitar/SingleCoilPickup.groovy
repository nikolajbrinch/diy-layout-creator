package org.diylc.components.guitar

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Polygon
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.RoundRectangle2D

import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.core.ComponentState
import org.diylc.core.HorizontalAlignment;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Orientation;
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.VerticalAlignment;
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

@ComponentDescriptor(name = "Strat Single Coil Pickup", category = "Guitar", author = "Branislav Stojkovic", description = "Strat-style single coil guitar pickup", stretchable = false, zOrder = IDIYComponent.COMPONENT, instanceNamePrefix = "PKP", autoEdit = false)
public class SingleCoilPickup extends AbstractTransparentComponent implements Geometry {

	private static final long serialVersionUID = 1L

	private static Color BODY_COLOR = Color.darkGray
	private static Color POINT_COLOR = Color.lightGray
	private static Size WIDTH = new Size(15.5d, SizeUnit.mm)
	private static Size LENGTH = new Size(83.82d, SizeUnit.mm)
	private static Size LIP_WIDTH = new Size(5d, SizeUnit.mm)
	private static Size LIP_LENGTH = new Size(20d, SizeUnit.mm)
	private static Size POINT_SIZE = new Size(3d, SizeUnit.mm)
	private static Size HOLE_SIZE = new Size(2d, SizeUnit.mm)
	private static Size HOLE_MARGIN = new Size(4d, SizeUnit.mm)
	private static Size POLE_SIZE = new Size(3d, SizeUnit.mm)
	private static Size POLE_SPACING = new Size(11.68d, SizeUnit.mm)

	private Point controlPoint = new Point(0, 0)
	transient Shape[] body
	
    @EditableProperty(name = "Model")
    String value = ""
    
    @EditableProperty
	Orientation orientation = Orientation.DEFAULT
    
    @EditableProperty
	Color color = BODY_COLOR

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		Shape[] body = getBody()

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		if (componentState != ComponentState.DRAGGING) {
			Composite oldComposite = graphicsContext.getComposite()
			if (alpha < Colors.MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
			}
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : color)
			graphicsContext.fill(body[0])
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : POINT_COLOR)
			graphicsContext.fill(body[1])
			graphicsContext.setComposite(oldComposite)
		}

		Color finalBorderColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : theme
					.getOutlineColor()
		} else {
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : color
					.brighter()
		}

		graphicsContext.setColor(finalBorderColor)
		graphicsContext.draw(body[0])
		if (!outlineMode) {
			graphicsContext.draw(body[2])
		}

		Color finalLabelColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED : theme
					.getOutlineColor()
		} else {
			finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
					: Colors.LABEL_COLOR
		}
		graphicsContext.setColor(finalLabelColor)
		graphicsContext.setFont(LABEL_FONT)
		Rectangle bounds = body[0].getBounds()
		drawCenteredText(graphicsContext, value, point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2),
				HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
	}

	public Shape[] getBody() {
		if (body == null) {
			body = new Shape[3]

			int x = controlPoint.x
			int y = controlPoint.y
			int width = (int) WIDTH.convertToPixels()
			int length = (int) LENGTH.convertToPixels()
			int lipWidth = (int) LIP_WIDTH.convertToPixels()
			int lipLength = (int) LIP_LENGTH.convertToPixels()
			int pointSize = getClosestOdd(POINT_SIZE.convertToPixels())
			int holeSize = getClosestOdd(HOLE_SIZE.convertToPixels())
			int holeMargin = getClosestOdd(HOLE_MARGIN.convertToPixels())

			Area mainArea = new Area(new RoundRectangle2D.Double(x - length / 2, y - lipWidth / 2
					- width, length, width, width, width))
			mainArea.add(new Area(new Polygon([ x - length / 2 + width / 2,
					x + length / 2 - width / 2, x + lipLength / 2, x - lipLength / 2 ] as int[], [
					y - lipWidth / 2, y - lipWidth / 2, y + lipWidth / 2, y + lipWidth / 2 ] as int[], 4)))
			// Cutout holes
			mainArea.subtract(new Area(new Ellipse2D.Double(x - length / 2 + holeMargin - holeSize / 2, y - lipWidth / 2 - width / 2 - holeSize / 2, holeSize, holeSize)))
			mainArea.subtract(new Area(new Ellipse2D.Double(x + length / 2 - holeMargin - holeSize / 2, y - lipWidth / 2 - width / 2 - holeSize / 2, holeSize, holeSize)))

			body[0] = mainArea

			body[1] = new Area(new Ellipse2D.Double(x - pointSize / 2, y - pointSize / 2,
					pointSize, pointSize))

			int poleSize = (int) POLE_SIZE.convertToPixels()
			int poleSpacing = (int) POLE_SPACING.convertToPixels()
			int poleMargin = (length - poleSpacing * 5) / 2
			Area poleArea = new Area()
			for (int i = 0; i < 6; i++) {
				Ellipse2D pole = new Ellipse2D.Double(
						x - length / 2 + poleMargin + i * poleSpacing, y - lipWidth / 2 - width / 2
								- poleSize / 2, poleSize, poleSize)
				poleArea.add(new Area(pole))
			}
			body[2] = poleArea

			// Rotate if needed
			if (orientation != Orientation.DEFAULT) {
				double theta = 0
				switch (orientation) {
				case Orientation._90:
					theta = Math.PI / 2
					break
				case Orientation._180:
					theta = Math.PI
					break
				case Orientation._270:
					theta = Math.PI * 3 / 2
					break
				}
				AffineTransform rotation = AffineTransform.getRotateInstance(theta, x, y)
				for (Shape shape : body) {
					Area area = (Area) shape
					area.transform(rotation)
				}
			}
		}
		return body
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int bodyWidth = 8 * width / 32
		int bodyLength = 30 * width / 32
		graphicsContext.with {
            setColor(BODY_COLOR)
    		fillRoundRect((width - bodyWidth) / 2, (height - bodyLength) / 2, bodyWidth,
    				bodyLength, bodyWidth, bodyWidth)
    		fillPolygon([ width * 9 / 16, width * 9 / 16, width * 11 / 16, width * 11 / 16 ] as int[],
    				[ (height - bodyLength) / 2, (height + bodyLength) / 2, height * 5 / 8,
    						height * 3 / 8 ] as int[], 4)
    		
    		setColor(Color.lightGray)
    		drawLine(width / 2, 4 * width / 32, width / 2, 4 * width / 32)
    		drawLine(width / 2, height - 4 * width / 32, width / 2, height - 4 * width / 32)
		}
	}

	@Override
	public int getControlPointCount() {
		return 1
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.WHEN_SELECTED
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return true
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoint
	}

	@Override
	public void setControlPoint(Point point, int index) {
		this.controlPoint.setLocation(point)
		// Invalidate the body
		body = null
	}

	public void setValue(String value) {
		this.value = value
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation
		// Invalidate the body
		body = null
	}

}
