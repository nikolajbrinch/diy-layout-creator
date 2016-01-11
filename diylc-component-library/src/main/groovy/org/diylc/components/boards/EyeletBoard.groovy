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

@ComponentDescriptor(name = "Eyelet Board", category = "Boards", author = "Branislav Stojkovic", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated board with eyelets", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class EyeletBoard extends AbstractBoard implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color BOARD_COLOR = Color.decode("#CCFFCC")
    public static Color BORDER_COLOR = BOARD_COLOR.darker()
    public static Color EYELET_COLOR = Color.decode("#C3E4ED")

    public static Size SPACING = new Size(0.5d, SizeUnit.in)
    public static Size EYELET_SIZE = new Size(0.2d, SizeUnit.in)
    public static Size HOLE_SIZE = new Size(0.1d, SizeUnit.in)

    // private Area copperArea
    protected Size spacing = SPACING
    protected Color eyeletColor = EYELET_COLOR

    public EyeletBoard() {
        super()
        this.boardColor = BOARD_COLOR
        this.borderColor = BORDER_COLOR
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
            Project project, IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()
        if (checkPointsClipped(clip) && !clip.contains(firstPoint.x, secondPoint.y) && !clip.contains(secondPoint.x, firstPoint.y)) {
            return
        }

        super.draw(graphicsContext, componentState, outlineMode, project, drawingObserver)

        if (componentState != ComponentState.DRAGGING) {
            Point p = point(firstPoint)
            int diameter = getClosestOdd((int) EYELET_SIZE.convertToPixels())
            int holeDiameter = getClosestOdd((int) HOLE_SIZE.convertToPixels())
            int spacing = (int) this.spacing.convertToPixels()

            graphicsContext.with {
                if (alpha < MAX_ALPHA) {
                    setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / MAX_ALPHA))
                }

                while (p.y < secondPoint.y - spacing) {
                    p.x = firstPoint.x
                    p.y += spacing
                    while (p.x < secondPoint.x - spacing - diameter) {
                        p.x += spacing
                        drawFilledOval(p.x - diameter / 2, p.y - diameter / 2, diameter, diameter, eyeletColor.darker(), eyeletColor)
                        drawFilledOval(p.x - holeDiameter / 2, p.y - holeDiameter / 2, holeDiameter, holeDiameter, eyeletColor.darker(), Constants.CANVAS_COLOR)
                    }
                }

            }
            
            super.drawCoordinates(graphicsContext, spacing)
        }
    }

    @EditableProperty(name = "Eyelet color")
    public Color getEyeletColor() {
        return eyeletColor
    }

    public void setEyeletColor(Color padColor) {
        this.eyeletColor = padColor
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
        graphicsContext.with {
            drawFilledRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor, BORDER_COLOR, BOARD_COLOR)
            drawFilledOval(width / 4, width / 4, width / 2, width / 2, EYELET_COLOR.darker(), EYELET_COLOR)
            drawFilledOval(width / 2 - 2 / factor, width / 2 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), EYELET_COLOR.darker(), Constants.CANVAS_COLOR)
        }
    }
}
