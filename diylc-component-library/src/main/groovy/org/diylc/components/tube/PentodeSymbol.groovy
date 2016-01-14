package org.diylc.components.tube

import org.diylc.components.Colors

import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath

import org.diylc.common.ObjectCache
import org.diylc.components.AbstractTubeSymbol
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.core.IDIYComponent
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "Pentode Symbol", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "V", description = "Pentode tube symbol", stretchable = false, zOrder = IDIYComponent.COMPONENT, rotatable = false)
public class PentodeSymbol extends AbstractTubeSymbol implements Geometry {

    private static final long serialVersionUID = 1L

    protected Point[] controlPoints = points(point(0, 0), point(0, 0), point(0, 0), point(0, 0), point(0, 0), point(0, 0), point(0, 0))

    @EditableProperty(name = "Suppressor grid")
    boolean exposeSuppressorGrid = true

    public PentodeSymbol() {
        super()
        updateControlPoints()
    }

    public Shape[] getBody() {
        if (this.@body == null) {
            this.@body = new Shape[3]
            int x = controlPoints[0].x
            int y = controlPoints[0].y
            int pinSpacing = (int) PIN_SPACING.convertToPixels()

            // electrodes
            GeneralPath polyline = new GeneralPath()

            polyline.with {
                // grid 1
                moveTo((double) x + pinSpacing * 5 / 4, (double) y - pinSpacing * 3 / 8)
                lineTo((double) x + pinSpacing * 7 / 4, (double) y - pinSpacing * 3 / 8)
                moveTo((double) x + pinSpacing * 9 / 4, (double) y - pinSpacing * 3 / 8)
                lineTo((double) x + pinSpacing * 11 / 4, (double) y - pinSpacing * 3 / 8)
                moveTo((double) x + pinSpacing * 13 / 4, (double) y - pinSpacing * 3 / 8)
                lineTo((double) x + pinSpacing * 15 / 4, (double) y - pinSpacing * 3 / 8)
                moveTo((double) x + pinSpacing * 17 / 4, (double) y - pinSpacing * 3 / 8)
                lineTo((double) x + pinSpacing * 19 / 4, (double) y - pinSpacing * 3 / 8)

                // grid 2
                moveTo((double) x + pinSpacing * 5 / 4, (double) y - pinSpacing)
                lineTo((double) x + pinSpacing * 7 / 4, (double) y - pinSpacing)
                moveTo((double) x + pinSpacing * 9 / 4, (double) y - pinSpacing)
                lineTo((double) x + pinSpacing * 11 / 4, (double) y - pinSpacing)
                moveTo((double) x + pinSpacing * 13 / 4, (double) y - pinSpacing)
                lineTo((double) x + pinSpacing * 15 / 4, (double) y - pinSpacing)
                moveTo((double) x + pinSpacing * 17 / 4, (double) y - pinSpacing)
                lineTo((double) x + pinSpacing * 19 / 4, (double) y - pinSpacing)

                // grid 3
                moveTo((double) x + pinSpacing * 5 / 4, (double) y - pinSpacing - pinSpacing
                        * 5 / 8)
                lineTo((double) x + pinSpacing * 7 / 4, (double) y - pinSpacing - pinSpacing
                        * 5 / 8)
                moveTo((double) x + pinSpacing * 9 / 4, (double) y - pinSpacing - pinSpacing
                        * 5 / 8)
                lineTo((double) x + pinSpacing * 11 / 4, (double) y - pinSpacing
                        - pinSpacing * 5 / 8)
                moveTo((double) x + pinSpacing * 13 / 4, (double) y - pinSpacing
                        - pinSpacing * 5 / 8)
                lineTo((double) x + pinSpacing * 15 / 4, (double) y - pinSpacing
                        - pinSpacing * 5 / 8)
                moveTo((double) x + pinSpacing * 17 / 4, (double) y - pinSpacing
                        - pinSpacing * 5 / 8)
                lineTo((double) x + pinSpacing * 19 / 4, (double) y - pinSpacing
                        - pinSpacing * 5 / 8)

                // plate
                moveTo((double) x + pinSpacing * 3 / 2, (double) y - pinSpacing * 9 / 4)
                lineTo((double) x + pinSpacing * 9 / 2, (double) y - pinSpacing * 9 / 4)

                // cathode
                moveTo((double) x + pinSpacing * 2, (double) y + pinSpacing * 3 / 8)
                lineTo((double) x + pinSpacing * 4, (double) y + pinSpacing * 3 / 8)
            }
            
            this.@body[0] = polyline

            // connectors
            polyline = new GeneralPath()

            // grid1
            polyline.moveTo((double) x, (double) y)
            polyline.lineTo((double) x + pinSpacing, (double) y)
            polyline.lineTo((double) x + pinSpacing * 5 / 4,(double)  y - pinSpacing * 3 / 8)

            // grid2
            polyline.moveTo((double) controlPoints[3].x, (double) controlPoints[3].y)
            polyline.lineTo((double) x + pinSpacing * 19 / 4, (double) y - pinSpacing)

            // grid3
            if (exposeSuppressorGrid) {
                polyline.moveTo((double) controlPoints[4].x, (double) controlPoints[4].y)
                polyline.lineTo((double) x + pinSpacing, (double) controlPoints[4].y)
                polyline.lineTo((double) x + pinSpacing * 5 / 4, (double) y - pinSpacing
                        - pinSpacing * 5 / 8)
            } else {
                polyline.moveTo((double) x + pinSpacing * 19 / 4, (double) y - pinSpacing
                        - pinSpacing * 5 / 8)
                polyline.lineTo((double) x + pinSpacing * 5, (double) y - pinSpacing - pinSpacing
                        * 5 / 8)
                polyline.lineTo((double) x + pinSpacing * 5, (double) y - pinSpacing * 5 / 4)
                polyline.curveTo((double) x + pinSpacing * 21 / 4, (double) y - pinSpacing * 5 / 4, (double) x + pinSpacing * 21 / 4, (double) y - pinSpacing * 3 / 4, (double) x
                        + pinSpacing * 5, (double) y - pinSpacing * 3 / 4)
                polyline.moveTo((double) x + pinSpacing * 5, (double) y - pinSpacing * 3 / 4)
                polyline.lineTo((double) x + pinSpacing * 5, (double) y + pinSpacing * 3 / 8)
                polyline.lineTo((double) x + pinSpacing * 4, (double) y + pinSpacing * 3 / 8)
            }

            // plate
            polyline.moveTo((double) controlPoints[1].x, (double) controlPoints[1].y)
            polyline.lineTo((double) x + pinSpacing * 3, (double) y - pinSpacing * 9 / 4)

            // cathode
            polyline.moveTo((double) controlPoints[2].x, (double) controlPoints[2].y)
            polyline.lineTo((double) x + pinSpacing * 2, (double) y + pinSpacing * 3 / 8)

            if (showHeaters) {
                polyline.moveTo((double) controlPoints[5].x, (double) controlPoints[5].y)
                polyline.lineTo((double) controlPoints[5].x, (double) controlPoints[5].y
                        - pinSpacing * 6 / 8)
                polyline.lineTo((double) controlPoints[5].x + pinSpacing / 2,
                        (double) controlPoints[5].y - pinSpacing * 10 / 8)

                polyline.moveTo((double) controlPoints[6].x, (double) controlPoints[6].y)
                polyline.lineTo((double) controlPoints[6].x, (double) controlPoints[6].y
                        - pinSpacing * 6 / 8)
                polyline.lineTo((double) controlPoints[6].x - pinSpacing / 2,
                        (double) controlPoints[6].y - pinSpacing * 10 / 8)
            }

            this.@body[1] = polyline

            // bulb
            this.@body[2] = new Ellipse2D.Double(x + pinSpacing / 2, y - pinSpacing
                    * 7 / 2, pinSpacing * 5, pinSpacing * 5)
        }
        return this.@body
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.setColor(Colors.SCHEMATIC_COLOR)

        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))

        graphicsContext.drawLine(width / 4, height / 4, width * 3 / 4, height / 4)
        graphicsContext.drawLine(width / 2, height / 4, width / 2, 0)

        graphicsContext.drawLine(width / 4 + 2 * width / 32, height * 3 / 4, width * 3 / 4
                - 4 * width / 32, height * 3 / 4)
        graphicsContext.drawLine(width / 4 + 2 * width / 32, height * 3 / 4, width / 4 + 2
                * width / 32, height - 1)

        graphicsContext.drawOval(1, 1, width - 1 - 2 * width / 32, height - 1 - 2 * width / 32)

		graphicsContext.drawLine(0, height / 2, width / 8, height / 2)
        graphicsContext.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_BEVEL, 0, [3f ] as float[], 6f))
        graphicsContext.drawLine(width / 8, height / 2, width * 7 / 8, height / 2)
        graphicsContext.drawLine(width / 8, height * 3 / 8, width * 7 / 8, height * 3 / 8)
        graphicsContext.drawLine(width / 8, height * 5 / 8, width * 7 / 8, height * 5 / 8)
    }

    @Override
    public Point getControlPoint(int index) {
        return controlPoints[index]
    }

    @Override
    public int getControlPointCount() {
        return controlPoints.length
    }

    protected void updateControlPoints() {
        int pinSpacing = (int) PIN_SPACING.convertToPixels()
        // Update control points.
        int x = controlPoints[0].x
        int y = controlPoints[0].y

        controlPoints[1].x = x + pinSpacing * 3
        controlPoints[1].y = y - pinSpacing * 4

        controlPoints[2].x = x + pinSpacing * 2
        controlPoints[2].y = y + pinSpacing * 2

        controlPoints[3].x = x + pinSpacing * 6
        controlPoints[3].y = y - pinSpacing

        controlPoints[4].x = x
        controlPoints[4].y = y - pinSpacing * 2

        controlPoints[5].x = x + pinSpacing * 3
        controlPoints[5].y = y + pinSpacing * 2

        controlPoints[6].x = x + pinSpacing * 4
        controlPoints[6].y = y + pinSpacing * 2
    }

    @Override
    public void setControlPoint(Point point, int index) {
        controlPoints[index].setLocation(point)
        // Invalidate body
        body = null
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        if (showHeaters) {
            return !exposeSuppressorGrid && index == 4 ? VisibilityPolicy.NEVER
                    : VisibilityPolicy.WHEN_SELECTED
        } else {
            return index >= 5 || (!exposeSuppressorGrid && index == 4) ? VisibilityPolicy.NEVER
                    : VisibilityPolicy.WHEN_SELECTED
        }
    }

    @Override
    public boolean isControlPointSticky(int index) {
        if (showHeaters) {
            return exposeSuppressorGrid ? true : index != 4
        } else {
            return exposeSuppressorGrid ? index < 5 : index < 4
        }
    }

    @Override
    protected Point getTextLocation() {
        int pinSpacing = (int) PIN_SPACING.convertToPixels()
        return point(controlPoints[0].x + pinSpacing * 5,
                controlPoints[0].y + pinSpacing)
    }

    public void setExposeSuppressorGrid(boolean exposeSuppressorGrid) {
        this.@exposeSuppressorGrid = exposeSuppressorGrid
        this.body = null
    }
}
