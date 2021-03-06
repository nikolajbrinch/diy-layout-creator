package org.diylc.components.passive

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.FontMetrics
import java.awt.Point
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D

import org.diylc.components.AbstractPotentiometer
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.core.ComponentState
import org.diylc.core.CreationMethod
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Orientation;
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Trimmer Potentiometer", author = "Branislav Stojkovic", category = "Passive", creationMethod = CreationMethod.SINGLE_CLICK, instanceNamePrefix = "VR", description = "Various types of board mounted trimmer potentiometers", zOrder = IDIYComponent.COMPONENT, stretchable = false)
public class TrimmerPotentiometer extends AbstractPotentiometer implements Geometry {

    private static final long serialVersionUID = 1L

    protected static Size FLAT_BODY_SIZE = new Size(9.5d, SizeUnit.mm)
    protected static Size FLAT_SHAFT_SIZE = new Size(4d, SizeUnit.mm)
    protected static Size VERTICAL_BODY_LENGTH = new Size(9.5d, SizeUnit.mm)
    protected static Size VERTICAL_BODY_WIDTH = new Size(4.5d, SizeUnit.mm)
    protected static Size SPACING = new Size(0.1d, SizeUnit.in)
    private static Color BODY_COLOR = Color.decode("#FFFFE0")
    private static Color BORDER_COLOR = Color.decode("#8E8E38")
    private static Color SHAFT_COLOR = Color.decode("#FFFFE0")
    private static Color SHAFT_BORDER_COLOR = Color.decode("#8E8E38")
    public static Color PIN_COLOR = Color.decode("#00B2EE")
    public static Color PIN_BORDER_COLOR = PIN_COLOR.darker()
    public static Size PIN_SIZE = new Size(0.03d, SizeUnit.in)

    // Array of 7 elements: 3 lug connectors, 1 pot body and 3 lugs
    transient protected Shape[] body = null

    @EditableProperty(name = "Body")
    Color bodyColor = BODY_COLOR

    @EditableProperty(name = "Border")
    Color borderColor = BORDER_COLOR

    @EditableProperty
    TrimmerType type = TrimmerType.FLAT_SMALL

    public TrimmerPotentiometer() {
        controlPoints = points( point(0, 0), point(0, 0),
                point(0, 0) )
        updateControlPoints()
    }

    protected void updateControlPoints() {
        int spacing = (int) SPACING.convertToPixels()
        int dx1 = 0
        int dy1 = 0
        int dx2 = 0
        int dy2 = 0
        switch (orientation) {
            case Orientation.DEFAULT:
                switch (type) {
                    case TrimmerType.FLAT_SMALL:
                    dx1 = 2 * spacing
                    dy1 = spacing
                    dx2 = 0
                    dy2 = 2 * spacing
                    break
                    case TrimmerType.FLAT_LARGE:
                    dx1 = 4 * spacing
                    dy1 = spacing
                    dx2 = 0
                    dy2 = 2 * spacing
                    break
                    case TrimmerType.VERTICAL_INLINE:
                    dx1 = 0
                    dy1 = spacing
                    dx2 = 0
                    dy2 = 2 * spacing
                    break
                    case TrimmerType.VERTICAL_OFFSET:
                    dx1 = spacing
                    dy1 = spacing
                    dx2 = 0
                    dy2 = 2 * spacing
                    break
                }
                break
            case Orientation._90:
                switch (type) {
                    case TrimmerType.FLAT_SMALL:
                    dx1 = -spacing
                    dy1 = 2 * spacing
                    dx2 = -2 * spacing
                    dy2 = 0
                    break
                    case TrimmerType.FLAT_LARGE:
                    dx1 = -spacing
                    dy1 = 4 * spacing
                    dx2 = -2 * spacing
                    dy2 = 0
                    break
                    case TrimmerType.VERTICAL_INLINE:
                    dx1 = -spacing
                    dy1 = 0
                    dx2 = -2 * spacing
                    dy2 = 0
                    break
                    case TrimmerType.VERTICAL_OFFSET:
                    dx1 = -spacing
                    dy1 = spacing
                    dx2 = -2 * spacing
                    dy2 = 0
                    break
                }
                break
            case Orientation._180:
                switch (type) {
                    case TrimmerType.FLAT_SMALL:
                    dx1 = -2 * spacing
                    dy1 = -spacing
                    dx2 = 0
                    dy2 = -2 * spacing
                    break
                    case TrimmerType.FLAT_LARGE:
                    dx1 = -4 * spacing
                    dy1 = -spacing
                    dx2 = 0
                    dy2 = -2 * spacing
                    break
                    case TrimmerType.VERTICAL_INLINE:
                    dx1 = 0
                    dy1 = -spacing
                    dx2 = 0
                    dy2 = -2 * spacing
                    break
                    case TrimmerType.VERTICAL_OFFSET:
                    dx1 = -spacing
                    dy1 = -spacing
                    dx2 = 0
                    dy2 = -2 * spacing
                    break
                }
                break
            case Orientation._270:
                switch (type) {
                    case TrimmerType.FLAT_SMALL:
                    dx1 = spacing
                    dy1 = -2 * spacing
                    dx2 = 2 * spacing
                    dy2 = 0
                    break
                    case TrimmerType.FLAT_LARGE:
                    dx1 = spacing
                    dy1 = -4 * spacing
                    dx2 = 2 * spacing
                    dy2 = 0
                    break
                    case TrimmerType.VERTICAL_INLINE:
                    dx1 = spacing
                    dy1 = 0
                    dx2 = 2 * spacing
                    dy2 = 0
                    break
                    case TrimmerType.VERTICAL_OFFSET:
                    dx1 = spacing
                    dy1 = -spacing
                    dx2 = 2 * spacing
                    dy2 = 0
                    break
                }
                break
            default:
                break
        }
        controlPoints[1].setLocation(controlPoints[0].x + dx1,
                controlPoints[0].y + dy1)
        controlPoints[2].setLocation(controlPoints[0].x + dx2,
                controlPoints[0].y + dy2)
    }

