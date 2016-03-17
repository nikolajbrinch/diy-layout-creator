package org.diylc.components.boards

import org.diylc.core.graphics.GraphicsContext

import java.awt.AlphaComposite
import java.awt.Point
import java.awt.Shape

import org.diylc.components.Colors
import org.diylc.components.AbstractBoard
import org.diylc.components.Geometry
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants


@ComponentBomPolicy(BomPolicy.SHOW_ONLY_TYPE_NAME)
@ComponentLayer(IDIYComponent.BOARD)
@ComponentDescriptor(name = "Marshall Style Perf Board", category = "Boards", author = "Branislav Stojkovic", instanceNamePrefix = "Board", description = "Perforated board as found on some Marshall and Trainwreck amps")
public class MarshallPerfBoard extends AbstractBoard implements Geometry {

    public static final String id = "49ec271c-36b6-4076-86e3-5d26ef5e5562"
    
    private static final long serialVersionUID = 1L

    public static Size SPACING = new Size(3 / 8d, SizeUnit.in)

    public static Size HOLE_SIZE = new Size(1 / 8d, SizeUnit.in)

    
    @EditableProperty
    Size spacing = SPACING

    public MarshallPerfBoard() {
        super()
        this.boardColor = Colors.MARSHALL_BOARD_COLOR
        this.borderColor = Colors.MARSHALL_BORDER_COLOR
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
            if (alpha < Colors.MAX_ALPHA) {
                graphicsContext.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
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

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width
        graphicsContext.setColor(Colors.MARSHALL_BOARD_COLOR)
        graphicsContext.fillRect(2 / factor, 2 / factor, width - 4 / factor, height - 4  / factor)
        graphicsContext.setColor(Colors.MARSHALL_BORDER_COLOR)
        graphicsContext.drawRect(2 / factor, 2 / factor, width - 4 / factor, height - 4  / factor)

        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(width / 3 - 2 / factor, width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(Colors.MARSHALL_BORDER_COLOR)
        graphicsContext.drawOval(width / 3 - 2 / factor, width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))

        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(2 * width / 3 - 2 / factor, width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(Colors.MARSHALL_BORDER_COLOR)
        graphicsContext.drawOval(2 * width / 3 - 2 / factor, width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))

        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(Colors.MARSHALL_BORDER_COLOR)
        graphicsContext.drawOval(width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))

        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(2 * width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(Colors.MARSHALL_BORDER_COLOR)
        graphicsContext.drawOval(2 * width / 3 - 2 / factor, 2 * width / 3 - 2 / factor,
                getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
    }
}
