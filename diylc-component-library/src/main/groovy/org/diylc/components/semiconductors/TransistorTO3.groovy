package org.diylc.components.semiconductors

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.FontMetrics
import java.awt.Point
import java.awt.Polygon
import java.awt.Rectangle
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D

import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.core.ComponentState
import org.diylc.core.Display;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Orientation;
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

@ComponentDescriptor(name = "Transistor (TO-3 package)", author = "Branislav Stojkovic", category = "Semiconductors", instanceNamePrefix = "Q", description = "Transistor with large metal body", stretchable = false, zOrder = IDIYComponent.COMPONENT)
public class TransistorTO3 extends AbstractTransparentComponent<String> implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color BODY_COLOR = Color.decode("#D0E0EF")
    public static Color BORDER_COLOR = BODY_COLOR.darker()
    public static Color PIN_COLOR = Color.decode("#00B2EE")
    public static Color PIN_BORDER_COLOR = PIN_COLOR.darker()
    public static Color LABEL_COLOR = Color.black

    public static Size LARGE_DIAMETER = new Size(26.2d, SizeUnit.mm)
    public static Size INNER_DIAMETER = new Size(21.3d, SizeUnit.mm)
    public static Size SMALL_DIAMETER = new Size(8d, SizeUnit.mm)
    public static Size HOLE_DISTANCE = new Size(30.1d, SizeUnit.mm)
    public static Size HOLE_SIZE = new Size(4.1d, SizeUnit.mm)
    public static Size PIN_SPACING = new Size(10.9d, SizeUnit.mm)
    public static Size PIN_OFFSET = new Size(1.85d, SizeUnit.mm)
    public static Size PIN_DIAMETER = new Size(1d, SizeUnit.mm)

    transient private Area[] body

    private Point[] controlPoints = points( point(0, 0),
            point(0, 0) )

    @EditableProperty
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    @EditableProperty(name = "Body")
    Color bodyColor = BODY_COLOR

    @EditableProperty(name = "Border")
    Color borderColor = BORDER_COLOR

    @EditableProperty(name = "Label")
    Color labelColor = LABEL_COLOR

    @EditableProperty
    Display display = Display.NAME

    public TransistorTO3() {
        super()
        updateControlPoints()
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation
        updateControlPoints()
        // Reset body shape;
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
    }

    private void updateControlPoints() {
        int pinSpacing = (int) PIN_SPACING.convertToPixels()
        // Update control points.
        int x = controlPoints[0].x
        int y = controlPoints[0].y
        switch (orientation) {
            case Orientation.DEFAULT:
                controlPoints[1].setLocation(x, y + pinSpacing)
                break
            case Orientation._90:
                controlPoints[1].setLocation(x - pinSpacing, y)
                break
            case Orientation._180:
                controlPoints[1].setLocation(x, y - pinSpacing)
                break
            case Orientation._270:
                controlPoints[1].setLocation(x + pinSpacing, y)
                break
            default:
                throw new RuntimeException("Unexpected orientation: " + orientation)
        }
    }

    public Area[] getBody() {
        if (body == null) {
            body = new Area[2]
            int pinOffset = (int) PIN_OFFSET.convertToPixels()
            int x = (controlPoints[0].x + controlPoints[1].x) / 2
            int y = (controlPoints[0].y + controlPoints[1].y) / 2

            switch (orientation) {
                case Orientation.DEFAULT:
                    x += pinOffset
                    break
                case Orientation._90:
                    y += pinOffset
                    break
                case Orientation._180:
                    x -= pinOffset
                    break
                case Orientation._270:
                    y -= pinOffset
                    break
                default:
                    throw new RuntimeException("Unexpected orientation: "
                    + orientation)
            }

            int largeDiameter = getClosestOdd(LARGE_DIAMETER.convertToPixels())
            int innerDiameter = getClosestOdd(INNER_DIAMETER.convertToPixels())
            int smallDiameter = getClosestOdd(SMALL_DIAMETER.convertToPixels())
            int holeDistance = getClosestOdd(HOLE_DISTANCE.convertToPixels())
            int holeSize = getClosestOdd(HOLE_SIZE.convertToPixels())

            body[0] = new Area(new Ellipse2D.Double(x - largeDiameter / 2, y - largeDiameter / 2, largeDiameter, largeDiameter))

            int[] xPoints = [
                x + (int) (Math.cos(Math.PI / 4) * largeDiameter / 2),
                x + holeDistance / 2 + (int) (Math.cos(Math.PI / 4) * smallDiameter / 2),
                x + holeDistance / 2 + (int) (Math.cos(Math.PI / 4) * smallDiameter / 2),
                x + (int) (Math.cos(Math.PI / 4) * largeDiameter / 2),
                x - (int) (Math.cos(Math.PI / 4) * largeDiameter / 2),
                x - holeDistance / 2 - (int) (Math.cos(Math.PI / 4) * smallDiameter / 2),
                x - holeDistance / 2 - (int) (Math.cos(Math.PI / 4) * smallDiameter / 2),
                x - (int) (Math.cos(Math.PI / 4) * largeDiameter / 2) ] as int[]
            int[] yPoints = [
                y - (int) (Math.sin(Math.PI / 4) * largeDiameter / 2),
                y - (int) (Math.sin(Math.PI / 4) * smallDiameter / 2),
                y + (int) (Math.sin(Math.PI / 4) * smallDiameter / 2),
                y + (int) (Math.sin(Math.PI / 4) * largeDiameter / 2),
                y + (int) (Math.sin(Math.PI / 4) * largeDiameter / 2),
                y + (int) (Math.sin(Math.PI / 4) * smallDiameter / 2),
                y - (int) (Math.sin(Math.PI / 4) * smallDiameter / 2),
                y - (int) (Math.sin(Math.PI / 4) * largeDiameter / 2) ] as int[]

            body[0].add(new Area(new Polygon(xPoints, yPoints, xPoints.length)))
            body[0].add(new Area(new Ellipse2D.Double(x - holeDistance / 2 - smallDiameter / 2, y - smallDiameter / 2, smallDiameter, smallDiameter)))
            body[0].subtract(new Area(new Ellipse2D.Double(x - holeDistance / 2 - holeSize / 2, y - holeSize / 2, holeSize, holeSize)))
            body[0].add(new Area(new Ellipse2D.Double(x + holeDistance / 2 - smallDiameter / 2, y - smallDiameter / 2, smallDiameter, smallDiameter)))
            body[0].subtract(new Area(new Ellipse2D.Double(x + holeDistance / 2 - holeSize / 2, y - holeSize / 2, holeSize, holeSize)))

            switch (orientation) {
                case Orientation.DEFAULT:
                    break
                case Orientation._90:
                    body[0].transform(AffineTransform.getRotateInstance(
                    Math.PI / 2, x, y))
                    break
                case Orientation._180:
                    body[0].transform(AffineTransform.getRotateInstance(Math.PI, x,
                    y))
                    break
                case Orientation._270:
                    body[0].transform(AffineTransform.getRotateInstance(
                    Math.PI * 3 / 2, x, y))
                    break
                default:
                    throw new RuntimeException("Unexpected orientation: "
                    + orientation)
            }

            body[1] = new Area(new Ellipse2D.Double(x - innerDiameter / 2, y
                    - innerDiameter / 2, innerDiameter, innerDiameter))
        }
        return body
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }
        int pinSize = (int) PIN_DIAMETER.convertToPixels() / 2 * 2
        Area mainArea = getBody()[0]
        Area innerArea = getBody()[1]
        Composite oldComposite = graphicsContext.getComposite()
        if (alpha < Colors.MAX_ALPHA) {
            graphicsContext.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
        }
        graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : bodyColor)
        graphicsContext.fill(mainArea)
        graphicsContext.setComposite(oldComposite)
        Color finalBorderColor
        Theme theme = Configuration.INSTANCE.getTheme()
        if (outlineMode) {
            finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                    : theme.getOutlineColor()
        } else {
            finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                    : borderColor
        }
        graphicsContext.setColor(finalBorderColor)
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        graphicsContext.draw(mainArea)
        graphicsContext.draw(innerArea)

        for (Point point : controlPoints) {
            if (!outlineMode) {
                graphicsContext.setColor(PIN_COLOR)
                graphicsContext.fillOval(point.x - pinSize / 2, point.y - pinSize / 2,
                        pinSize, pinSize)
            }
            graphicsContext.setColor(outlineMode ? theme.getOutlineColor()
                    : PIN_BORDER_COLOR)
            graphicsContext.drawOval(point.x - pinSize / 2, point.y - pinSize / 2, pinSize,
                    pinSize)
        }

        // Draw label.
        graphicsContext.setFont(LABEL_FONT)
        Color finalLabelColor
        if (outlineMode) {
            finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
                    : theme.getOutlineColor()
        } else {
            finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
                    : getLabelColor()
        }
        graphicsContext.setColor(finalLabelColor)
        String label = (getDisplay() == Display.NAME) ? getName() : getValue()
        FontMetrics fontMetrics = graphicsContext.getFontMetrics(graphicsContext.getFont())
        Rectangle2D rect = fontMetrics.getStringBounds(label, graphicsContext.graphics2D)
        int textHeight = (int) (rect.getHeight())
        int textWidth = (int) (rect.getWidth())
        // Center text horizontally and vertically
        Rectangle bounds = mainArea.getBounds()
        int x = bounds.x + (bounds.width - textWidth) / 2
        int y = bounds.y + (bounds.height - textHeight) / 2
        + fontMetrics.getAscent()
        graphicsContext.drawString(label, x, y)
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int sizeLarge = getClosestOdd(width * 3d / 4)
        int sizeInner = getClosestOdd(width * 5d / 10)
        int sizeSmall = getClosestOdd(width / 3d)
        int hole = 4 * width / 32
        Area area = new Area(new Ellipse2D.Double((width - sizeLarge) / 2,
                (height - sizeLarge) / 2, sizeLarge, sizeLarge))
        // int[] x = new int[] { width / 2 + sizeSmall / 2,
        // width / 2 + sizeLarge / 2, width / 2 + sizeSmall / 2,
        // width / 2 - sizeSmall / 2, width / 2 - sizeLarge / 2,
        // width / 2 - sizeSmall / 2 };
        // int[] y = new int[] { height / 8, height / 2, height * 7 / 8,
        // height * 7 / 8, height / 2, height / 8 };
        // Area p = new Area(new Polygon(x, y, x.length));
        // AffineTransform t = AffineTransform.getTranslateInstance(width / 2,
        // height / 2);
        // t.concatenate(AffineTransform.getScaleInstance(1.05, 1.05));
        // t.concatenate(AffineTransform.getTranslateInstance(-width / 2,
        // -height / 2));
        // p.transform(t);
        // area.add(new Area(p));
        area.add(new Area(new Ellipse2D.Double((width - sizeSmall) / 2, height / 8 - sizeSmall / 2, sizeSmall, sizeSmall)))
        area.add(new Area(new Ellipse2D.Double((width - sizeSmall) / 2, height
                * 7 / 8 - sizeSmall / 2, sizeSmall, sizeSmall)))
        area.subtract(new Area(new Ellipse2D.Double((width - hole) / 2, height / 8 - hole / 2, hole, hole)))
        area.subtract(new Area(new Ellipse2D.Double((width - hole) / 2, height
                * 7 / 8 - hole / 2, hole, hole)))
        area.transform(AffineTransform.getRotateInstance(Math.PI / 4,
                width / 2, height / 2))
        graphicsContext.setColor(BODY_COLOR)
        graphicsContext.fill(area)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.draw(area)
        graphicsContext.drawOval((width - sizeInner) / 2, (height - sizeInner) / 2,
                sizeInner, sizeInner)
    }

}
