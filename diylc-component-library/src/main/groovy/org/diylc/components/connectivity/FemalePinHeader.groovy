package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.AlphaComposite;
import java.awt.Color
import java.awt.Composite;
import java.awt.Point
import java.awt.Rectangle;
import java.awt.Shape
import java.awt.geom.Area;
import java.awt.geom.GeneralPath

import org.diylc.components.AbstractTransparentComponent;
import org.diylc.components.ControlPoint;
import org.diylc.components.Geometry
import org.diylc.components.Pin
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.annotations.ComponentEditOptions
import org.diylc.core.IDrawingObserver
import org.diylc.core.Orientation;
import org.diylc.core.Project
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentEditOptions(stretchable = false)
@ComponentBomPolicy(BomPolicy.NEVER_SHOW)
@ComponentDescriptor(name = "Female Pin header", category = "Connectivity", author = "Nikolaj Brinch Jørgensen", description = "Pin header female", instanceNamePrefix = "Header")
public class FemalePinHeader extends AbstractTransparentComponent implements Geometry {

    public static final String id = "d10a0b9c-b7f5-4e1b-aa43-f777c3d97ecb"
    
    private static final long serialVersionUID = 1L

    private static int PIN_SPACING = new Size(0.1d, SizeUnit.in).convertToPixels()

    private static int PIN_SIZE = Pin.DEFAULT_PIN_SIZE.convertToPixels() + 2

    private static int BASE_SIZE = new Size(2.5d, SizeUnit.mm).convertToPixels()

    private static Color COLOR = Color.black

    ControlPoint[] controlPoints = points(point(0, 0))

    transient List<Area> body

    @EditableProperty
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    @EditableProperty
    RowPinCount rowPinCount = RowPinCount._4

    @EditableProperty
    ColumnPinCount columnPinCount = ColumnPinCount._1

    public FemalePinHeader() {
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

        for(int i = 0; i < columnPinCount.value; i++) {
            for(int j = 0; j < rowPinCount.value; j++) {
                switch(orientation) {
                    case Orientation.DEFAULT:
                    case Orientation._180:
                        controlPoints[i * rowPinCount.value + j] = point(x + j * PIN_SPACING, y + i * PIN_SPACING)
                        break
                    case Orientation._90:
                    case Orientation._270:
                        controlPoints[i * rowPinCount.value + j] = point(x + i * PIN_SPACING, y + j * PIN_SPACING)
                        break
                    default:
                        throw new RuntimeException("Unexpected orientation: " + orientation)
                }
            }
        }

        this.controlPoints = controlPoints
    }

    private List<Area> getBodyArea() {
        if (body == null) {
            updateControlPoints()

            Area baseArea

            switch(orientation) {
                case Orientation.DEFAULT:
                case Orientation._180:
                    baseArea = new Area(new Rectangle(0, 0, rowPinCount.value * PIN_SPACING, columnPinCount.value * PIN_SPACING))
                    break
                case Orientation._90:
                case Orientation._270:
                    baseArea = new Area(new Rectangle(0, 0, columnPinCount.value * PIN_SPACING, rowPinCount.value * PIN_SPACING))
                    break
                default:
                    throw new RuntimeException("Unexpected orientation: " + orientation)
            }

            List<Area> areas = [baseArea, new Area(new Rectangle(0, 0, PIN_SIZE + 6, PIN_SIZE + 6))]
            areas.addAll(producePaths(0, 0, PIN_SIZE + 6, PIN_SIZE + 6).collect { GeneralPath path ->
                new Area(path)
            })
            body = areas
        }

        return body
    }

    private List<GeneralPath> producePaths(int x1, int y1, int x2, int y2) {
        List<GeneralPath> paths = []

        GeneralPath path = new GeneralPath()
        path.moveTo(x1, y1)
        path.lineTo(x2, y1)
        path.lineTo(x2 - 3, y1 + 3)
        path.lineTo(x1 + 3, y1 + 3)
        path.lineTo(x1, y1)
        paths << path

        path = new GeneralPath()
        path.moveTo(x1, y1)
        path.lineTo(x1, y2)
        path.lineTo(x1 + 3, y2 - 3)
        path.lineTo(x1 + 3, y1 + 3)
        path.lineTo(x1, y1)
        paths << path

        path = new GeneralPath()
        path.moveTo(x2, y1)
        path.lineTo(x2, y2)
        path.lineTo(x2 - 3, y2 - 3)
        path.lineTo(x2 - 3, y1 + 3)
        path.lineTo(x2, y1)
        paths << path

        path = new GeneralPath()
        path.moveTo(x2, y2)
        path.lineTo(x1, y2)
        path.lineTo(x1 + 3, y2 - 3)
        path.lineTo(x2 - 3, y2 - 3)
        path.lineTo(x2, y2)
        paths << path

        return paths
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode, Project project, IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()

        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }

        List areas = getBodyArea()
        Area baseArea = areas[0]
        Area pinArea1 = areas[1]
        List<Area> pinArea2 = areas.subList(2, areas.size())

        graphicsContext.with {
            Composite oldComposite = graphicsContext.getComposite()

            if (alpha < Colors.MAX_ALPHA) {
                AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toFloat(alpha / Colors.MAX_ALPHA))
                setComposite(composite)
            }

            int x = toInt(controlPoints[0].x - BASE_SIZE / 2)
            int y = toInt(controlPoints[0].y - BASE_SIZE / 2)

            drawArea(graphicsContext, x, y, baseArea, Colors.CHIP_COLOR, Colors.CHIP_BORDER_COLOR)

            /*
             * Draw control points
             */
            controlPoints.each { Point controlPoint ->
                int pinX = toInt(controlPoint.x - (PIN_SIZE + 4) / 2)
                int pinY = toInt(controlPoint.y - (PIN_SIZE + 4) / 2)

                drawArea(graphicsContext, pinX, pinY, pinArea1, Colors.CHIP_BORDER_COLOR.darker().darker(), null)

                int i = 0
                pinArea2.each { Area area ->
                    Color color
                    if (i > 1) {
                        color = Colors.CHIP_BORDER_COLOR
                    } else {
                        color = Colors.CHIP_BORDER_COLOR.darker()
                    }
                    drawArea(graphicsContext, pinX, pinY, area, color, null)
                    i++
                }
            }


            setComposite(oldComposite)

            if (componentState == ComponentState.SELECTED) {
                Rectangle bounds = new Rectangle(baseArea.getBounds())
                bounds.setLocation(x, y)
                setColor(Colors.SELECTION_COLOR)
                draw(bounds)
            }
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.with {
            drawFilledRect(4 * width / 32, 8 * height / 32, 24 * width / 32, 12 * height / 32, Colors.CHIP_BORDER_COLOR, Colors.CHIP_COLOR)
            for (int j = 0 ; j < 2; j ++) {
                for (int i = 0 ; i < 5; i ++) {
                    drawFilledRect(7 * width / 32 + i * 4, 11 * height / 32 + j * 4, 2 * width / 32, Colors.DARK_SILVER_COLOR, Colors.SILVER_COLOR)
                }
            }
        }
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
