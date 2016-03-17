package org.diylc.components.shapes

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite

import org.diylc.components.AbstractShape
import org.diylc.components.Geometry
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Project
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext

@ComponentLayer(flexible = true)
@ComponentDescriptor(name = "Polygon", author = "Branislav Stojkovic", category = "Shapes", instanceNamePrefix = "POLY", description = "Polygonal area")
public class Polygon extends AbstractShape implements Geometry {

    public static final String id = "c96fdbc4-1831-4a4b-b6c4-1c512f694500"
    
    private static final long serialVersionUID = 1L

    @EditableProperty(name = "Edges")
    PointCount pointCount = PointCount._4

    public Polygon() {
        super()
        this.controlPoints = points(point(0, 0), point(0, DEFAULT_HEIGHT.convertToPixels()), point(DEFAULT_WIDTH.convertToPixels(), DEFAULT_HEIGHT.convertToPixels()), point(DEFAULT_WIDTH.convertToPixels(), 0))
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        graphicsContext.with {
            setStroke(ObjectCache.getInstance().fetchBasicStroke((int) borderThickness.convertToPixels()))
            setColor(color)
            int[] xPoints = new int[controlPoints.length]
            int[] yPoints = new int[controlPoints.length]

            for (int i = 0 ;i < controlPoints.length; i++) {
                xPoints[i] = controlPoints[i].x
                yPoints[i] = controlPoints[i].y
            }

            Composite oldComposite = getComposite()
            if (this.alpha < Colors.MAX_ALPHA) {
                setComposite(AlphaComposite.getInstance(3, 1.0F * this.alpha / Colors.MAX_ALPHA))
            }
            fillPolygon(xPoints, yPoints, controlPoints.length)
            setComposite(oldComposite)

            /* 
             * Do not track any changes that follow because the whole board has been
             * tracked so far.
             */
            drawingObserver.stopTracking()
            Color lineColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : borderColor
            setColor(lineColor)
            drawPolygon(xPoints, yPoints, controlPoints.length)
        }
    }

    public void setPointCount(PointCount pointCount) {
        if (this.pointCount == pointCount)
            return
        int oldPointCount = Integer.parseInt(this.pointCount.toString())
        int newPointCount = Integer.parseInt(pointCount.toString())
        this.controlPoints = Arrays.copyOf(this.controlPoints, newPointCount)
        if (oldPointCount < newPointCount) {
            this.controlPoints[newPointCount - 1] = this.controlPoints[oldPointCount - 1]
            for (int i = oldPointCount - 1 ;i < newPointCount - 1; i++) {
                this.controlPoints[i] = point(
                        (this.controlPoints[i - 1].x + this.controlPoints[newPointCount - 1].x) / 2,
                        (this.controlPoints[i - 1].y + this.controlPoints[newPointCount - 1].y) / 2)
            }
        }
        this.pointCount = pointCount
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width
        graphicsContext.with {
            setColor(Colors.SHAPE_FILL_COLOR)
            def x = [2 / factor, width - 2 / factor, width - 4 / factor, 3 / factor ] as int[]
            def y = [4 / factor, 2 / factor, height - 5 / factor, height - 2 / factor ] as int[]
            fillPolygon(x, y, 4)
            setColor(Colors.SHAPE_BORDER_COLOR)
            drawPolygon(x, y, 4)
        }
    }

    public enum PointCount {
        _3, _4, _5, _6, _7, _8

        public String toString() {
            return name().substring(1)
        }
    }
}
