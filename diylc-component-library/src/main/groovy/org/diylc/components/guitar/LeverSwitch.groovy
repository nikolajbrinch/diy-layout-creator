package org.diylc.components.guitar

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D

import org.diylc.common.ObjectCache
import org.diylc.common.Orientation
import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.ComponentDescriptor
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

@ComponentDescriptor(name = "Lever Switch", category = "Guitar", author = "Branislav Stojkovic", description = "Strat-style lever switch", stretchable = false, zOrder = IDIYComponent.COMPONENT, instanceNamePrefix = "SW")
public class LeverSwitch extends AbstractTransparentComponent<String> {

	private static final long serialVersionUID = 1L

	private static Color BASE_COLOR = Color.lightGray
	private static Color WAFER_COLOR = Color.decode("#CD8500")

	private static Size BASE_WIDTH = new Size(10d, SizeUnit.mm)
	private static Size BASE_LENGTH = new Size(47.5d, SizeUnit.mm)
	private static Size WAFER_LENGTH = new Size(40d, SizeUnit.mm)
	private static Size WAFER_SPACING = new Size(7.62d, SizeUnit.mm)
	private static Size WAFER_THICKNESS = new Size(1.27d, SizeUnit.mm)
	private static Size HOLE_SIZE = new Size(2d, SizeUnit.mm)
	private static Size HOLE_SPACING = new Size(41.2d, SizeUnit.mm)
	private static Size TERMINAL_WIDTH = new Size(2d, SizeUnit.mm)
	private static Size TERMINAL_LENGTH = new Size(0.1d, SizeUnit.in)
	private static Size TERMINAL_SPACING = new Size(0.1d, SizeUnit.in)

	private String value = ""
	private Point[] controlPoints = [ new Point(0, 0) ] as Point[]
	transient Shape[] body
	private Orientation orientation = Orientation.DEFAULT
	private LeverSwitchType type = LeverSwitchType.DP3T

