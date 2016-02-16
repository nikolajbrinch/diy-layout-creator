package org.diylc.components.arduino

import org.diylc.components.AbstractComponent
import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.Angle
import org.diylc.components.Colors
import org.diylc.components.ComponentDescriptor
import org.diylc.components.AbstractBoard
import org.diylc.components.Geometry
import org.diylc.components.arduino.PcbText.Placement
import org.diylc.core.ComponentState
import org.diylc.core.ControlPoint
import org.diylc.core.HorizontalAlignment
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Orientation
import org.diylc.core.Project
import org.diylc.core.VerticalAlignment
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.util.List


public abstract class AbstractArduino extends AbstractTransparentComponent implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color SILVER_COLOR = Color.decode("#C0C0C0")

    public static Color CHIP_COLOR = Color.gray

    public static Color CHIP_BORDER_COLOR = Color.gray.darker()

    public static Size PIN_SPACING = new Size(0.05d, SizeUnit.in)

    public static Size SPACING = new Size(0.1d, SizeUnit.in)

    public static Size PAD_SIZE = new Size(0.07d, SizeUnit.in)

    public static Size HOLE_SIZE = new Size(1.33d, SizeUnit.mm)

    public static float LABEL_FONT_SIZE = 8f

    public static Size CHIP_SIZE = new Size(7d, SizeUnit.mm)

    public static int PIN_WIDTH = 6
    
    Point[] controlPoints = points(point(0, 0))

    Map<Point, PcbText> labels = [:]

    transient Area[] body

    @EditableProperty
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    protected abstract void updateControlPoints()

    protected abstract Area[] getBodyArea()

    protected abstract String getIconText()

    public AbstractArduino() {
        super()
        updateControlPoints()
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation
        updateControlPoints()
        body = null
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
        return true
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.NEVER
    }

    @Override
    public void setControlPoint(Point point, int index) {
        controlPoints[index].setLocation(point)
        body = null
        if (index == 0) {
            updateControlPoints()
        }
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode, Project project, IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()

        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }

        Area mainArea = getBodyArea()[0]
        Area chipArea = getBodyArea()[1]

        graphicsContext.with {
            Composite oldComposite = graphicsContext.getComposite()

            if (alpha < Colors.MAX_ALPHA) {
                AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toFloat(alpha / Colors.MAX_ALPHA))
                setComposite(composite)
            }

            setColor(Colors.PCB_BLUE_COLOR)
            fill(mainArea)

            setColor(CHIP_COLOR)
            fill(chipArea)
            setColor(CHIP_BORDER_COLOR)
            draw(chipArea)

            /*
             * Draw control points
             */
            int padSize = (int) PAD_SIZE.convertToPixels()
            int holeSize = (int) HOLE_SIZE.convertToPixels()

            controlPoints.each { Point controlPoint ->
                drawFilledOval(controlPoint.x - padSize / 2, controlPoint.y - padSize / 2, padSize, SILVER_COLOR.darker(), SILVER_COLOR)
                drawFilledOval(controlPoint.x - holeSize / 2, controlPoint.y - holeSize / 2, holeSize, SILVER_COLOR.darker(), Constants.CANVAS_COLOR)

                PcbText label = labels[(controlPoint)]
                
                if (label) {
                    Point point = point(controlPoint)
                    HorizontalAlignment horizontalAlignment
                    VerticalAlignment verticalAlignment
                    if (label.placement == Placement.BELOW) {
                        point.translate(0, padSize.intdiv(2))
                        horizontalAlignment = HorizontalAlignment.CENTER
                        verticalAlignment = VerticalAlignment.TOP
                    } else if (label.placement == Placement.ABOVE) {
                        point.translate(0, (int) -(5 * padSize / 4))
                        horizontalAlignment = HorizontalAlignment.CENTER
                        verticalAlignment = VerticalAlignment.BOTTOM
                    } else if (label.placement == Placement.LEFT) {
                        point.translate((int) -(2 * padSize / 3), 0)
                        horizontalAlignment = HorizontalAlignment.RIGHT
                        verticalAlignment = VerticalAlignment.CENTER
                    } else if (label.placement == Placement.RIGHT) {
                        point.translate((int) (2 * padSize / 3), 0)
                        horizontalAlignment = HorizontalAlignment.LEFT
                        verticalAlignment = VerticalAlignment.CENTER
                    }
                    setColor(Color.white)
                    setFont(LABEL_FONT.deriveFont(LABEL_FONT_SIZE))
                    drawCenteredText(graphicsContext, label.text, point, horizontalAlignment, verticalAlignment)
                }
            }

            setComposite(oldComposite)
            setColor(componentState == ComponentState.SELECTED ? Colors.SELECTION_COLOR : Colors.PCB_BLUE_BORDER_COLOR)
            draw(mainArea)
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.with {
            drawFilledRect(1, height / 4, width - 2, height / 2, Colors.PCB_BLUE_BORDER_COLOR, Colors.PCB_BLUE_COLOR)
        }
        graphicsContext.setColor(Color.white)
        graphicsContext.setFont(LABEL_FONT.deriveFont(LABEL_FONT_SIZE))
        drawCenteredText(graphicsContext, getIconText(), point(width / 2, height / 2), HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
    }
}