    public Shape[] getBody() {
        if (body == null) {
            body = new Shape[2]

            // Calculate the center point as center of the minimum bounding
            // rectangle.
            int centerX = (Math.max(Math.max(controlPoints[0].x,
                    controlPoints[1].x), controlPoints[2].x) + Math.min(Math
                    .min(controlPoints[0].x, controlPoints[1].x),
                    controlPoints[2].x)) / 2
            int centerY = (Math.max(Math.max(controlPoints[0].y,
                    controlPoints[1].y), controlPoints[2].y) + Math.min(Math
                    .min(controlPoints[0].y, controlPoints[1].y),
                    controlPoints[2].y)) / 2

            // Calculate body dimensions based on the selected type.
            int length = 0
            int width = 0
            switch (type) {
                case TrimmerType.FLAT_LARGE:
                case TrimmerType.FLAT_SMALL:
                    length = getClosestOdd(FLAT_BODY_SIZE.convertToPixels())
                    width = length
                    int shaftSize = getClosestOdd(FLAT_SHAFT_SIZE.convertToPixels())
                    Area shaft = new Area(new Ellipse2D.Double(centerX - shaftSize / 2, centerY - shaftSize / 2, shaftSize, shaftSize))
                    Area slot = new Area(new Rectangle2D.Double(centerX - shaftSize / 2, centerY - shaftSize / 8, shaftSize, shaftSize / 4))
                    slot.transform(AffineTransform.getRotateInstance(Math.PI / 4,
                            centerX, centerY))
                    shaft.subtract(slot)
                    body[1] = shaft
                    break
                case TrimmerType.VERTICAL_INLINE:
                case TrimmerType.VERTICAL_OFFSET:
                    length = getClosestOdd(VERTICAL_BODY_LENGTH.convertToPixels())
                    width = getClosestOdd(VERTICAL_BODY_WIDTH.convertToPixels())
                    break
            }
            if (orientation == Orientation.DEFAULT
            || orientation == Orientation._180) {
                int p = length
                length = width
                width = p
            }
            body[0] = new Rectangle2D.Double(centerX - length / 2, centerY
                    - width / 2, length, width)
        }
        return body
    }

    @Override
    public void setControlPoint(Point point, int index) {
        super.setControlPoint(point, index)
        body = null
    }

