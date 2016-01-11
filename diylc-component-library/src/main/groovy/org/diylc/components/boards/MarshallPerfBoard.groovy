package org.diylc.components.boards

import org.diylc.core.graphics.GraphicsContext

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Point
import java.awt.Shape

import org.diylc.components.ComponentDescriptor
import org.diylc.components.AbstractBoard;
import org.diylc.components.Geometry;
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.utils.Constants

@ComponentDescriptor(name = "Marshall Style Perf Board", category = "Boards", author = "Branislav Stojkovic", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated board as found on some Marshall and Trainwreck amps", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class MarshallPerfBoard extends AbstractBoard implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color BOARD_COLOR = Color.decode("#CD8500")
    public static Color BORDER_COLOR = BOARD_COLOR.darker()
    public static Size SPACING = new Size(3 / 8d, SizeUnit.in)
    public static Size HOLE_SIZE = new Size(1 / 8d, SizeUnit.in)

    // private Area copperArea
    protected Size spacing = SPACING

    public MarshallPerfBoard() {
        super()
        this.boardColor = BOARD_COLOR
        this.borderColor = BORDER_COLOR
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()
        if (checkPointsClipped(clip)
                && !clip.contains(firstPoint.x, secondPoint.y)
                && !clip.contains(secondPoint.x, firstPoint.y)) {
            return
        }
        super.draw(graphicsContext, componentState, outlineMode, project, drawingObserver)
        if (componentState != ComponentState.DRAGGING) {
            if (alpha < MAX_ALPHA) {
                graphicsContext.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1f * alpha / MAX_ALPHA))
            }
            Point p = point(firstPoint)
            int holeDiameter = getClosestOdd((int) HOLE_SIZE.convertToPixels())
            int spacing = (int) this.spacing.convertToPixels()

            while (p.y < secondPoint.y - spacing) {
                p.x = firstPoint.x
                p.y += spacing
                while (p.x < secondPoint.x - spacing - holeDiameter) {
                    p.x += spacing
                    graphicsContext.setColor(Constants.CANVAS_COLOR)
                    graphicsContext.fillOval(p.x - holeDiameter / 2, p.y - holeDiameter / 2, holeDiameter, holeDiameter)
                    graphicsContext.setColor(borderColor)
                    graphicsContext.drawOval(p.x - holeDiameter / 2, p.y - holeDiameter / 2, holeDiameter, holeDiameter)
                }
            }
            super.drawCoordinates(graphicsContext, spacing)
        }
    }

    @EditableProperty
    public Size getSpacing() {
        return spacing
    }

    public void setSpacing(Size spacing) {
        this.spacing = spacing
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width
        graphicsContext.setColor(BOARD_COLOR)
        graphicsContext.fillRect(2 / factor, 2 / factor, width - 4 / factor, height - 4  / factor)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawRect(2 / factor, 2 / factor, width - 4 / factor, height - 4  / factor)

        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(width / 3 - 2 / factor, width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawOval(width / 3 - 2 / factor, width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))

        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(2 * width / 3 - 2 / factor, width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawOval(2 * width / 3 - 2 / factor, width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))

        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawOval(width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))

        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(2 * width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawOval(2 * width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
    }
}
