package org.diylc.components.boards

import org.diylc.core.graphics.GraphicsContext

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Point
import java.awt.Shape

import org.diylc.components.Colors
import org.diylc.components.AbstractBoard
import org.diylc.components.Geometry
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.components.ComponentState;
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

@ComponentDescriptor(name = "Eyelet Board", category = "Boards", author = "Branislav Stojkovic", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated board with eyelets", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class EyeletBoard extends AbstractBoard implements Geometry {

    public static final String id = "7acf925a-8823-4005-86d7-6390a1dfea29"
    
    private static final long serialVersionUID = 1L

    private static Size SPACING = new Size(0.5d, SizeUnit.in)

    private static Size EYELET_SIZE = new Size(0.2d, SizeUnit.in)

    private static Size HOLE_SIZE = new Size(0.1d, SizeUnit.in)

    @EditableProperty
    Size spacing = SPACING

    @EditableProperty(name = "Eyelet color")
    Color eyeletColor = Colors.EYELET_COLOR

    public EyeletBoard() {
        super()
        this.boardColor = Colors.EYELET_BOARD_COLOR
        this.borderColor = Colors.EYELET_BORDER_COLOR
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
                if (alpha < Colors.MAX_ALPHA) {
                    setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
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

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width
        graphicsContext.with {
            drawFilledRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor, Colors.EYELET_BORDER_COLOR, Colors.EYELET_BOARD_COLOR)
            drawFilledOval(width / 4, width / 4, width / 2, width / 2, Colors.EYELET_COLOR.darker(), Colors.EYELET_COLOR)
            drawFilledOval(width / 2 - 2 / factor, width / 2 - 2 / factor, getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor), Colors.EYELET_COLOR.darker(), Constants.CANVAS_COLOR)
        }
    }
}
