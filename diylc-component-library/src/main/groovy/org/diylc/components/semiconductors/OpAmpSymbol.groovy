package org.diylc.components.semiconductors

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Point
import java.awt.Polygon
import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.GeneralPath
import java.awt.geom.Rectangle2D

import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.components.ICPointCount
import org.diylc.core.ComponentState
import org.diylc.core.Display;
import org.diylc.core.HorizontalAlignment;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
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

@ComponentDescriptor(name = "OpAmp Symbol", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "IC", description = "OpAmp symbol with 3 or 5 contacts", stretchable = false, zOrder = IDIYComponent.COMPONENT, rotatable = false)
public class OpAmpSymbol extends AbstractTransparentComponent implements Geometry {

	private static final long serialVersionUID = 1L

	public static Size PIN_SPACING = new Size(0.1d, SizeUnit.in)
	public static Color BODY_COLOR = Color.white
	public static Color BORDER_COLOR = Color.black

	transient private Shape[] body

	protected Point[] controlPoints = points( point(0, 0), point(0, 0),
			point(0, 0), point(0, 0), point(0, 0))

	@EditableProperty(name = "Contacts")
	ICPointCount icPointCount = ICPointCount._5

	@EditableProperty
	String value = ""

	@EditableProperty(name = "Body")
	Color bodyColor = BODY_COLOR

	@EditableProperty(name = "Border")
	Color borderColor = BORDER_COLOR

	@EditableProperty
	Display display = Display.NAME


	public OpAmpSymbol() {
		super()
		updateControlPoints()
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return
		}
		int pinSpacing = (int) PIN_SPACING.convertToPixels()
		Composite oldComposite = graphicsContext.getComposite()
		if (alpha < Colors.MAX_ALPHA) {
			graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
		}

		Shape[] body = getBody()

		graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : bodyColor)
		graphicsContext.fill(body[0])
		graphicsContext.setComposite(oldComposite)
		Color finalBorderColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalBorderColor = theme.getOutlineColor()
		} else {
			finalBorderColor = borderColor
		}
		graphicsContext.setColor(finalBorderColor)
		// Draw contacts
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.draw(body[1])
		// Draw triangle
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2))
		graphicsContext.draw(body[0])
		// Draw label
		graphicsContext.setFont(LABEL_FONT)
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
		int x = (controlPoints[0].x + controlPoints[2].x) / 2
		drawCenteredText(graphicsContext, display == Display.VALUE ? getValue() : getName(), point(x,
				controlPoints[0].y + pinSpacing), HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER)
		// Draw +/- markers
		drawCenteredText(graphicsContext, "-", point(controlPoints[0].x + pinSpacing, controlPoints[0].y),
				HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
		drawCenteredText(graphicsContext, "+", point(controlPoints[1].x + pinSpacing, controlPoints[1].y),
				HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int margin = 3 * width / 32
		Area area = new Area(new Polygon([margin, margin, width - margin ] as int[], [
				margin, height - margin, height / 2 ] as int[], 3))
		area.intersect(new Area(new Rectangle2D.Double(2 * margin, 0, width, height)))
		graphicsContext.setColor(BODY_COLOR)
		graphicsContext.fill(area)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.setFont(LABEL_FONT.deriveFont(8f))
		drawCenteredText(graphicsContext, "-", point(3 * margin, height / 3), HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER)
		drawCenteredText(graphicsContext, "+", point(3 * margin + 1, height * 2 / 3), HorizontalAlignment.CENTER,
				VerticalAlignment.CENTER)
		graphicsContext.draw(area)
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoints[index]
	}

	@Override
	public int getControlPointCount() {
		return icPointCount.getValue()
	}

	private void updateControlPoints() {
		int pinSpacing = (int) PIN_SPACING.convertToPixels()
		// Update control points.
		int x = controlPoints[0].x
		int y = controlPoints[0].y

		controlPoints[1].x = x
		controlPoints[1].y = y + pinSpacing * 2

		controlPoints[2].x = x + pinSpacing * 6
		controlPoints[2].y = y + pinSpacing

		controlPoints[3].x = x + pinSpacing * 3
		controlPoints[3].y = y - pinSpacing

		controlPoints[4].x = x + pinSpacing * 3
		controlPoints[4].y = y + pinSpacing * 3
	}

	public Shape[] getBody() {
		if (body == null) {
			body = new Shape[2]
			int pinSpacing = (int) PIN_SPACING.convertToPixels()
			int x = controlPoints[0].x
			int y = controlPoints[0].y
			Shape triangle = new Polygon([ x + pinSpacing / 2, x + pinSpacing * 11 / 2,
					x + pinSpacing / 2 ] as int[], [ y - pinSpacing * 3 / 2, y + pinSpacing,
					y + pinSpacing * 7 / 2 ] as int[], 3)
			body[0] = triangle

			GeneralPath polyline = new GeneralPath()
			polyline.moveTo(controlPoints[0].x, controlPoints[0].y)
			polyline.lineTo(controlPoints[0].x + pinSpacing / 2, controlPoints[0].y)
			polyline.moveTo(controlPoints[1].x, controlPoints[1].y)
			polyline.lineTo(controlPoints[1].x + pinSpacing / 2, controlPoints[1].y)
			polyline.moveTo(controlPoints[2].x, controlPoints[2].y)
			polyline.lineTo(controlPoints[2].x - pinSpacing / 2, controlPoints[2].y)
			if (icPointCount == ICPointCount._5) {
				polyline.moveTo(controlPoints[3].x, controlPoints[3].y)
				polyline.lineTo(controlPoints[3].x, controlPoints[3].y + pinSpacing * 3 / 4)
				polyline.moveTo(controlPoints[4].x, controlPoints[4].y)
				polyline.lineTo(controlPoints[4].x, controlPoints[4].y - pinSpacing * 3 / 4)
			}
			body[1] = polyline
		}
		return body
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
	public void setControlPoint(Point point, int index) {
		controlPoints[index].setLocation(point)
		body = null
	}

	public void setIcPointCount(ICPointCount icPointCount) {
		this.icPointCount = icPointCount
		updateControlPoints()
		body = null
	}


}
