package org.diylc.components.boards

import groovy.transform.CompileStatic

import org.diylc.core.graphics.GraphicsContext

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point

import org.diylc.components.AbstractComponent
import org.diylc.components.Colors
import org.diylc.components.Geometry
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.HorizontalAlignment;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Project
import org.diylc.core.VerticalAlignment;
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.components.ComponentState;
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit


@ComponentDescriptor(name = "Breadboard", category = "Boards", author = "Branislav Stojkovic", description = "Prototyping solderless breadboard", instanceNamePrefix = "BB", stretchable = false, zOrder = IDIYComponent.BOARD, bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class Breadboard extends AbstractComponent implements Geometry {

    public static final String id = "8b33c35d-35c8-4bb1-a87b-e6dd845c272a"
    
    private static final long serialVersionUID = 1L

    private static Size BODY_ARC = new Size(3d, SizeUnit.mm)

    private static float COORDINATE_FONT_SIZE = 9f

    private static Size HOLE_SIZE = new Size(1.5d, SizeUnit.mm)

    private static Size HOLE_ARC = new Size(1d, SizeUnit.mm)

    Point point = point(0, 0)

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }

        int bodyArc = (int) BODY_ARC.convertToPixels()
        double spacing = project.getGridSpacing().convertToPixels()

        graphicsContext.with {
            // draw body
            setColor(Colors.SHAPE_FILL_COLOR)
            int width = (int) (23 * project.getGridSpacing().convertToPixels())
            int height = (int) (31 * project.getGridSpacing().convertToPixels())
            fillRoundRect(point.x, point.y, width, height, bodyArc, bodyArc)
            setColor(componentState == ComponentState.SELECTED
                    || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                    : Colors.SHAPE_BORDER_COLOR)
            setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
            drawRoundRect(point.x, point.y, width, height, bodyArc, bodyArc)

            drawingObserver.stopTracking()

            // draw lines
            setColor(Colors.PLUS_COLOR)
            drawLine(point.x + spacing, point.y + spacing, point.x + spacing, point.y + 30 * spacing)
            drawLine(point.x + 19 * spacing, point.y + spacing, point.x + 19 * spacing, point.y + 30 * spacing)
            setColor(Colors.MINUS_COLOR)
            drawLine(point.x + 4 * spacing, point.y + spacing, point.x + 4 * spacing, point.y + 30 * spacing)
            drawLine(point.x + 22 * spacing, point.y + spacing, point.x + 22 * spacing, point.y + 30 * spacing)

            int holeSize = getClosestOdd(HOLE_SIZE.convertToPixels())
            int holeArc = (int) HOLE_ARC.convertToPixels()

            setFont(LABEL_FONT.deriveFont(COORDINATE_FONT_SIZE))
            byte a = "a".getBytes()[0]

            // draw main holes
            for (int section = 0; section <= 1; section++) {
                double offset = section * 7 * spacing

                for (int y = 0; y < 30; y++) {
                    setColor(Colors.COORDINATE_COLOR)
                    int coordinateX
                    if (section == 0) {
                        coordinateX = (int) (point.x + offset + 5.5 * spacing)
                    } else {
                        coordinateX = (int) (point.x + offset + 10.5 * spacing)
                    }
                    drawCenteredText(graphicsContext, new Integer(y + 1).toString(), point(coordinateX, point.y + (y + 1) * spacing),
                            section == 0 ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT,
                            VerticalAlignment.CENTER)
                    for (int x = 0; x < 5; x++) {
                        int holeX = (int) (point.x + offset + (x + 6) * spacing)
                        int holeY = (int) (point.y + (y + 1) * spacing)
                        setColor(Colors.BREADBOARD_HOLE_COLOR)
                        fillRoundRect(holeX - holeSize / 2, holeY - holeSize / 2, holeSize, holeSize, holeArc, holeArc)
                        setColor(Colors.SHAPE_BORDER_COLOR)
                        drawRoundRect(holeX - holeSize / 2, holeY - holeSize / 2, holeSize, holeSize, holeArc, holeArc)

                        // Draw horizontal labels
                        if (y == 0) {
                            setColor(Colors.COORDINATE_COLOR)
                            drawCenteredText(graphicsContext, new String([
                                (byte) (a + x + 5 * section)] as byte[]), point(holeX, point.y), HorizontalAlignment.CENTER, VerticalAlignment.TOP)
                            drawCenteredText(graphicsContext, new String([
                                (byte) (a + x + 5 * section)] as byte[]), point(holeX, point.y + spacing * 30 + COORDINATE_FONT_SIZE / 2),
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
                        setColor(Colors.BREADBOARD_HOLE_COLOR)
                        fillRoundRect(holeX - holeSize / 2, holeY - holeSize / 2, holeSize, holeSize, holeArc, holeArc)
                        setColor(Colors.SHAPE_BORDER_COLOR)
                        drawRoundRect(holeX - holeSize / 2, holeY - holeSize / 2, holeSize, holeSize, holeArc, holeArc)
                    }
                }
            }
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width
        int arc = 4 / factor
        graphicsContext.with {
            setColor(Colors.SHAPE_FILL_COLOR)
            fillRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor)
            setColor(Colors.SHAPE_BORDER_COLOR)
            drawRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor)
            setColor(Colors.BREADBOARD_HOLE_COLOR)
            fillRoundRect(width / 3 - 2 / factor, width / 3 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc, arc)
            setColor(Colors.SHAPE_BORDER_COLOR)
            drawRoundRect(width / 3 - 2 / factor, width / 3 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc, arc)
            setColor(Colors.BREADBOARD_HOLE_COLOR)
            fillRoundRect(2 * width / 3 - 2 / factor, width / 3 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc, arc)
            setColor(Colors.SHAPE_BORDER_COLOR)
            drawRoundRect(2 * width / 3 - 2 / factor, width / 3 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc, arc)
            setColor(Colors.BREADBOARD_HOLE_COLOR)
            fillRoundRect(width / 3 - 2 / factor, 2 * width / 3 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc, arc)
            setColor(Colors.SHAPE_BORDER_COLOR)
            drawRoundRect(width / 3 - 2 / factor, 2 * width / 3 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc, arc)
            setColor(Colors.BREADBOARD_HOLE_COLOR)
            fillRoundRect(2 * width / 3 - 2 / factor, 2 * width / 3 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc, arc)
            setColor(Colors.SHAPE_BORDER_COLOR)
            drawRoundRect(2 * width / 3 - 2 / factor, 2 * width / 3 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), arc, arc)
            setColor(Colors.MINUS_COLOR)
            drawLine(width / 2, 2 / factor, width / 2, height - 4 / factor)
        }
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

    @Deprecated
    @Override
    public String getName() {
        return super.getName()
    }
}
