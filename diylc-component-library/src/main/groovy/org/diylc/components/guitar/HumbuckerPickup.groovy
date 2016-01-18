package org.diylc.components.guitar

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.RoundRectangle2D

import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.Geometry
import org.diylc.core.ComponentState
import org.diylc.core.HorizontalAlignment;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Orientation;
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.VerticalAlignment;
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

@ComponentDescriptor(name = "Humbucker Pickup", category = "Guitar", author = "Branislav Stojkovic", description = "PAF-style humbucker guitar pickup", stretchable = false, zOrder = IDIYComponent.COMPONENT, instanceNamePrefix = "PKP", autoEdit = false)
public class HumbuckerPickup extends AbstractTransparentComponent<String> implements Geometry {

    private static final long serialVersionUID = 1L

    private static Color BODY_COLOR = Color.lightGray
    private static Color POINT_COLOR = Color.darkGray
    private static Size WIDTH = new Size(36.5d, SizeUnit.mm)
    private static Size LENGTH = new Size(68.58d, SizeUnit.mm)
    private static Size LIP_WIDTH = new Size(12.7d, SizeUnit.mm)
    private static Size LIP_LENGTH = new Size(7.9d, SizeUnit.mm)
    private static Size EDGE_RADIUS = new Size(4d, SizeUnit.mm)
    private static Size POINT_MARGIN = new Size(5d, SizeUnit.mm)
    private static Size POINT_SIZE = new Size(3d, SizeUnit.mm)
    private static Size LIP_HOLE_SIZE = new Size(1.5d, SizeUnit.mm)
    private static Size POLE_SIZE = new Size(3d, SizeUnit.mm)
    private static Size POLE_SPACING = new Size(10.1d, SizeUnit.mm)
    private static Size COIL_SPACING = new Size(18d, SizeUnit.mm)

    private Point controlPoint = new Point(0, 0)
    transient Shape[] body

    @EditableProperty(name = "Model")
    String value = ""

    @EditableProperty
    Orientation orientation = Orientation.DEFAULT

