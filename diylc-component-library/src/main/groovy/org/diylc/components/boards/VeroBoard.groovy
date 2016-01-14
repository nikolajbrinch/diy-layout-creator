package org.diylc.components.boards

import org.diylc.core.graphics.GraphicsContext

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Point
import java.awt.Shape

import org.diylc.common.OrientationHV
import org.diylc.components.Colors
import org.diylc.components.ComponentDescriptor
import org.diylc.components.AbstractBoard
import org.diylc.components.Geometry
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.utils.Constants

@ComponentDescriptor(name = "Vero Board", category = "Boards", author = "Branislav Stojkovic", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated FR4 board with copper strips connecting all holes in a row", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class VeroBoard extends AbstractBoard implements Geometry {

    private static final long serialVersionUID = 1L

    public static Size SPACING = new Size(0.1d, SizeUnit.in)

    public static Size STRIP_SIZE = new Size(0.07d, SizeUnit.in)

    public static Size HOLE_SIZE = new Size(0.7d, SizeUnit.mm)

    @EditableProperty
    Size spacing = SPACING

    @EditableProperty(name = "Strip color")
    Color stripColor = Colors.PCB_STRIP_COLOR

    @EditableProperty
    OrientationHV orientation = OrientationHV.HORIZONTAL

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()

        if (checkPointsClipped(clip) && !clip.contains(firstPoint.x, secondPoint.y) && !clip.contains(secondPoint.x, firstPoint.y)) {
            return
        }

        super.draw(graphicsContext, componentState, outlineMode, project, drawingObserver)

        if (componentState != ComponentState.DRAGGING) {
            Composite oldComposite = graphicsContext.getComposite()
            if (alpha < Colors.MAX_ALPHA) {
                graphicsContext.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
            }
            Point p = point(firstPoint)
            int stripSize = getClosestOdd((int) STRIP_SIZE.convertToPixels())
            int holeSize = getClosestOdd((int) HOLE_SIZE.convertToPixels())
            int spacing = (int) this.spacing.convertToPixels()

            if (orientation == OrientationHV.HORIZONTAL) {
                while (p.y < secondPoint.y - spacing) {
                    p.x = firstPoint.x
                    p.y += spacing

                    graphicsContext.drawFilledRect(p.x + spacing / 2, p.y - stripSize / 2, secondPoint.x - spacing - p.x, stripSize, stripColor.darker(), stripColor)

                    while (p.x < secondPoint.x - spacing - holeSize) {
                        p.x += spacing

                        graphicsContext.drawFilledOval(p.x - holeSize / 2, p.y - holeSize / 2, holeSize, holeSize, stripColor.darker(), Constants.CANVAS_COLOR)
                    }
                }
            } else {
                while (p.x < secondPoint.x - spacing) {
                    p.x += spacing
                    p.y = firstPoint.y

                    graphicsContext.drawFilledRect(p.x - stripSize / 2, p.y + spacing / 2, stripSize, secondPoint.y - spacing - p.y, stripColor.darker(), stripColor)

                    while (p.y < secondPoint.y - spacing - holeSize) {
                        p.y += spacing

                        graphicsContext.drawFilledOval(p.x - holeSize / 2, p.y - holeSize / 2, holeSize, holeSize, stripColor.darker(), Constants.CANVAS_COLOR)
                    }
                }
            }
            graphicsContext.setComposite(oldComposite)
            super.drawCoordinates(graphicsContext, spacing)
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width
        graphicsContext.drawFilledRect(2 / factor, width - 4 / factor, Colors.PCB_BORDER_COLOR, Colors.PCB_BOARD_COLOR)
        graphicsContext.drawFilledRect(4 / factor, width / 4, width - 8 / factor, width / 2, Colors.PCB_STRIP_COLOR.darker(), Colors.PCB_STRIP_COLOR)
        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval(width / 3 - 2 / factor, width / 2 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.fillOval(2 * width / 3 - 2 / factor, width / 2 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.setColor(Colors.PCB_STRIP_COLOR.darker())
        graphicsContext.drawOval(width / 3 - 2 / factor, width / 2 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
        graphicsContext.drawOval(2 * width / 3 - 2 / factor, width / 2 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor))
    }
}