    @Override
    public void setOrientation(Orientation orientation) {
        super.setOrientation(orientation)
        updateControlPoints()
        body = null
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        Shape mainShape = getBody()[0]
        Shape shaftShape = getBody()[1]
        Theme theme = Configuration.INSTANCE.getTheme()
        if (mainShape != null) {
            graphicsContext.setColor(bodyColor)
            Composite oldComposite = graphicsContext.getComposite()
            if (alpha < Colors.MAX_ALPHA) {
                graphicsContext.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
            }
            if (!outlineMode) {
                graphicsContext.fill(mainShape)
            }
            if (!outlineMode && shaftShape != null) {
                graphicsContext.setColor(SHAFT_COLOR)
                graphicsContext.fill(shaftShape)
                graphicsContext.setColor(SHAFT_BORDER_COLOR)
                graphicsContext.draw(shaftShape)
            }
            graphicsContext.setComposite(oldComposite)
            Color finalBorderColor
            if (outlineMode) {
                finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                        : theme.getOutlineColor()
            } else {
                finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                        : borderColor
            }
            graphicsContext.setColor(finalBorderColor)
            graphicsContext.draw(mainShape)
        }

        // Draw pins.
        int pinSize = getClosestOdd(PIN_SIZE.convertToPixels())
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
                    : Colors.LABEL_COLOR
        }
        graphicsContext.setColor(finalLabelColor)
        FontMetrics fontMetrics = graphicsContext.getFontMetrics()
        Rectangle2D bodyRect = getBody()[0].getBounds2D()
        Rectangle2D rect = fontMetrics.getStringBounds(getName(), graphicsContext.graphics2D)

        int textHeight = (int) rect.getHeight()
        int textWidth = (int) rect.getWidth()
        int panelHeight = (int) bodyRect.getHeight()
        int panelWidth = (int) bodyRect.getWidth()

        int x = (panelWidth - textWidth) / 2
        int y = (panelHeight - textHeight) / 2 + fontMetrics.getAscent()

        graphicsContext.drawString(getName(), (int) (bodyRect.getX() + x), (int) (bodyRect
                .getY() + y))
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int margin = 4
        graphicsContext.setColor(BODY_COLOR)
        graphicsContext.fillRect(margin, margin, width - 2 * margin, width - 2 * margin)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        graphicsContext.drawRect(margin, margin, width - 2 * margin, width - 2 * margin)
        int shaftSize = 11
        int slotSize = 2
        Area area = new Area(new Ellipse2D.Double(width / 2 - shaftSize / 2,
                width / 2 - shaftSize / 2, shaftSize, shaftSize))
        Area slot = new Area(new Rectangle2D.Double(0,
                width / 2 - slotSize / 2, width, slotSize))
        slot.transform(AffineTransform.getRotateInstance(Math.PI / 4,
                width / 2, width / 2))
        area.subtract(slot)
        graphicsContext.setColor(SHAFT_COLOR)
        graphicsContext.fill(area)
        graphicsContext.setColor(SHAFT_BORDER_COLOR)
        graphicsContext.draw(area)

        int pinSize = 3
        graphicsContext.setColor(PIN_COLOR)
        graphicsContext.fillOval(margin - pinSize / 2, 10 - pinSize / 2, pinSize, pinSize)
        graphicsContext.setColor(PIN_BORDER_COLOR)
        graphicsContext.drawOval(margin - pinSize / 2, 10 - pinSize / 2, pinSize, pinSize)

        graphicsContext.setColor(PIN_COLOR)
        graphicsContext.fillOval(margin - pinSize / 2, 21 - pinSize / 2, pinSize, pinSize)
        graphicsContext.setColor(PIN_BORDER_COLOR)
        graphicsContext.drawOval(margin - pinSize / 2, 21 - pinSize / 2, pinSize, pinSize)

        graphicsContext.setColor(PIN_COLOR)
        graphicsContext.fillOval(width - margin - pinSize / 2, width / 2 - pinSize / 2,
                pinSize, pinSize)
        graphicsContext.setColor(PIN_BORDER_COLOR)
        graphicsContext.drawOval(width - margin - pinSize / 2, width / 2 - pinSize / 2,
                pinSize, pinSize)
    }

    public void setType(TrimmerType type) {
        this.type = type
        updateControlPoints()
        body = null
    }

    public static enum TrimmerType {
        FLAT_LARGE, FLAT_SMALL, VERTICAL_INLINE, VERTICAL_OFFSET

        @Override
        public String toString() {
            return name().substring(0, 1)
            + name().substring(1).toLowerCase().replace("_", " ")
        }
    }
}
