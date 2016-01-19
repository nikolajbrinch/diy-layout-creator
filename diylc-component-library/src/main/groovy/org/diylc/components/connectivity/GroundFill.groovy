package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.util.Arrays

import org.diylc.components.AbstractComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.components.PCBLayer
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Project
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Ground Fill", author = "Branislav Stojkovic", category = "Connectivity", instanceNamePrefix = "GF", description = "Polygonal ground fill area", zOrder = IDIYComponent.TRACE, bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false)
public class GroundFill extends AbstractComponent implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color COLOR = Color.black
    public static Size DEFAULT_WIDTH = new Size(1.5d, SizeUnit.in)
    public static Size DEFAULT_HEIGHT = new Size(1.2d, SizeUnit.in)

    protected Point[] controlPoints = points(
        point(0, 0),
        point(0, (int) DEFAULT_HEIGHT.convertToPixels()),
        point((int) DEFAULT_WIDTH.convertToPixels(),
        (int) DEFAULT_HEIGHT.convertToPixels()),
        point((int) DEFAULT_WIDTH.convertToPixels(), 0))

    @EditableProperty(name = "Color")
    Color color = COLOR

    @EditableProperty
    PCBLayer layer = PCBLayer._1

    @EditableProperty(name = "Edges")
    PointCount pointCount = PointCount._4

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        Color fillColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : color
        graphicsContext.setColor(fillColor)
        int[] xPoints = new int[controlPoints.length]
        int[] yPoints = new int[controlPoints.length]
        for (int i = 0; i < controlPoints.length; i++) {
            xPoints[i] = controlPoints[i].x
            yPoints[i] = controlPoints[i].y
        }
        graphicsContext.fillPolygon(xPoints, yPoints, controlPoints.length)
        // Do not track any changes that follow because the whole board has been
        // tracked so far.
        drawingObserver.stopTracking()
    }


    @Override
    public int getControlPointCount() {
        return controlPoints.length
    }

    @Override
    public Point getControlPoint(int index) {
        return controlPoints[index]
    }

    @Override
    public boolean isControlPointSticky(int index) {
        return false
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.WHEN_SELECTED
    }

    @Override
    public void setControlPoint(Point point, int index) {
        controlPoints[index].setLocation(point)
    }

    public void setPointCount(PointCount pointCount) {
        if (this.pointCount == pointCount)
            return
        int oldPointCount = Integer.parseInt(this.pointCount.toString())
        int newPointCount = Integer.parseInt(pointCount.toString())
        this.controlPoints = Arrays.copyOf(this.controlPoints, newPointCount)
        if (oldPointCount < newPointCount) {
            this.controlPoints[newPointCount - 1] = this.controlPoints[oldPointCount - 1]
            for (int i = oldPointCount - 1; i < newPointCount - 1; i++) {
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
        graphicsContext.setColor(COLOR)
        def x = [2 / factor, width - 2 / factor, width - 4 / factor, 3 / factor] as int[]
        def y = [4 / factor, 2 / factor, height - 5 / factor, height - 2 / factor] as int[]
        graphicsContext.fillPolygon(x, y, 4)
    }

    public enum PointCount {
        _3, _4, _5, _6, _7, _8

        public String toString() {
            return name().substring(1)
        }
    }
}
