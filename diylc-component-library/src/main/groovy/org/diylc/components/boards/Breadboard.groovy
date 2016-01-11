package org.diylc.components.boards

import groovy.transform.CompileStatic

import org.diylc.core.graphics.GraphicsContext

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point

import org.diylc.common.HorizontalAlignment
import org.diylc.common.ObjectCache
import org.diylc.common.VerticalAlignment
import org.diylc.components.AbstractComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit


@ComponentDescriptor(name = "Breadboard", category = "Boards", author = "Branislav Stojkovic", description = "Prototyping solderless breadboard", instanceNamePrefix = "BB", stretchable = false, zOrder = IDIYComponent.BOARD, bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class Breadboard extends AbstractComponent<Void> implements Geometry {

	private static final long serialVersionUID = 1L

	public static Color FILL_COLOR = Color.white
	public static Color BORDER_COLOR = Color.black
	public static Size BODY_ARC = new Size(3d, SizeUnit.mm)
	public static Color SELECTION_COLOR = Color.red
	public static Color HOLE_COLOR = Color.decode("#EEEEEE")

	public static Color PLUS_COLOR = Color.red
	public static Color MINUS_COLOR = Color.blue

	public static float COORDINATE_FONT_SIZE = 9f
	public static Color COORDINATE_COLOR = Color.gray.brighter()

	public static Size HOLE_SIZE = new Size(1.5d, SizeUnit.mm)
	public static Size HOLE_ARC = new Size(1d, SizeUnit.mm)

	protected Point point = point(0, 0)

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
                     boolean outlineMode, Project project,
                     IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return
		}

		int bodyArc = (int) BODY_ARC.convertToPixels()
		double spacing = project.getGridSpacing().convertToPixels()

		// draw body
		graphicsContext.setColor(FILL_COLOR)
		int width = (int) (23 * project.getGridSpacing().convertToPixels())
		int height = (int) (31 * project.getGridSpacing().convertToPixels())
		graphicsContext.fillRoundRect(point.x, point.y, width, height, bodyArc, bodyArc)
		graphicsContext.setColor(componentState == ComponentState.SELECTED
				|| componentState == ComponentState.DRAGGING ? SELECTION_COLOR
				: BORDER_COLOR)
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.drawRoundRect(point.x, point.y, width, height, bodyArc, bodyArc)

		drawingObserver.stopTracking()

		// draw lines
		graphicsContext.setColor(PLUS_COLOR)
		graphicsContext.drawLine((int) (point.x + spacing), (int) (point.y + spacing),
				(int) (point.x + spacing), (int) (point.y + 30 * spacing))
		graphicsContext.drawLine((int) (point.x + 19 * spacing), (int) (point.y + spacing),
				(int) (point.x + 19 * spacing), (int) (point.y + 30 * spacing))
		graphicsContext.setColor(MINUS_COLOR)
		graphicsContext.drawLine((int) (point.x + 4 * spacing), (int) (point.y + spacing),
				(int) (point.x + 4 * spacing), (int) (point.y + 30 * spacing))
		graphicsContext.drawLine((int) (point.x + 22 * spacing), (int) (point.y + spacing),
				(int) (point.x + 22 * spacing), (int) (point.y + 30 * spacing))

		int holeSize = getClosestOdd(HOLE_SIZE.convertToPixels())
		int holeArc = (int) HOLE_ARC.convertToPixels()

		graphicsContext.setFont(LABEL_FONT.deriveFont(COORDINATE_FONT_SIZE))
		byte a = "a".getBytes()[0]

		// draw main holes
		for (int section = 0; section <= 1; section++) {
			double offset = section * 7 * spacing

			for (int y = 0; y < 30; y++) {
				graphicsContext.setColor(COORDINATE_COLOR)
				int coordinateX
				if (section == 0) {
					coordinateX = (int) (point.x + offset + 5.5 * spacing)
				} else {
					coordinateX = (int) (point.x + offset + 10.5 * spacing)
				}
				drawCenteredText(graphicsContext, new Integer(y + 1).toString(),
						coordinateX, (int) (point.y + (y + 1) * spacing),
						section == 0 ? HorizontalAlignment.RIGHT
								: HorizontalAlignment.LEFT,
						VerticalAlignment.CENTER)
				for (int x = 0; x < 5; x++) {
					int holeX = (int) (point.x + offset + (x + 6) * spacing)
					int holeY = (int) (point.y + (y + 1) * spacing)
					graphicsContext.setColor(HOLE_COLOR)
					graphicsContext.fillRoundRect(holeX - holeSize / 2, holeY - holeSize / 2, holeSize, holeSize, holeArc, holeArc)
					graphicsContext.setColor(BORDER_COLOR)
					graphicsContext.drawRoundRect(holeX - holeSize / 2, holeY - holeSize / 2, holeSize, holeSize, holeArc, holeArc)

					// Draw horizontal labels
					if (y == 0) {
						graphicsContext.setColor(COORDINATE_COLOR)
						drawCenteredText(graphicsContext, new String([(byte) (a + x + 5 * section)] as byte[]), holeX, (int) (point.y), HorizontalAlignment.CENTER, VerticalAlignment.TOP)
						drawCenteredText(graphicsContext, new String([(byte) (a + x + 5 * section)] as byte[]), holeX, (int) (point.y
								+ spacing * 30 + COORDINATE_FONT_SIZE / 2),
								HorizontalAlignment.CENTER,
								VerticalAlignment.TOP)
					}
				}
			}
		}

		// draw side holes
		for (int section = 0; section <= 1; section++) {
			double offset = section * 18 * spacing
			for (int y = 0; y < 30; y++) {
				for (int x = 0; x < 2; x++) {
					if ((y + 1) % 5 == 0)
						continue
					int holeX = (int) (point.x + offset + (x + 2) * spacing)
					int holeY = (int) (point.y + (y + 1 + 0.5) * spacing)
					graphicsContext.setColor(HOLE_COLOR)
					graphicsContext.fillRoundRect(holeX - holeSize / 2, holeY - holeSize / 2, holeSize, holeSize, holeArc, holeArc)
					graphicsContext.setColor(BORDER_COLOR)
					graphicsContext.drawRoundRect(holeX - holeSize / 2, holeY - holeSize / 2, holeSize, holeSize, holeArc, holeArc)
				}
			}
		}
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int factor = 32 / width
		int arc = 4 / factor
		graphicsContext.setColor(FILL_COLOR)
		graphicsContext.fillRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor)

		graphicsContext.setColor(HOLE_COLOR)
		graphicsContext.fillRoundRect(width / 3 - 2 / factor, width / 3 - 2 / factor,
				getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc,
				arc)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRoundRect(width / 3 - 2 / factor, width / 3 - 2 / factor,
				getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc,
				arc)

		graphicsContext.setColor(HOLE_COLOR)
		graphicsContext.fillRoundRect(2 * width / 3 - 2 / factor, width / 3 - 2 / factor,
				getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc,
				arc)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRoundRect(2 * width / 3 - 2 / factor, width / 3 - 2 / factor,
				getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc,
				arc)

		graphicsContext.setColor(HOLE_COLOR)
		graphicsContext.fillRoundRect(width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
				getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc,
				arc)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRoundRect(width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
				getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc,
				arc)

		graphicsContext.setColor(HOLE_COLOR)
		graphicsContext.fillRoundRect(2 * width / 3 - 2 / factor, 2 * width / 3 - 2 / factor, getClosestOdd(5.0 / factor),
				getClosestOdd(5.0 / factor), arc, arc)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRoundRect(2 * width / 3 - 2 / factor, 2 * width / 3 - 2 / factor, getClosestOdd(5.0 / factor),
				getClosestOdd(5.0 / factor), arc, arc)
		
		graphicsContext.setColor(MINUS_COLOR)
		graphicsContext.drawLine(width / 2, 2 / factor, width / 2, height - 4 / factor)
	}

	@Override
	public int getControlPointCount() {
		return 1
	}

	@Override
	public Point getControlPoint(int index) {
		return point
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return false
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.NEVER
	}

	@Override
	public void setControlPoint(Point point, int index) {
		this.point.setLocation(point)
	}

	@Override
	public Void getValue() {
		return null
	}

	@Override
	public void setValue(Void value) {
	}

	@Deprecated
	@Override
	public String getName() {
		return super.getName()
	}
}