    @EditableProperty
    Color color = BODY_COLOR

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
            Project project, IDrawingObserver drawingObserver) {
        Shape[] body = getBody()

        graphicsContext.with {
            setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
            if (componentState != ComponentState.DRAGGING) {
                Composite oldComposite = getComposite()
                if (alpha < Colors.MAX_ALPHA) {
                    setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
                }
                setColor(outlineMode ? Constants.TRANSPARENT_COLOR : color)
                fill(body[0])
                fill(body[1])
                setColor(outlineMode ? Constants.TRANSPARENT_COLOR : POINT_COLOR)
                fill(body[2])
                setComposite(oldComposite)
            }

            Color finalBorderColor
            if (outlineMode) {
                Theme theme = Configuration.INSTANCE.getTheme()
                finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : theme.getOutlineColor()
            } else {
                finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : color.darker()
            }

            setColor(finalBorderColor)
            draw(body[0])
            draw(body[1])
            if (!outlineMode) {
                setColor(color.darker())
                draw(body[3])
            }

            Color finalLabelColor
            if (outlineMode) {
                Theme theme = Configuration.INSTANCE.getTheme()
                finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED : theme
                        .getOutlineColor()
            } else {
                finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
                        : Colors.LABEL_COLOR
            }
            setColor(finalLabelColor)
            setFont(LABEL_FONT)
            Rectangle bounds = body[0].getBounds()
            drawCenteredText(graphicsContext, value, point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2),
                    HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        }
    }

    public Shape[] getBody() {
        if (body == null) {
            body = new Shape[4]

            int x = controlPoint.x
            int y = controlPoint.y
            int width = (int) WIDTH.convertToPixels()
            int length = (int) LENGTH.convertToPixels()
            int lipWidth = (int) LIP_WIDTH.convertToPixels()
            int lipLength = (int) LIP_LENGTH.convertToPixels()
            int edgeRadius = (int) EDGE_RADIUS.convertToPixels()
            int pointMargin = (int) POINT_MARGIN.convertToPixels()
            int pointSize = getClosestOdd(POINT_SIZE.convertToPixels())
            int lipHoleSize = getClosestOdd(LIP_HOLE_SIZE.convertToPixels())

            body[0] = new Area(new RoundRectangle2D.Double(x + pointMargin - length, y
                    - pointMargin, length, width, edgeRadius, edgeRadius))
            body[1] = new Area(new RoundRectangle2D.Double(x + pointMargin - length - lipLength, y
                    - pointMargin + width / 2 - lipWidth / 2, length + 2 * lipLength, lipWidth,
                    edgeRadius / 2, edgeRadius / 2))
            Area lipArea = new Area(body[1])
            lipArea.subtract(new Area(body[0]))
            lipArea.subtract(new Area(new Ellipse2D.Double(
                    x + pointMargin - length - lipLength / 2, y - pointMargin + width / 2
                    - lipHoleSize / 2, lipHoleSize, lipHoleSize)))
            lipArea.subtract(new Area(new Ellipse2D.Double(x + pointMargin + lipLength / 2, y
                    - pointMargin + width / 2 - lipHoleSize / 2, lipHoleSize, lipHoleSize)))
            body[1] = lipArea
            body[2] = new Area(new Ellipse2D.Double(x - pointSize / 2, y - pointSize / 2,
                    pointSize, pointSize))

            int poleSize = (int) POLE_SIZE.convertToPixels()
            int poleSpacing = (int) POLE_SPACING.convertToPixels()
            int coilSpacing = (int) COIL_SPACING.convertToPixels()
            int coilMargin = (width - coilSpacing) / 2
            int poleMargin = (length - poleSpacing * 5) / 2
            Area poleArea = new Area()
            for (int i = 0; i < 6; i++) {
                Ellipse2D pole = new Ellipse2D.Double(x + pointMargin - length + poleMargin + i
                        * poleSpacing - poleSize / 2, y - pointMargin + coilMargin - poleSize / 2,
                        poleSize, poleSize)
                poleArea.add(new Area(pole))
                pole = new Ellipse2D.Double(x + pointMargin - length + poleMargin + i * poleSpacing
                        - poleSize / 2, y - pointMargin + width - coilMargin - poleSize / 2,
                        poleSize, poleSize)
                poleArea.add(new Area(pole))
            }
            body[3] = poleArea

            // Rotate if needed
            if (orientation != Orientation.DEFAULT) {
                double theta = 0
                switch (orientation) {
                    case Orientation._90:
                        theta = Math.PI / 2
                        break
                    case Orientation._180:
                        theta = Math.PI
                        break
                    case Orientation._270:
                        theta = Math.PI * 3 / 2
                        break
                }
                AffineTransform rotation = AffineTransform.getRotateInstance(theta, x, y)
                for (Shape shape : body) {
                    Area area = (Area) shape
                    area.transform(rotation)
                }
            }
        }
        return body
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int baseWidth = 16 * width / 32
        int baseLength = 27 * width / 32

        graphicsContext.with {
            setColor(BODY_COLOR)
            fillRoundRect((width - baseWidth / 4) / 2, 0, baseWidth / 4, height - 1,
                    2 * width / 32, 2 * width / 32)
            setColor(BODY_COLOR.darker())
            drawRoundRect((width - baseWidth / 4) / 2, 0, baseWidth / 4, height - 1,
                    2 * width / 32, 2 * width / 32)

            setColor(BODY_COLOR)
            fillRoundRect((width - baseWidth) / 2, (height - baseLength) / 2, baseWidth,
                    baseLength, 4 * width / 32, 4 * width / 32)
            setColor(BODY_COLOR.darker())
            drawRoundRect((width - baseWidth) / 2, (height - baseLength) / 2, baseWidth,
                    baseLength, 4 * width / 32, 4 * width / 32)
        }
    }

    @Override
    public int getControlPointCount() {
        return 1
    }

    @Override
    public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
        return VisibilityPolicy.WHEN_SELECTED
    }

    @Override
    public boolean isControlPointSticky(int index) {
        return true
    }

    @Override
    public Point getControlPoint(int index) {
        return controlPoint
    }

    @Override
    public void setControlPoint(Point point, int index) {
        this.controlPoint.setLocation(point)
        // Invalidate the body
        body = null
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation
        // Invalidate the body
        body = null
    }

}
