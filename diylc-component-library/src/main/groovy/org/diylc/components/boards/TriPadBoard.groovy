package org.diylc.components.boards

import org.diylc.common.OrientationHV
import org.diylc.components.Colors
import org.diylc.components.ComponentDescriptor
import org.diylc.components.AbstractBoard
import org.diylc.components.Geometry
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.utils.Constants

import java.awt.*

@ComponentDescriptor(name = "TriPad Board", category = "Boards", author = "Hauke Juhls", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated FR4 board with copper strips connecting 3 holes in a row (aka TriPad Board)")
public class TriPadBoard extends AbstractBoard implements Geometry {

    private static final long serialVersionUID = 1L

    public static Size SPACING = new Size(0.1d, SizeUnit.in)
    public static Size STRIP_SIZE = new Size(0.07d, SizeUnit.in)
    public static Size HOLE_SIZE = new Size(0.7d, SizeUnit.mm)

    @EditableProperty(name = "Holes per strip")
    int stripSpan = 3 // determines how many holes are covered by a

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
        if (checkPointsClipped(clip)
        && !clip.contains(firstPoint.x, secondPoint.y)
        && !clip.contains(secondPoint.x, firstPoint.y)) {
            return
        }
        super.draw(graphicsContext, componentState, outlineMode, project, drawingObserver)
        if (componentState != ComponentState.DRAGGING) {
            graphicsContext.with {
                Composite oldComposite = getComposite()

                if (alpha < Colors.MAX_ALPHA) {
                    setComposite(AlphaComposite.getInstance(
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

                        while (p.x + spacing < secondPoint.x) {

                            int remainingSpace = secondPoint.x - p.x
                            int spacesToDraw = stripSpan

                            if (remainingSpace < (stripSize + (stripSpan * spacing))) {
                                spacesToDraw = (remainingSpace - stripSize) / spacing
                            }

                            drawFilledRect(p.x + spacing - stripSize / 2, p.y
                                    - stripSize / 2, spacing * (spacesToDraw - 1)
                                    + stripSize, stripSize, stripColor.darker(), stripColor)

                            p.x += spacing * spacesToDraw
                        }

                        // draw holes
                        p.x = firstPoint.x

                        while (p.x < secondPoint.x - spacing - holeSize) {
                            p.x += spacing
                            drawFilledOval(p.x - holeSize / 2, p.y - holeSize / 2,
                                    holeSize, holeSize, stripColor.darker(), Constants.CANVAS_COLOR)
                        }
                    }
                } else {
                    while (p.x < secondPoint.x - spacing) {
                        p.x += spacing
                        p.y = firstPoint.y

                        while (p.y + spacing < secondPoint.y) {

                            int remainingSpace = secondPoint.y - p.y
                            int spacesToDraw = stripSpan

                            if (remainingSpace < (stripSize + (stripSpan * spacing))) {
                                spacesToDraw = (remainingSpace - stripSize) / spacing
                            }

                            drawFilledRect(p.x - stripSize / 2, p.y + spacing
                                    - stripSize / 2, stripSize, spacing
                                    * (spacesToDraw - 1) + stripSize, stripColor.darker(), stripColor)

                            p.y += spacing * spacesToDraw
                        }

                        // draw holes
                        p.y = firstPoint.y

                        while (p.y < secondPoint.y - spacing - holeSize) {
                            p.y += spacing
                            drawFilledOval(p.x - holeSize / 2, p.y - holeSize / 2,
                                    holeSize, holeSize, stripColor.darker(), Constants.CANVAS_COLOR)
                        }
                    }
                }
                setComposite(oldComposite)
            }
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.with {
            setColor(Colors.PCB_BOARD_COLOR)
            fillRect(0, 0, width, height)

            final int horizontalSpacing = width / 5
            final int horizontalIndent = horizontalSpacing / 2

            final int verticalSpacing = height / 5
            final int verticalIndent = verticalSpacing / 2

            for (int row = 0; row < 5; row++) {
                setColor(Colors.PCB_STRIP_COLOR)
                fillRect(0, row * verticalSpacing + 2, horizontalIndent / 2
                        + horizontalSpacing, verticalSpacing - 1)

                setColor(Colors.PCB_STRIP_COLOR)
                fillRect(horizontalSpacing + 2, row * verticalSpacing + 2,
                        horizontalSpacing * 3 - 1, verticalSpacing - 1)

                fillRect(horizontalSpacing * 4 + 2, row * verticalSpacing + 2,
                        horizontalIndent / 2 + horizontalSpacing,
                        verticalSpacing - 1)
            }

            // draw dots
            for (int row = 0; row < 5; row++) {
                int y = (verticalSpacing * row) + verticalIndent
                for (int col = 0; col < 5; col++) {
                    int x = (horizontalSpacing * col) + horizontalIndent
                    drawFilledOval(x, y, 2, 2, Colors.PCB_STRIP_COLOR.darker(), Constants.CANVAS_COLOR)
                }
            }
        }
    }

    public void setStripSpan(int stripSpan) {
        if (stripSpan < 1) {
            this.stripSpan = 1
        } else {
            this.stripSpan = stripSpan
        }
    }
}
