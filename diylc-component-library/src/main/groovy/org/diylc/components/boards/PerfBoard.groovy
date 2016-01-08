package org.diylc.components.boards

import groovy.transform.CompileStatic
import org.diylc.components.AbstractBoard
import org.diylc.components.ComponentDescriptor
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.utils.Constants

import java.awt.*


@ComponentDescriptor(name = "Perf Board w/ Pads", category = "Boards", author = "Nikolaj Brinch Jørgensen", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated board with solder pads", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class PerfBoard extends AbstractBoard {

    private static final long serialVersionUID = 1L

    public static Color COPPER_COLOR = Color.decode("#DA8A67")

    public static Size SPACING = new Size(0.1d, SizeUnit.in)
    public static Size PAD_SIZE = new Size(0.08d, SizeUnit.in)
    public static Size HOLE_SIZE = new Size(0.7d, SizeUnit.mm)

    // private Area copperArea
    protected Size spacing = SPACING
    protected Color padColor = COPPER_COLOR

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
                AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha.intdiv(MAX_ALPHA))
                graphicsContext.setComposite(composite)
            }

            Point p = new Point(firstPoint)
            int diameter = getClosestOdd((int) PAD_SIZE.convertToPixels())
            int holeDiameter = getClosestOdd((int) HOLE_SIZE.convertToPixels())
            int spacing = (int) this.spacing.convertToPixels()

            while (p.y < secondPoint.y - spacing) {
                p.@x = firstPoint.x as int
                p.@y += spacing

                while (p.x < secondPoint.x - spacing - diameter) {
                    p.@x += spacing

                    graphicsContext.drawFilledOval(p.x - (int) diameter / 2, p.y - (int) diameter / 2, diameter, padColor.darker(), padColor)
                    graphicsContext.drawFilledOval(p.x - (int) holeDiameter / 2, p.y - (int) holeDiameter / 2, holeDiameter, padColor.darker(), Constants.CANVAS_COLOR)
                }
            }

            drawCoordinates(graphicsContext, spacing)
        }
    }

    @EditableProperty(name = "Pad color")
    public Color getPadColor() {
        return padColor
    }

    public void setPadColor(Color padColor) {
        this.padColor = padColor
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
        int factor = (int) 32 / width

        graphicsContext.drawFilledRect((int) 2 / factor, width - (int) 4 / factor, BORDER_COLOR, BOARD_COLOR)
        graphicsContext.drawFilledOval((int) width / 4, (int) width / 2, COPPER_COLOR.darker(), COPPER_COLOR)
        graphicsContext.drawFilledOval((int) width / 2 - (int) 2 / factor, getClosestOdd(5.0 / factor), COPPER_COLOR.darker(), Constants.CANVAS_COLOR)
    }
}
