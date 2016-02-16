package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.AlphaComposite;
import java.awt.Color
import java.awt.Composite;
import java.awt.Point
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.Map;

import org.diylc.components.AbstractComponent
import org.diylc.components.AbstractTransparentComponent;
import org.diylc.components.ComponentDescriptor
import org.diylc.components.ControlPoint;
import org.diylc.components.Geometry;
import org.diylc.components.PCBLayer
import org.diylc.components.Pin;
import org.diylc.components.PinBase;
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Orientation;
import org.diylc.core.Project
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

@ComponentDescriptor(name = "Pin header", category = "Connectivity", author = "Nikolaj Brinch Jørgensen", description = "Pin header male", instanceNamePrefix = "Header", stretchable = false, zOrder = IDIYComponent.COMPONENT, bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false)
public class PinHeader extends AbstractTransparentComponent implements Geometry {

    private static final long serialVersionUID = 1L

    public static Size PIN_SPACING = new Size(0.1d, SizeUnit.in)

    public static Color COLOR = Color.black

    ControlPoint[] controlPoints = points(point(0, 0))

    transient Area[] body

    @EditableProperty
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    @EditableProperty
    RowPinCount rowPinCount = RowPinCount._4

    @EditableProperty
    ColumnPinCount columnPinCount = ColumnPinCount._1

    public PinHeader() {
        super()
        updateControlPoints()
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation
        updateControlPoints()
        body = null
    }

    public void setRowPinCount(RowPinCount rowPinCount) {
        this.rowPinCount = rowPinCount
        updateControlPoints()
        body = null
    }

    public void setColumnPinCount(ColumnPinCount columnPinCount) {
        this.columnPinCount = columnPinCount
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

    private void updateControlPoints() {
        Point firstPoint = controlPoints[0]

        int x = firstPoint.x
        int y = firstPoint.y

        Point[] controlPoints = new Point[columnPinCount.value * rowPinCount.value]

        int spacing = (int) PIN_SPACING.convertToPixels()

        for(int i = 0; i < columnPinCount.value; i++) {
            for(int j = 0; j < rowPinCount.value; j++) {
                switch(orientation) {
                    case Orientation.DEFAULT:
                    case Orientation._180:
                        controlPoints[i * rowPinCount.value + j] = point(x + j * spacing, y + i * spacing)
                        break
                    case Orientation._90:
                    case Orientation._270:
                        controlPoints[i * rowPinCount.value + j] = point(x + i * spacing, y + j * spacing)
                        break
                    default:
                        throw new RuntimeException("Unexpected orientation: " + orientation)
                }
            }
        }

        this.controlPoints = controlPoints
    }

    private Area[] getBodyArea() {
        if (body == null) {
            updateControlPoints()
            body = [ new Area(new PinBase()), new Area(new Pin()) ] as Area[]
        }

        return body
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode, Project project, IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()

        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }

        Area baseArea = getBodyArea()[0]
        Area pinArea = getBodyArea()[1]
        
        graphicsContext.with {
            Composite oldComposite = graphicsContext.getComposite()

            if (alpha < Colors.MAX_ALPHA) {
                AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toFloat(alpha / Colors.MAX_ALPHA))
                setComposite(composite)
            }

            /*
             * Draw control points
             */
            int pinSize = toInt(Pin.DEFAULT_PIN_SIZE.convertToPixels())
            int headerSize = toInt(PinBase.DEFAULT_BASE_SIZE.convertToPixels())

            int left = controlPoints[0].x
            int top = controlPoints[0].y
            int right = controlPoints[0].x
            int bottom = controlPoints[0].y

            controlPoints.each { Point controlPoint ->
                int x = toInt(controlPoint.x - headerSize / 2)
                int y = toInt(controlPoint.y - headerSize / 2)
                drawArea(graphicsContext, x, y, baseArea, Colors.CHIP_COLOR, Colors.CHIP_BORDER_COLOR)
                
                left = Math.min(left, x)
                top = Math.min(top, y)
                right = Math.max(right, x)
                bottom = Math.max(bottom, y)

                x = toInt(controlPoint.x - pinSize / 2)
                y = toInt(controlPoint.y - pinSize / 2)
                drawArea(graphicsContext, x, y, pinArea, Colors.SILVER_COLOR, Colors.SILVER_COLOR.darker())
            }
            
            setComposite(oldComposite)

            if (componentState == ComponentState.SELECTED) {
                Rectangle bounds = rectangle(left, top, right - left + headerSize, bottom - top + headerSize / 2)
                setColor(Colors.SELECTION_COLOR)
                draw(bounds)
            }
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int diameter = getClosestOdd(width / 2)
        int holeDiameter = 5
        graphicsContext.setColor(COLOR)
        graphicsContext.fillOval((int) (width - diameter) / 2, (int) (height - diameter) / 2, diameter, diameter)
        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval((int) (width - holeDiameter) / 2, (int) (height - holeDiameter) / 2, holeDiameter, holeDiameter)
    }

    public static enum RowPinCount {
        _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20,
        _21, _22, _23, _24, _25, _26, _27, _28, _29, _30, _31, _32, _33, _34, _35, _36, _37, _38, _39, _40

        @Override
        public String toString() {
            return name().replace("_", "")
        }

        public int getValue() {
            return Integer.parseInt(toString())
        }
    }

    public static enum ColumnPinCount {
        _1, _2, _3

        @Override
        public String toString() {
            return name().replace("_", "")
        }

        public int getValue() {
            return Integer.parseInt(toString())
        }
    }
}
