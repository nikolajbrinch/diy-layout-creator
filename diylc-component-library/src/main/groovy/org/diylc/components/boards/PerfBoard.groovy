package org.diylc.components.boards

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
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

import java.awt.*


@ComponentDescriptor(name = "Perf Board w/ Pads", category = "Boards", author = "Nikolaj Brinch Jørgensen", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated board with solder pads", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class PerfBoard extends AbstractBoard implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color COPPER_COLOR = Color.decode("#DA8A67")

    public static Size SPACING = new Size(0.1d, SizeUnit.in)

    public static Size PAD_SIZE = new Size(0.08d, SizeUnit.in)

    public static Size HOLE_SIZE = new Size(0.7d, SizeUnit.mm)

    @EditableProperty
    Size spacing = SPACING

    @EditableProperty(name = "Pad color")
    Color padColor = COPPER_COLOR

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode, Project project, IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()

        if (checkPointsClipped(clip)
        && !clip.contains(firstPoint.x, secondPoint.y)
        && !clip.contains(secondPoint.x, firstPoint.y)) {
            return
        }

        super.draw(graphicsContext, componentState, outlineMode, project, drawingObserver)

        if (componentState != ComponentState.DRAGGING) {
            Point p = point(firstPoint)
            int diameter = getClosestOdd((int) PAD_SIZE.convertToPixels())
            int holeDiameter = getClosestOdd((int) HOLE_SIZE.convertToPixels())
            int spacing = (int) this.spacing.convertToPixels()

            graphicsContext.with {
                if (alpha < Colors.MAX_ALPHA) {
                    AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toFloat(alpha / Colors.MAX_ALPHA))
                    setComposite(composite)
                }

                while (p.y < secondPoint.y - spacing) {
                    p.@x = firstPoint.x as int
                    p.@y += spacing

                    while (p.x < secondPoint.x - spacing - diameter) {
                        p.@x += spacing

                        drawFilledOval(p.x - diameter / 2, p.y - diameter / 2, diameter, padColor.darker(), padColor)
                        drawFilledOval(p.x - holeDiameter / 2, p.y - holeDiameter / 2, holeDiameter, padColor.darker(), Constants.CANVAS_COLOR)
                    }
                }
            }
            
            drawCoordinates(graphicsContext, spacing)
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width

        graphicsContext.drawFilledRect( 2 / factor, width -  4 / factor, Colors.PCB_BORDER_COLOR, Colors.PCB_BOARD_COLOR)
        graphicsContext.drawFilledOval(width / 4,  width / 2, COPPER_COLOR.darker(), COPPER_COLOR)
        graphicsContext.drawFilledOval(width / 2 - 2 / factor, getClosestOdd(5.0 / factor), COPPER_COLOR.darker(), Constants.CANVAS_COLOR)
    }
}
