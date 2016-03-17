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
import java.awt.geom.GeneralPath

import org.diylc.components.AbstractTransparentComponent;
import org.diylc.components.ControlPoint;
import org.diylc.components.Geometry
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
@ComponentDescriptor(name = "Male Molex 6410", category = "Connectivity", author = "Nikolaj Brinch Jørgensen", description = "Male Molex 6410", instanceNamePrefix = "Connector")
public class MaleMolex6410 extends AbstractTransparentComponent implements Geometry {

    public static final String id = "6b71ef92-6c33-4508-865c-b43735d933d5"
    
    private static final long serialVersionUID = 1L

    private static Color COLOR = Color.black

    private static int PIN_COUNT = 4

    private static int PIN_SPACING = new Size(0.1d, SizeUnit.in).convertToPixels()
    
    private static int PIN_SIZE = new Size(0.64d, SizeUnit.mm).convertToPixels()
    
    private static int HALF_PIN_SIZE = (int) (PIN_SIZE / 2)
    
    private static int HEIGHT = new Size(5.8d, SizeUnit.mm).convertToPixels()
    
    ControlPoint[] controlPoints = points(point(0, 0))

    transient List<Area> body

    @EditableProperty
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    public MaleMolex6410() {
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

    private void updateControlPoints() {
        Point firstPoint = controlPoints[0]

        int x = firstPoint.x
        int y = firstPoint.y

        Point[] controlPoints = new Point[PIN_COUNT]

        for(int i = 0; i < PIN_COUNT; i++) {
            switch(orientation) {
                case Orientation.DEFAULT:
                case Orientation._180:
                    controlPoints[i] = point(x + i * PIN_SPACING, y)
                    break
                case Orientation._90:
                case Orientation._270:
                    controlPoints[i] = point(x, y + i * PIN_SPACING)
                    break
                default:
                    throw new RuntimeException("Unexpected orientation: " + orientation)
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
                    baseArea = new Area(new Rectangle(0, 0, PIN_COUNT * PIN_SPACING, HEIGHT))
                    break;
                case Orientation._90:
                case Orientation._270:
                    baseArea = new Area(new Rectangle(0, 0, HEIGHT, PIN_COUNT * PIN_SPACING))
                    break;
                default:
                    throw new RuntimeException("Unexpected orientation: " + orientation)
            }
            
            body = [baseArea]
            body.addAll(producePaths(PIN_COUNT).collect { new Area(it) })
        }

        return body
    }

    private List<GeneralPath> producePaths(int rowPinCount) {
        List <GeneralPath> paths = []

        int width1 = new Size(1.0d, SizeUnit.mm).convertToPixels()
        int width2 = new Size(0.53d, SizeUnit.mm).convertToPixels()
        
        int x1 = PIN_SPACING / 2
        int y1 = 0
        int x2 = rowPinCount * PIN_SPACING - PIN_SPACING / 2
        int y2 = width1
        
        switch(orientation) {
            case Orientation.DEFAULT:
                break
            case Orientation._90:
                x1 = HEIGHT - width1
                y1 = PIN_SPACING / 2
                x2 = HEIGHT
                y2 = rowPinCount * PIN_SPACING - PIN_SPACING / 2
            case Orientation._180:
                y1 = HEIGHT - width1
                y2 = HEIGHT
                break
            case Orientation._270:
                x1 = 0
                y1 = PIN_SPACING / 2
                x2 = width1
                y2 = rowPinCount * PIN_SPACING - PIN_SPACING / 2
                break
                break
            default:
                throw new RuntimeException("Unexpected orientation: " + orientation)
        }

        GeneralPath path = new GeneralPath()
        path.moveTo(x1, y1)
        path.lineTo(x2, y1)
        path.lineTo(x2, y2)
        path.lineTo(x1, y2)
        path.lineTo(x1, y1)
        paths << path

        switch(orientation) {
            case Orientation.DEFAULT:
                y1 = width1
                y2 = y1 + width2
                break
            case Orientation._90:
                x1 = HEIGHT - width1 - width2
                x2 = x1 + width2
                break
            case Orientation._180:
                y1 = HEIGHT - width1 - width2
                y2 = y1 + width2
                break
            case Orientation._270:
                x1 = width1
                x2 = x1 + width2
                break
            default:
                throw new RuntimeException("Unexpected orientation: " + orientation)
        }

        path = new GeneralPath()
        path.moveTo(x1, y1)
        path.lineTo(x2, y1)
        path.lineTo(x2, y2)
        path.lineTo(x1, y2)
        path.lineTo(x1, y1)
        paths << path

        return paths

    }
    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode, Project project, IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()

        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }

        graphicsContext.with {
            Composite oldComposite = graphicsContext.getComposite()

            if (alpha < Colors.MAX_ALPHA) {
                AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toFloat(alpha / Colors.MAX_ALPHA))
                setComposite(composite)
            }

            int x = toInt(controlPoints[0].x - PIN_SPACING / 2)
            int y = toInt(controlPoints[0].y - HEIGHT / 2)
            
            if (orientation == Orientation._90 || orientation == Orientation._270) {
                x = toInt(controlPoints[0].x - HEIGHT / 2)
                y = toInt(controlPoints[0].y - PIN_SPACING / 2)
            }
                        
            List<Area> areas = getBodyArea()
            Area baseArea = new Area(areas[0])
            AffineTransform move = AffineTransform.getTranslateInstance(x, y)
            baseArea.transform(move)
            
            setColor(Color.white)
            fill(baseArea)

            areas.subList(1, areas.size()).each { Area area ->            
                Area innerArea = new Area(area)
                innerArea.transform(move)
                setColor(Color.white.darker())
                draw(innerArea)
            }
            
            controlPoints.each { ControlPoint controlPoint ->
                drawFilledRect(controlPoint.x - HALF_PIN_SIZE, controlPoint.y - HALF_PIN_SIZE, PIN_SIZE, Colors.SILVER_COLOR.darker(), Colors.SILVER_COLOR)
            }

            if (componentState == ComponentState.SELECTED) {
                setComposite(oldComposite)
                setColor(Colors.SELECTION_COLOR)
                draw(baseArea)
            } else {
                setColor(Color.white.darker())
                draw(baseArea)
                setComposite(oldComposite)
            }
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int pinSize = 2 * width / 32
        
        graphicsContext.with {
            drawFilledRect(width / 32, 12 * height / 32, 30 * width / 32, 14 * height / 32, Color.white.darker(), Color.white)
            drawFilledRect(6 * width / 32, 12 * height / 32, 20 * width / 32, 3 * height / 32, Color.white.darker(), Color.white)
            drawFilledRect(6 * width / 32, 15 * height / 32, 20 * width / 32, 2 * height / 32, Color.white.darker(), Color.white)
            drawFilledOval(6 * width / 32, 20 * height / 32, pinSize, Color.white.darker(), Color.white)
            drawFilledOval(12 * width / 32, 20 * height / 32, pinSize, Color.white.darker(), Color.white)
            drawFilledOval(18 * width / 32, 20 * height / 32, pinSize, Color.white.darker(), Color.white)
            drawFilledOval(24 * width / 32, 20 * height / 32, pinSize, Color.white.darker(), Color.white)
        }
    }

}
