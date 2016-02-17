package org.diylc.components.connectivity

import org.diylc.components.Colors

import groovy.transform.CompileStatic;

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

@ComponentDescriptor(name = "Female Molex 8981", category = "Connectivity", author = "Nikolaj Brinch Jørgensen", description = "Female Molex 4 Pin 8981", instanceNamePrefix = "Connector", stretchable = false, zOrder = IDIYComponent.COMPONENT, bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false)
public class FemaleMolex8981 extends AbstractTransparentComponent implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color COLOR = Color.black

    public static int PIN_COUNT = 4

    public static int PIN_SPACING = new Size(0.2d, SizeUnit.in).convertToPixels()
    
    public static int PIN_SIZE = new Size(2.12d, SizeUnit.mm).convertToPixels()
    
    public static int HALF_PIN_SIZE = (int) (PIN_SIZE / 2)
    
    public static int WIDTH1 = new Size(23.44d, SizeUnit.mm).convertToPixels()

    public static int WIDTH2 = new Size(19.5d, SizeUnit.mm).convertToPixels()
    
    public static int HEIGHT = new Size(8.28d, SizeUnit.mm).convertToPixels()
    
    ControlPoint[] controlPoints = points(point(0, 0))

    transient Area[] body

    @EditableProperty
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    public FemaleMolex8981() {
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

    private Area[] getBodyArea() {
        if (body == null) {
            updateControlPoints()
            
            int gap = toInt((WIDTH1 - WIDTH2) / 2)

            GeneralPath basePath = producePath(0, 0, WIDTH1, HEIGHT, gap)

            int thickness = toInt(new Size(0.89d, SizeUnit.mm).convertToPixels())

            GeneralPath innerPath = producePath(thickness, thickness, WIDTH1 - thickness, HEIGHT - thickness, gap)
            
            body = [new Area(basePath), new Area(innerPath)] as Area[]
        }

        return body
    }

    private GeneralPath producePath(int x1, int y1, int x2, int y2, int gap) {
        GeneralPath path = new GeneralPath()
        path.moveTo(x1, y1)
        path.lineTo(x2, y1)
        path.lineTo(x2, y2 - gap)
        path.lineTo(x2 - gap, y2)
        path.lineTo(x1 + gap, y2)
        path.lineTo(x1, y2 - gap)
        path.lineTo(x1, y2)
        
        return path

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

            Area baseArea = new Area(getBodyArea()[0])
            AffineTransform move = AffineTransform.getTranslateInstance(
                toInt(controlPoints[0].x - (WIDTH1 - 3 * PIN_SPACING) / 2),
                toInt(controlPoints[0].y - HEIGHT / 2))
            baseArea.transform(move)
            
            setColor(Color.white)
            fill(baseArea)

            Area innerArea = new Area(getBodyArea()[1])
            innerArea.transform(move)
            setColor(Color.white.darker())
            draw(innerArea)

            controlPoints.each { ControlPoint controlPoint ->
                int x = controlPoint.x
                int y = controlPoint.y
                
                drawFilledOval(x - HALF_PIN_SIZE, y - HALF_PIN_SIZE, PIN_SIZE, Colors.SILVER_COLOR.darker(), Colors.SILVER_COLOR)
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
        int diameter = getClosestOdd(width / 2)
        int holeDiameter = 5
        graphicsContext.setColor(COLOR)
        graphicsContext.fillOval((int) (width - diameter) / 2, (int) (height - diameter) / 2, diameter, diameter)
        graphicsContext.setColor(Constants.CANVAS_COLOR)
        graphicsContext.fillOval((int) (width - holeDiameter) / 2, (int) (height - holeDiameter) / 2, holeDiameter, holeDiameter)
    }

}
