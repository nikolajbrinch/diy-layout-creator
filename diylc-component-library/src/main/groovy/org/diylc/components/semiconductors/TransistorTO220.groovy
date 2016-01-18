package org.diylc.components.semiconductors

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.FontMetrics
import java.awt.Point
import java.awt.Rectangle
import java.awt.Shape
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

@ComponentDescriptor(name = "Transistor (TO-220 package)", author = "Branislav Stojkovic", category = "Semiconductors", instanceNamePrefix = "Q", description = "Transistors with metal tab for heat sink mounting", stretchable = false, zOrder = IDIYComponent.COMPONENT)
public class TransistorTO220 extends AbstractTransparentComponent<String> implements Geometry {

    private static final long serialVersionUID = 1L

    public static Color BODY_COLOR = Color.gray
    public static Color BORDER_COLOR = Color.gray.darker()
    public static Color PIN_COLOR = Color.decode("#00B2EE")
    public static Color PIN_BORDER_COLOR = PIN_COLOR.darker()
    public static Color TAB_COLOR = Color.decode("#C3E4ED")
    public static Color TAB_BORDER_COLOR = TAB_COLOR.darker()
    public static Color LABEL_COLOR = Color.white
    public static Size PIN_SIZE = new Size(0.03d, SizeUnit.in)
    public static Size PIN_SPACING = new Size(0.1d, SizeUnit.in)
    public static Size BODY_WIDTH = new Size(0.4d, SizeUnit.in)
    public static Size BODY_THICKNESS = new Size(4.5d, SizeUnit.mm)
    public static Size BODY_HEIGHT = new Size(9d, SizeUnit.mm)
    public static Size TAB_THICKNESS = new Size(1d, SizeUnit.mm)
    public static Size TAB_HEIGHT = new Size(6.2d, SizeUnit.mm)
    public static Size TAB_HOLE_DIAMETER = new Size(3.6d, SizeUnit.mm)
    public static Size LEAD_LENGTH = new Size(3.5d, SizeUnit.mm)
    public static Size LEAD_THICKNESS = new Size(0.8d, SizeUnit.mm)

    transient private Shape[] body

    private Point[] controlPoints = points( point(0, 0),
            point(0, 0), point(0, 0))

    @EditableProperty
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    @EditableProperty(name = "Body")
    Color bodyColor = BODY_COLOR

    @EditableProperty(name = "Border")
    Color borderColor = BORDER_COLOR

    @EditableProperty(name = "Tab")
    Color tabColor = TAB_COLOR

    @EditableProperty(name = "Tab border")
    Color tabBorderColor = TAB_BORDER_COLOR

    @EditableProperty
    Display display = Display.NAME

    @EditableProperty
    boolean folded = false

    @EditableProperty(name = "Lead length")
    Size leadLength = LEAD_LENGTH

