package org.diylc.components.shapes

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Graphics2D
import java.awt.Point
import java.util.Arrays

import org.diylc.common.ObjectCache
import org.diylc.components.AbstractShape
import org.diylc.components.ComponentDescriptor
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "Polygon", author = "Branislav Stojkovic", category = "Shapes", instanceNamePrefix = "POLY", description = "Polygonal area", zOrder = IDIYComponent.COMPONENT, flexibleZOrder = true, bomPolicy = BomPolicy.SHOW_ALL_NAMES, autoEdit = false)
public class Polygon extends AbstractShape {

    private static final long serialVersionUID = 1L

    protected PointCount pointCount = PointCount._4

    public Polygon() {
        super()
        this.controlPoints = [
            new Point(0, 0),
            new Point(0, (int) DEFAULT_HEIGHT.convertToPixels()),
            new Point((int) DEFAULT_WIDTH.convertToPixels(),
            (int) DEFAULT_HEIGHT.convertToPixels()),
            new Point((int) DEFAULT_WIDTH.convertToPixels(), 0) ] as Point[]
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke((int) borderThickness.convertToPixels()))
        graphicsContext.setColor(color)
        int[] xPoints = new int[controlPoints.length]
        int[] yPoints = new int[controlPoints.length]
        for (int i = 0 ;i < controlPoints.length; i++) {
            xPoints[i] = controlPoints[i].x
            yPoints[i] = controlPoints[i].y
        }

        Composite oldComposite = graphicsContext.getComposite()
        if (this.alpha < MAX_ALPHA) {
            graphicsContext.setComposite(AlphaComposite.getInstance(3, 1.0F * this.alpha
                    / MAX_ALPHA))
		}
		graphicsContext.fillPolygon(xPoints, yPoints, controlPoints.length)
		graphicsContext.setComposite(oldComposite)
		
		// Do not track any changes that follow because the whole board has been
            // tracked so far.
            drawingObserver.stopTracking()
            Color lineColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? SELECTION_COLOR : borderColor
            graphicsContext.setColor(lineColor)
            graphicsContext.drawPolygon(xPoints, yPoints, controlPoints.length)
        }

        @EditableProperty(name = "Edges")
        public PointCount getPointCount() {
            return pointCount
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
                    this.controlPoints[i] = new Point(
                            (this.controlPoints[i - 1].x + this.controlPoints[newPointCount - 1].x) / 2,
                            (this.controlPoints[i - 1].y + this.controlPoints[newPointCount - 1].y) / 2)
                }
            }
            this.pointCount = pointCount
        }

        @Override
        public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
            int factor = 32 / width
            graphicsContext.setColor(COLOR)
            def x = [2 / factor, width - 2 / factor, width - 4 / factor, 3 / factor ] as int[]
            def y = [4 / factor, 2 / factor, height - 5 / factor, height - 2 / factor ] as int[]
            graphicsContext.fillPolygon(x, y, 4)
            graphicsContext.setColor(BORDER_COLOR)
            graphicsContext.drawPolygon(x, y, 4)
        }

        public enum PointCount {
            _3, _4, _5, _6, _7, _8

            public String toString() {
                return name().substring(1)
            }
        }
    }