	public LeverSwitch() {
		super()
		updateControlPoints()
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		Shape[] body = getBody()

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		if (componentState != ComponentState.DRAGGING) {
			Composite oldComposite = graphicsContext.getComposite()
			if (alpha < MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha
						/ MAX_ALPHA))
			}
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : BASE_COLOR)
			graphicsContext.fill(body[0])
			graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : WAFER_COLOR)
			graphicsContext.fill(body[1])
			graphicsContext.setComposite(oldComposite)
		}

		Color finalBorderColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR : theme
					.getOutlineColor()
		} else {
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR : BASE_COLOR
					.darker()
		}

		graphicsContext.setColor(finalBorderColor)
		graphicsContext.draw(body[0])

		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR : theme
					.getOutlineColor()
		} else {
			finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR : WAFER_COLOR
					.darker()
		}

		graphicsContext.draw(body[1])

		graphicsContext.setColor(METAL_COLOR)
		graphicsContext.fill(body[2])
		graphicsContext.setColor(METAL_COLOR.darker())
		graphicsContext.draw(body[2])
	}

	public Shape[] getBody() {
		if (body == null) {
			body = new Shape[3]

			int x = controlPoints[0].x
			int y = controlPoints[0].y
			int baseWidth = (int) BASE_WIDTH.convertToPixels()
			int baseLength = (int) BASE_LENGTH.convertToPixels()
			int holeSize = getClosestOdd(HOLE_SIZE.convertToPixels())
			int holeSpacing = (int) HOLE_SPACING.convertToPixels()
			int waferLength = (int) WAFER_LENGTH.convertToPixels()
			int waferSpacing = (int) WAFER_SPACING.convertToPixels()
			int waferThickness = (int) WAFER_THICKNESS.convertToPixels()
			int terminalSpacing = (int) TERMINAL_SPACING.convertToPixels()
			int terminalLength = getClosestOdd(TERMINAL_LENGTH.convertToPixels())
			int terminalWidth = getClosestOdd(TERMINAL_WIDTH.convertToPixels())

			int baseX = x - terminalLength / 2 - waferSpacing
			int baseY = y
					- (baseLength - terminalSpacing * (type == LeverSwitchType.DP3T ? 7 : 12)) / 2
			Area baseArea = new Area(new Rectangle2D.Double(baseX, baseY, baseWidth, baseLength))
			baseArea.subtract(new Area(new Ellipse2D.Double(baseX + baseWidth / 2 - holeSize / 2,
					baseY + (baseLength - holeSpacing) / 2 - holeSize / 2, holeSize, holeSize)))
			baseArea.subtract(new Area(new Ellipse2D.Double(baseX + baseWidth / 2 - holeSize / 2,
					baseY + (baseLength - holeSpacing) / 2 - holeSize / 2 + holeSpacing, holeSize,
					holeSize)))
			body[0] = baseArea

			Area waferArea = new Area(new Rectangle2D.Double(x - terminalLength / 2
					- waferThickness / 2,
					y - (waferLength - terminalSpacing * (type == LeverSwitchType.DP3T ? 7 : 12))
							/ 2, waferThickness, waferLength))

			if (type == LeverSwitchType._4P5T) {
				waferArea.add(new Area(new Rectangle2D.Double(x - terminalLength / 2
						- waferThickness / 2 + waferSpacing, y
						- (waferLength - terminalSpacing * (type == LeverSwitchType.DP3T ? 7 : 12))
						/ 2, waferThickness, waferLength)))
			}
			body[1] = waferArea

			double theta = 0
			// Rotate if needed
			if (orientation != Orientation.DEFAULT) {
				switch (orientation) {
				case _90:
					theta = Math.PI / 2
					break
				case _180:
					theta = Math.PI
					break
				case _270:
					theta = Math.PI * 3 / 2
					break
				}
			}

			Area terminalArea = new Area()
			for (Point point : controlPoints) {
				Area terminal = new Area(new RoundRectangle2D.Double(point.x - terminalLength / 2,
						point.y - terminalWidth / 2, terminalLength, terminalWidth,
						terminalWidth / 2, terminalWidth / 2))
				terminal.subtract(new Area(
						new RoundRectangle2D.Double(point.x - terminalLength / 4, point.y
								- terminalWidth / 4, terminalLength / 2, terminalWidth / 2,
								terminalWidth / 2, terminalWidth / 2)))
				// Rotate the terminal if needed
				if (theta != 0) {
					AffineTransform rotation = AffineTransform.getRotateInstance(theta, point.x,
							point.y)
					terminal.transform(rotation)
				}
				terminalArea.add(terminal)
			}
			body[2] = terminalArea

			// Rotate if needed
			if (theta != 0) {
				AffineTransform rotation = AffineTransform.getRotateInstance(theta, x, y)
				// Skip the last one because it's already rotated
				for (int i = 0; i < body.length - 1; i++) {
					Shape shape = body[i]
					Area area = (Area) shape
					area.transform(rotation)
				}
			}
		}
		return body
	}

	private void updateControlPoints() {
		int x = controlPoints[0].x
		int y = controlPoints[0].y
		int waferSpacing = (int) WAFER_SPACING.convertToPixels()
		int terminalSpacing = (int) TERMINAL_SPACING.convertToPixels()
		int terminalLength = (int) TERMINAL_LENGTH.convertToPixels()

		switch (type) {
		case LeverSwitchType.DP3T:
			controlPoints = new Point[8]
			for (int i = 0; i < 8; i++) {
				controlPoints[i] = new Point(x - (i % 2 == 1 ? terminalLength : 0), y + i
						* terminalSpacing)
			}
			break
		case LeverSwitchType.DP5T:
			controlPoints = new Point[12]
			for (int i = 0; i < 12; i++) {
				controlPoints[i] = new Point(x, y + i * terminalSpacing
						+ (i >= 6 ? terminalSpacing : 0))
			}
			break
		case LeverSwitchType._4P5T:
			controlPoints = new Point[24]
			for (int i = 0; i < 12; i++) {
				controlPoints[i] = new Point(x, y + i * terminalSpacing
						+ (i >= 6 ? terminalSpacing : 0))
				controlPoints[i + 12] = new Point(x + waferSpacing, y + i * terminalSpacing
						+ (i >= 6 ? terminalSpacing : 0))
			}
			break
		}

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
			for (Point point : controlPoints) {
				rotation.transform(point, point)
			}
		}
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setClip(width / 32, width / 32, width, height)
		graphicsContext.setColor(BASE_COLOR)
		graphicsContext.fillRect(0, 0, width * 2 / 3, height)
		graphicsContext.setColor(BASE_COLOR.darker())
		graphicsContext.drawRect(0, 0, width * 2 / 3, height)
		graphicsContext.setColor(WAFER_COLOR)
		graphicsContext.fillRect(width / 8 * 3, 0, width / 8, height)
		graphicsContext.setColor(WAFER_COLOR.darker())
		graphicsContext.drawRect(width / 8 * 3, 0, width / 8, height)
		Area terminals = new Area()
		int terminalLength = getClosestOdd(11 * width / 32)
		int terminalWidth = getClosestOdd(7 * width / 32)
		Area terminal = new Area(new RoundRectangle2D.Double(width / 16 * 7, 4 * width / 32,
				terminalLength, terminalWidth, terminalWidth / 2, terminalWidth / 2))
		terminal.subtract(new Area(new RoundRectangle2D.Double(width / 16 * 7 + terminalLength / 4
				+ 1, 4 * width / 32 + terminalWidth / 4 + 1, terminalLength / 2, terminalWidth / 2,
				terminalWidth / 4, terminalWidth / 4)))
		terminals.add(terminal)
		terminal = new Area(terminal)
		terminal.transform(AffineTransform.getTranslateInstance(-terminalLength, terminalWidth + 2
				* width / 32))
		terminals.add(terminal)
		terminal = new Area(terminal)
		terminal.transform(AffineTransform.getTranslateInstance(terminalLength, terminalWidth + 2
				* width / 32))
		terminals.add(terminal)
		graphicsContext.setColor(METAL_COLOR)
		graphicsContext.fill(terminals)
		graphicsContext.setColor(METAL_COLOR.darker())
		graphicsContext.draw(terminals)
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
		return true
	}

	@Override
	public Point getControlPoint(int index) {
		return controlPoints[index]
	}

	@Override
	public void setControlPoint(Point point, int index) {
		this.controlPoints[index].setLocation(point)
		// Invalidate the body
		body = null
	}

	@Override
	public String getValue() {
		return value
	}

	@Override
	public void setValue(String value) {
		this.value = value
	}

	@EditableProperty
	public Orientation getOrientation() {
		return orientation
	}

	@EditableProperty
	public LeverSwitchType getType() {
		return type
	}

	public void setType(LeverSwitchType type) {
		this.type = type
		updateControlPoints()
		// Invalidate body
		this.body = null
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation
		updateControlPoints()
		// Invalidate the body
		body = null
	}

	public enum LeverSwitchType {
		DP3T("DP3T (Standard Strat)"), _4P5T("4P5T (Super/Mega)"), DP5T("DP5T")

		private String title

		private LeverSwitchType(String title) {
			this.title = title
		}

		@Override
		public String toString() {
			return title
		}
	}
}