    public TransistorTO220() {
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
                controlPoints[2].setLocation(x, y + 2 * pinSpacing)
                break
            case Orientation._90:
                controlPoints[1].setLocation(x - pinSpacing, y)
                controlPoints[2].setLocation(x - 2 * pinSpacing, y)
                break
            case Orientation._180:
                controlPoints[1].setLocation(x, y - pinSpacing)
                controlPoints[2].setLocation(x, y - 2 * pinSpacing)
                break
            case Orientation._270:
                controlPoints[1].setLocation(x + pinSpacing, y)
                controlPoints[2].setLocation(x + 2 * pinSpacing, y)
                break
            default:
                throw new RuntimeException("Unexpected orientation: " + orientation)
        }
    }

    public Shape[] getBody() {
        if (body == null) {
            body = new Shape[2]
            int x = controlPoints[0].x
            int y = controlPoints[0].y
            int pinSpacing = (int) PIN_SPACING.convertToPixels()
            int bodyWidth = getClosestOdd(BODY_WIDTH.convertToPixels())
            int bodyThickness = getClosestOdd(BODY_THICKNESS.convertToPixels())
            int bodyHeight = getClosestOdd(BODY_HEIGHT.convertToPixels())
            int tabThickness = (int) TAB_THICKNESS.convertToPixels()
            int tabHeight = (int) TAB_HEIGHT.convertToPixels()
            int tabHoleDiameter = (int) TAB_HOLE_DIAMETER.convertToPixels()
            double leadLength = getLeadLength().convertToPixels()

            switch (orientation) {
                case Orientation.DEFAULT:
                    if (folded) {
                        body[0] = new Rectangle2D.Double(x + leadLength, y
                                + pinSpacing - bodyWidth / 2, bodyHeight, bodyWidth)
                        body[1] = new Area(new Rectangle2D.Double(x + leadLength
                                + bodyHeight, y + pinSpacing - bodyWidth / 2,
                                tabHeight, bodyWidth))
                        ((Area) body[1]).subtract(new Area(new Ellipse2D.Double(x
                                + leadLength + bodyHeight + tabHeight / 2
                                - tabHoleDiameter / 2, y + pinSpacing
                                - tabHoleDiameter / 2, tabHoleDiameter,
                                tabHoleDiameter)))
                    } else {
                        body[0] = new Rectangle2D.Double(x - bodyThickness / 2, y
                                + pinSpacing - bodyWidth / 2, bodyThickness,
                                bodyWidth)
                        body[1] = new Rectangle2D.Double(x + bodyThickness / 2
                                - tabThickness, y + pinSpacing - bodyWidth / 2,
                                tabThickness, bodyWidth)
                    }
                    break
                case Orientation._90:
                    if (folded) {
                        body[0] = new Rectangle2D.Double(x - pinSpacing - bodyWidth / 2, y + leadLength, bodyWidth, bodyHeight)
					body[1] = new Area(new Rectangle2D.Double(x - pinSpacing
							- bodyWidth / 2, y + leadLength + bodyHeight,
                        bodyWidth, tabHeight))
                        ((Area) body[1]).subtract(new Area(new Ellipse2D.Double(x
                                - pinSpacing - tabHoleDiameter / 2, y + leadLength
                                + bodyHeight + tabHeight / 2 - tabHoleDiameter / 2,
                                tabHoleDiameter, tabHoleDiameter)))
                    } else {
                        body[0] = new Rectangle2D.Double(x - pinSpacing - bodyWidth / 2, y - bodyThickness / 2, bodyWidth,
                                bodyThickness)
                        body[1] = new Rectangle2D.Double(x - pinSpacing - bodyWidth / 2, y + bodyThickness / 2 - tabThickness,
                                bodyWidth, tabThickness)
                    }
                    break
                case Orientation._180:
                    if (folded) {
                        body[0] = new Rectangle2D.Double(x - leadLength
                                - bodyHeight, y - pinSpacing - bodyWidth / 2,
                                bodyHeight, bodyWidth)
                        body[1] = new Area(new Rectangle2D.Double(x - leadLength
                                - bodyHeight - tabHeight, y - pinSpacing
                                - bodyWidth / 2, tabHeight, bodyWidth))
                        ((Area) body[1]).subtract(new Area(new Ellipse2D.Double(x
                                - leadLength - bodyHeight - tabHeight / 2
                                - tabHoleDiameter / 2, y - pinSpacing
                                - tabHoleDiameter / 2, tabHoleDiameter,
                                tabHoleDiameter)))
                    } else {
                        body[0] = new Rectangle2D.Double(x - bodyThickness / 2, y
                                - pinSpacing - bodyWidth / 2, bodyThickness,
                                bodyWidth)
                        body[1] = new Rectangle2D.Double(x - bodyThickness / 2, y
                                - pinSpacing - bodyWidth / 2, tabThickness,
                                bodyWidth)
                    }
                    break
                case Orientation._270:
                    if (folded) {
                        body[0] = new Rectangle2D.Double(x + pinSpacing - bodyWidth / 2, y - leadLength - bodyHeight, bodyWidth,
							bodyHeight)
					body[1] = new Area(new Rectangle2D.Double(x + pinSpacing
							- bodyWidth / 2, y - leadLength - bodyHeight
                        - tabHeight, bodyWidth, tabHeight))
                        ((Area) body[1]).subtract(new Area(new Ellipse2D.Double(x
                                + pinSpacing - tabHoleDiameter / 2, y - leadLength
                                - bodyHeight - tabHeight / 2 - tabHoleDiameter / 2,
                                tabHoleDiameter, tabHoleDiameter)))
                    } else {
                        body[0] = new Rectangle2D.Double(x + pinSpacing - bodyWidth / 2, y - bodyThickness / 2, bodyWidth,
                                bodyThickness)
                        body[1] = new Rectangle2D.Double(x + pinSpacing - bodyWidth / 2, y - bodyThickness / 2, bodyWidth, tabThickness)
                    }
                    break
                default:
                    throw new RuntimeException("Unexpected orientation: "
                    + orientation)
            }
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
        int pinSize = (int) PIN_SIZE.convertToPixels() / 2 * 2
        Shape mainArea = getBody()[0]
        Shape tabArea = getBody()[1]
        Composite oldComposite = graphicsContext.getComposite()
        if (alpha < Colors.MAX_ALPHA) {
            graphicsContext.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
        }
        graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : bodyColor)
        graphicsContext.fill(mainArea)
        Color finalTabColor
        if (outlineMode) {
            Theme theme = Configuration.INSTANCE.getTheme()
            finalTabColor = theme.getOutlineColor()
        } else {
            finalTabColor = tabColor
        }
        graphicsContext.setColor(finalTabColor)
        graphicsContext.fill(tabArea)
        graphicsContext.setComposite(oldComposite)
        if (!outlineMode) {
            graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
            graphicsContext.setColor(tabBorderColor)
            graphicsContext.draw(tabArea)
        }
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
        graphicsContext.draw(mainArea)
        if (folded) {
            graphicsContext.draw(tabArea)
        }

        // Draw pins.

        if (folded) {
            int leadThickness = getClosestOdd(LEAD_THICKNESS.convertToPixels())
            int leadLength = (int) getLeadLength().convertToPixels()
            Color finalPinColor
            Color finalPinBorderColor
            if (outlineMode) {
                finalPinColor = new Color(0, 0, 0, 0)
                finalPinBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                        : theme.getOutlineColor()
            } else {
                finalPinColor = Colors.METAL_COLOR
                finalPinBorderColor = Colors.METAL_COLOR.darker()
            }
            for (Point point : controlPoints) {
                switch (orientation) {
                    case Orientation.DEFAULT:
                        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
                        leadThickness))
                        graphicsContext.setColor(finalPinBorderColor)
                        graphicsContext.drawLine(point.x, point.y, point.x + leadLength
                                - leadThickness / 2, point.y)
                        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
                                leadThickness - 2))
                        graphicsContext.setColor(finalPinColor)
                        graphicsContext.drawLine(point.x, point.y, point.x + leadLength
                                - leadThickness / 2, point.y)
                        break
                    case Orientation._90:
                        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
                        leadThickness))
                        graphicsContext.setColor(finalPinBorderColor)
                        graphicsContext.drawLine(point.x, point.y, point.x, point.y
                                + leadLength - leadThickness / 2)
                        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
                                leadThickness - 2))
                        graphicsContext.setColor(finalPinColor)
                        graphicsContext.drawLine(point.x, point.y, point.x, point.y
                                + leadLength - leadThickness / 2)
                        break
                    case Orientation._180:
                        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
                        leadThickness))
                        graphicsContext.setColor(finalPinBorderColor)
                        graphicsContext.drawLine(point.x, point.y, point.x - leadLength
                                - leadThickness / 2, point.y)
                        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
                                leadThickness - 2))
                        graphicsContext.setColor(finalPinColor)
                        graphicsContext.drawLine(point.x, point.y, point.x - leadLength
                                - leadThickness / 2, point.y)
                        break
                    case Orientation._270:
                        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
                        leadThickness))
                        graphicsContext.setColor(finalPinBorderColor)
                        graphicsContext.drawLine(point.x, point.y, point.x, point.y
                                - leadLength)
                        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(
                                leadThickness - 2))
                        graphicsContext.setColor(finalPinColor)
                        graphicsContext.drawLine(point.x, point.y, point.x, point.y
                                - leadLength)
                        break
                }
            }
        } else {
            if (!outlineMode) {
                for (Point point : controlPoints) {

                    graphicsContext.setColor(PIN_COLOR)
                    graphicsContext.fillOval(point.x - pinSize / 2, point.y - pinSize / 2,
                            pinSize, pinSize)
                    graphicsContext.setColor(outlineMode ? theme.getOutlineColor()
                            : PIN_BORDER_COLOR)
                    graphicsContext.drawOval(point.x - pinSize / 2, point.y - pinSize / 2,
                            pinSize, pinSize)
                }
            }
        }

        // Draw label.
        graphicsContext.setFont(LABEL_FONT)
        Color finalLabelColor
        if (outlineMode) {
            finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
                    : theme.getOutlineColor()
        } else {
            finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
                    : LABEL_COLOR
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
        int margin = 2 * width / 32
        int bodySize = width * 5 / 10
        int tabSize = bodySize * 6 / 10
        int holeSize = 5 * width / 32
        Area a = new Area(new Rectangle2D.Double((width - bodySize) / 2,
                margin, bodySize, tabSize))
        a.subtract(new Area(new Ellipse2D.Double(width / 2 - holeSize / 2,
                margin + tabSize / 2 - holeSize / 2, holeSize, holeSize)))
        graphicsContext.setColor(TAB_COLOR)
        graphicsContext.fill(a)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.draw(a)
        graphicsContext.setColor(BODY_COLOR)
        graphicsContext.fillRect((width - bodySize) / 2, margin + tabSize, bodySize,
                bodySize)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawRect((width - bodySize) / 2, margin + tabSize, bodySize,
                bodySize)
        graphicsContext.setColor(Colors.METAL_COLOR)
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2))
        graphicsContext.drawLine(width / 2, margin + tabSize + bodySize, width / 2, height
                - margin)
        graphicsContext.drawLine(width / 2 - bodySize / 3, margin + tabSize + bodySize,
                width / 2 - bodySize / 3, height - margin)
        graphicsContext.drawLine(width / 2 + bodySize / 3, margin + tabSize + bodySize,
                width / 2 + bodySize / 3, height - margin)
    }

    public void setFolded(boolean folded) {
        this.folded = folded
        // Invalidate the body
        this.body = null
    }

    public void setLeadLength(Size leadLength) {
        this.leadLength = leadLength
        // Invalidate the body
        this.body = null
    }

}
