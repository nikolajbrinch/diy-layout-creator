package org.diylc.components.arduino

import org.diylc.components.AbstractComponent
import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.Angle
import org.diylc.components.Colors
import org.diylc.components.ComponentDescriptor
import org.diylc.components.AbstractBoard
import org.diylc.components.ControlPoint;
import org.diylc.components.Geometry
import org.diylc.components.Constants.Placement;
import org.diylc.components.Pin;
import org.diylc.components.PinBase;
import org.diylc.core.ComponentState
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

    public static Size HALF_PIN_SPACING = new Size(0.05d, SizeUnit.in)

    public static Size PIN_SPACING = new Size(0.1d, SizeUnit.in)

    public static float LABEL_FONT_SIZE = 8f

    public static Size CHIP_SIZE = new Size(7d, SizeUnit.mm)

    public static int ROW_SPACING = 6

    private String iconText
        
    Point[] controlPoints = points(point(0, 0))

    transient Area[] body

    @EditableProperty
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    protected abstract void updateControlPoints()

    protected abstract Area[] getBodyArea()

    public AbstractArduino(String iconText) {
        super()
        this.iconText = iconText
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

            setColor(Colors.CHIP_COLOR)
            fill(chipArea)
            setColor(Colors.CHIP_BORDER_COLOR)
            draw(chipArea)

            /*
             * Draw control points
             */
            int padSize = toInt(org.diylc.components.Constants.SMALL_PAD_SIZE.convertToPixels())
            int holeSize = toInt(org.diylc.components.Constants.LARGE_HOLE_SIZE.convertToPixels())

            
            controlPoints.each { ControlPoint controlPoint ->
                if (controlPoint.properties['type'] == 'pad') {
                    drawFilledOval(controlPoint.x - padSize / 2, controlPoint.y - padSize / 2, padSize, Colors.SILVER_COLOR.darker(), Colors.SILVER_COLOR)
                    drawFilledOval(controlPoint.x - holeSize / 2, controlPoint.y - holeSize / 2, holeSize, Colors.SILVER_COLOR.darker(), Constants.CANVAS_COLOR)
                } else if (controlPoint.properties['type'] == 'pin') {
                    int pinSize = toInt(Pin.DEFAULT_PIN_SIZE.convertToPixels())
                    int pinBaseSize = toInt(PinBase.DEFAULT_BASE_SIZE.convertToPixels())
                    Area pinBaseArea = getBodyArea()[2]
                    Area pinArea = getBodyArea()[3]
                    int x = toInt(controlPoint.x - pinBaseSize / 2)
                    int y = toInt(controlPoint.y - pinBaseSize / 2)
                    drawArea(graphicsContext, x, y, pinBaseArea, Colors.CHIP_COLOR, Colors.CHIP_BORDER_COLOR)
                    x = toInt(controlPoint.x - pinSize / 2)
                    y = toInt(controlPoint.y - pinSize / 2)
                    drawArea(graphicsContext, x, y, pinArea, , Colors.SILVER_COLOR, Colors.SILVER_COLOR.darker())
                }
                
                if (controlPoint.properties['text']) {
                    Point point = point(controlPoint)
                    HorizontalAlignment horizontalAlignment
                    VerticalAlignment verticalAlignment
                    if (controlPoint.properties['text-placement'] == Placement.BELOW) {
                        point.translate(0, padSize.intdiv(2))
                        horizontalAlignment = HorizontalAlignment.CENTER
                        verticalAlignment = VerticalAlignment.TOP
                    } else if (controlPoint.properties['text-placement'] == Placement.ABOVE) {
                        point.translate(0, (int) -(5 * padSize / 4))
                        horizontalAlignment = HorizontalAlignment.CENTER
                        verticalAlignment = VerticalAlignment.BOTTOM
                    } else if (controlPoint.properties['text-placement'] == Placement.LEFT) {
                        point.translate((int) -(2 * padSize / 3), 0)
                        horizontalAlignment = HorizontalAlignment.RIGHT
                        verticalAlignment = VerticalAlignment.CENTER
                    } else if (controlPoint.properties['text-placement'] == Placement.RIGHT) {
                        point.translate((int) (2 * padSize / 3), 0)
                        horizontalAlignment = HorizontalAlignment.LEFT
                        verticalAlignment = VerticalAlignment.CENTER
                    }
                    setColor(Color.white)
                    setFont(LABEL_FONT.deriveFont(LABEL_FONT_SIZE))
                    drawCenteredText(graphicsContext, controlPoint.properties['text'], point, horizontalAlignment, verticalAlignment)
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
        drawCenteredText(graphicsContext, iconText, point(width / 2, height / 2), HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
    }
}
