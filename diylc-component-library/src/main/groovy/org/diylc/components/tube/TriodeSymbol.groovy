package org.diylc.components.tube

import java.awt.BasicStroke
import java.awt.Point
import java.awt.Shape
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath

import org.diylc.components.AbstractTubeSymbol
import org.diylc.components.Colors
import org.diylc.components.Geometry
import org.diylc.core.ObjectCache
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.annotations.ComponentAutoEdit
import org.diylc.core.components.annotations.ComponentDescriptor
import org.diylc.core.components.annotations.ComponentEditOptions
import org.diylc.core.components.annotations.ComponentPads
import org.diylc.core.components.properties.EditableProperty
import org.diylc.core.graphics.GraphicsContext

@ComponentAutoEdit
@ComponentPads(false)
@ComponentEditOptions(stretchable = false, rotatable = false)
@ComponentDescriptor(name = "Triode Symbol", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "V", description = "Triode tube symbol")
public class TriodeSymbol extends AbstractTubeSymbol implements Geometry {

    public static final String id = "dae9ad67-a50b-480e-802f-9b451ae6cf78"
    
    private static final long serialVersionUID = 1L

    protected Point[] controlPoints = points(point(0, 0),
    point(0, 0), point(0, 0), point(0, 0), point(0, 0))

    @EditableProperty(name = "Directly heated")
    boolean directlyHeated = false

    public TriodeSymbol() {
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

                // grid
                moveTo((double) x + pinSpacing * 5 / 4, (double) y)
                lineTo((double) x + pinSpacing * 7 / 4, (double) y)
                moveTo((double) x + pinSpacing * 9 / 4, (double) y)
                lineTo((double) x + pinSpacing * 11 / 4, (double) y)
                moveTo((double) x + pinSpacing * 13 / 4, (double) y)
                lineTo((double) x + pinSpacing * 15 / 4, (double) y)
                moveTo((double) x + pinSpacing * 17 / 4, (double) y)
                lineTo((double) x + pinSpacing * 19 / 4, (double) y)

                // plate
                moveTo((double) x + pinSpacing * 3 / 2, (double) y - pinSpacing)
                lineTo((double) x + pinSpacing * 9 / 2, (double) y - pinSpacing)

                // cathode
                if (directlyHeated) {
                    moveTo((double) controlPoints[2].x, (double) controlPoints[2].y - pinSpacing)
                    lineTo((double) controlPoints[2].x + pinSpacing, (double) controlPoints[2].y - pinSpacing * 2)
                    lineTo((double) controlPoints[4].x, (double) controlPoints[4].y - pinSpacing)
                } else {
                    moveTo((double) x + pinSpacing * 2, (double) y + pinSpacing)
                    lineTo((double) x + pinSpacing * 4, (double) y + pinSpacing)
                }
            }
            this.@body[0] = polyline

            // connectors
            polyline = new GeneralPath()
            polyline.with {

                // grid
                moveTo((double) x, (double) y)
                lineTo((double) x + pinSpacing, (double) y)

                // plate
                moveTo((double) controlPoints[1].x, (double) controlPoints[1].y)
                lineTo((double) x + pinSpacing * 3, (double) y - pinSpacing)

                // cathode
                if (directlyHeated) {
                    moveTo((double) controlPoints[2].x, (double) controlPoints[2].y)
                    lineTo((double) controlPoints[2].x, (double) controlPoints[2].y - pinSpacing)
                    moveTo((double) controlPoints[4].x, (double) controlPoints[4].y)
                    lineTo((double) controlPoints[4].x, (double) controlPoints[4].y - pinSpacing)
                } else {
                    moveTo((double) controlPoints[2].x, (double) controlPoints[2].y)
                    lineTo((double) x + pinSpacing * 2, (double) y + pinSpacing)

                    if (showHeaters) {
                        moveTo((double) controlPoints[3].x, (double) controlPoints[3].y)
                        lineTo((double) controlPoints[3].x, (double) controlPoints[3].y - pinSpacing)
                        lineTo((double) controlPoints[3].x + pinSpacing / 2, (double) controlPoints[3].y - 3 * pinSpacing / 2)

                        moveTo((double) controlPoints[4].x, (double) controlPoints[4].y)
                        lineTo((double) controlPoints[4].x, (double) controlPoints[4].y - pinSpacing)
                        lineTo((double) controlPoints[4].x - pinSpacing / 2, (double) controlPoints[4].y - 3 * pinSpacing / 2)
                    }
                }

            }

            this.@body[1] = polyline

            // bulb
            this.@body[2] = new Ellipse2D.Double(x + pinSpacing / 2, y - pinSpacing
                    * 5 / 2, pinSpacing * 5, pinSpacing * 5)
        }
        return this.@body
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.with {
            setColor(Colors.SCHEMATIC_COLOR)
            setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
            drawLine(width / 4, height / 4, width * 3 / 4, height / 4)
            drawLine(width / 2, height / 4, width / 2, 0)
            drawLine(width / 4 + 2 * width / 32, height * 3 / 4, width * 3 / 4 - 4 * width / 32, height * 3 / 4)
            drawLine(width / 4 + 2 * width / 32, height * 3 / 4, width / 4 + 2 * width / 32, height - 1)
            drawOval(1, 1, width - 1 - 2 * width / 32, height - 1 - 2 * width / 32)
            drawLine(0, height / 2, width / 8, height / 2)
            setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, [3f ] as float[], 6f))
            drawLine(width / 8, height / 2, width * 7 / 8, height / 2)
        }
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
        controlPoints[1].y = y - pinSpacing * 3

        controlPoints[2].x = x + pinSpacing * 2
        controlPoints[2].y = y + pinSpacing * 3

        controlPoints[3].x = x + pinSpacing * 3
        controlPoints[3].y = y + pinSpacing * 3

        controlPoints[4].x = x + pinSpacing * 4
        controlPoints[4].y = y + pinSpacing * 3
    }

    @Override
    public void setControlPoint(Point point, int index) {
        controlPoints[index].setLocation(point)
        // Invalidate body
        body = null
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        if (directlyHeated) {
            return index != 3 ? VisibilityPolicy.WHEN_SELECTED
                    : VisibilityPolicy.NEVER
        } else if (showHeaters) {
            return VisibilityPolicy.WHEN_SELECTED
        } else {
            return index < 3 ? VisibilityPolicy.WHEN_SELECTED
                    : VisibilityPolicy.NEVER
        }
    }

    @Override
    public boolean isControlPointSticky(int index) {
        if (directlyHeated) {
            return index != 3
        } else if (showHeaters) {
            return true
        } else {
            return index < 3
        }
    }

    @Override
    protected Point getTextLocation() {
        int pinSpacing = (int) PIN_SPACING.convertToPixels()
        return point(controlPoints[0].x + pinSpacing * 5,
                controlPoints[0].y + pinSpacing * 2)
    }

    public void setDirectlyHeated(boolean directlyHeated) {
        this.@directlyHeated = directlyHeated
        // Invalidate body
        body = null
    }
}
