package org.diylc.components.electromechanical

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Point
import java.awt.Shape
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.RoundRectangle2D

import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.Geometry
import org.diylc.components.ToggleSwitchType
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.annotations.ComponentEditOptions
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.OrientationHV;
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants

import com.thoughtworks.xstream.annotations.XStreamAlias;

@ComponentEditOptions(stretchable = false)
@ComponentDescriptor(name = "Mini Toggle Switch", category = "Electromechanical", author = "Branislav Stojkovic", description = "Panel mounted mini toggle switch", instanceNamePrefix = "SW")
public class MiniToggleSwitch extends AbstractTransparentComponent implements Geometry {

    public static final String id = "582c5b97-45e0-407b-a6f7-aa24f329af6d"
    
    private static final long serialVersionUID = 1L

    private static Size SPACING = new Size(0.2d, SizeUnit.in)
    
    private static Size MARGIN = new Size(0.08d, SizeUnit.in)
    
    private static Size CIRCLE_SIZE = new Size(0.09d, SizeUnit.in)
    
    private static Size LUG_WIDTH = new Size(0.060d, SizeUnit.in)
    
    private static Size LUG_THICKNESS = new Size(0.02d, SizeUnit.in)

    private static Color BODY_COLOR = Color.decode("#3299CC")
    
    private static Color BORDER_COLOR = BODY_COLOR.darker()
    
    private static Color CIRCLE_COLOR = Color.decode("#FFFFAA")

    protected Point[] controlPoints = [new Point(0, 0) ] as Point[]
    
    transient protected Shape body

    @EditableProperty
    String name

    @XStreamAlias("switchType")
    @EditableProperty(name = "Type")
    ToggleSwitchType value = ToggleSwitchType.DPDT
    
    @EditableProperty
    OrientationHV orientation = OrientationHV.VERTICAL

    public MiniToggleSwitch() {
        super()
        updateControlPoints()
    }

    private void updateControlPoints() {
        Point firstPoint = controlPoints[0]
        int spacing = (int) SPACING.convertToPixels()
        switch (value) {
            case ToggleSwitchType.SPST:
                controlPoints = points(firstPoint,
                point(firstPoint.x, firstPoint.y + spacing))
                break
            case ToggleSwitchType.SPDT:
                controlPoints = points(firstPoint,
                point(firstPoint.x, firstPoint.y + spacing),
                point(firstPoint.x, firstPoint.y + 2 * spacing))
                break
            case ToggleSwitchType.DPDT:
                controlPoints = points(firstPoint,
                point(firstPoint.x, firstPoint.y + spacing),
                point(firstPoint.x, firstPoint.y + 2 * spacing),
                point(firstPoint.x + spacing, firstPoint.y),
                point(firstPoint.x + spacing, firstPoint.y + spacing),
                point(firstPoint.x + spacing, firstPoint.y + 2 * spacing))
                break
            case ToggleSwitchType._3PDT:
                controlPoints = points(firstPoint,
                point(firstPoint.x, firstPoint.y + spacing),
                point(firstPoint.x, firstPoint.y + 2 * spacing),
                point(firstPoint.x + spacing, firstPoint.y),
                point(firstPoint.x + spacing, firstPoint.y + spacing),
                point(firstPoint.x + spacing, firstPoint.y + 2 * spacing),
                point(firstPoint.x + 2 * spacing, firstPoint.y),
                point(firstPoint.x + 2 * spacing, firstPoint.y + spacing),
                point(firstPoint.x + 2 * spacing, firstPoint.y + 2 * spacing))
                break
            case ToggleSwitchType._4PDT:
                controlPoints = points(firstPoint,
                point(firstPoint.x, firstPoint.y + spacing),
                point(firstPoint.x, firstPoint.y + 2 * spacing),
                point(firstPoint.x + spacing, firstPoint.y),
                point(firstPoint.x + spacing, firstPoint.y + spacing),
                point(firstPoint.x + spacing, firstPoint.y + 2 * spacing),
                point(firstPoint.x + 2 * spacing, firstPoint.y),
                point(firstPoint.x + 2 * spacing, firstPoint.y + spacing),
                point(firstPoint.x + 2 * spacing, firstPoint.y + 2 * spacing),
                point(firstPoint.x + 3 * spacing, firstPoint.y),
                point(firstPoint.x + 3 * spacing, firstPoint.y + spacing),
                point(firstPoint.x + 3 * spacing, firstPoint.y + 2 * spacing))
                break
            case ToggleSwitchType._5PDT:
                controlPoints = points(firstPoint,
                point(firstPoint.x, firstPoint.y + spacing),
                point(firstPoint.x, firstPoint.y + 2 * spacing),
                point(firstPoint.x + spacing, firstPoint.y),
                point(firstPoint.x + spacing, firstPoint.y + spacing),
                point(firstPoint.x + spacing, firstPoint.y + 2 * spacing),
                point(firstPoint.x + 2 * spacing, firstPoint.y),
                point(firstPoint.x + 2 * spacing, firstPoint.y + spacing),
                point(firstPoint.x + 2 * spacing, firstPoint.y + 2 * spacing),
                point(firstPoint.x + 3 * spacing, firstPoint.y),
                point(firstPoint.x + 3 * spacing, firstPoint.y + spacing),
                point(firstPoint.x + 3 * spacing, firstPoint.y + 2 * spacing),
                point(firstPoint.x + 4 * spacing, firstPoint.y),
                point(firstPoint.x + 4 * spacing, firstPoint.y + spacing),
                point(firstPoint.x + 4 * spacing, firstPoint.y + 2 * spacing))
                break
        }
        AffineTransform xform = AffineTransform.getRotateInstance(-Math.PI / 2,
                firstPoint.x, firstPoint.y)
        if (getOrientation() == OrientationHV.HORIZONTAL) {
            for (int i = 1; i < controlPoints.length; i++) {
                xform.transform(controlPoints[i], controlPoints[i])
            }
        }
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
    public int getControlPointCount() {
        return controlPoints.length
    }

    @Override
    public void setControlPoint(Point point, int index) {
        controlPoints[index].setLocation(point)
        // Reset body shape.
        body = null
    }

    public void setValue(ToggleSwitchType value) {
        this.value = value
        updateControlPoints()
        // Reset body shape.
        body = null
    }

    public void setOrientation(OrientationHV orientation) {
        this.orientation = orientation
        updateControlPoints()
        // Reset body shape.
        body = null
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }
        Shape body = getBody()
        Theme theme = Configuration.INSTANCE.getTheme()
        // Draw body if available.
        if (body != null) {
            Composite oldComposite = graphicsContext.getComposite()
            if (alpha < Colors.MAX_ALPHA) {
                graphicsContext.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
            }
            graphicsContext
                    .setColor(outlineMode ? Constants.TRANSPARENT_COLOR
                    : BODY_COLOR)
            graphicsContext.fill(body)
            graphicsContext.setComposite(oldComposite)
            graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
            Color finalBorderColor
            if (outlineMode) {
                finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                        : theme.getOutlineColor()
            } else {
                finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                        : BORDER_COLOR
            }
            graphicsContext.setColor(finalBorderColor)
            graphicsContext.draw(body)
        }
        // Do not track these changes because the whole switch has been tracked
        // so far.
        drawingObserver.stopTracking()
        // Draw lugs.
        int circleDiameter = getClosestOdd((int) CIRCLE_SIZE.convertToPixels())
        int lugWidth = getClosestOdd((int) LUG_WIDTH.convertToPixels())
        int lugHeight = getClosestOdd((int) LUG_THICKNESS.convertToPixels())
        for (Point p : controlPoints) {
            if (outlineMode) {
                graphicsContext.setColor(theme.getOutlineColor())
                graphicsContext.drawRect(p.x - lugWidth / 2, p.y - lugHeight / 2, lugWidth,
                        lugHeight)
            } else {
                graphicsContext.setColor(CIRCLE_COLOR)
                graphicsContext.fillOval(p.x - circleDiameter / 2,
                        p.y - circleDiameter / 2, circleDiameter,
                        circleDiameter)
                graphicsContext.setColor(Colors.METAL_COLOR)
                graphicsContext.fillRect(p.x - lugWidth / 2, p.y - lugHeight / 2, lugWidth,
                        lugHeight)
            }
        }
    }

    public Shape getBody() {
        if (body == null) {
            Point firstPoint = controlPoints[0]
            int margin = (int) MARGIN.convertToPixels()
            int spacing = (int) SPACING.convertToPixels()
            switch (value) {
                case ToggleSwitchType.SPST:
                    body = new RoundRectangle2D.Double(firstPoint.x - margin,
                    firstPoint.y - margin, 2 * margin,
                    2 * margin + spacing, margin, margin)
                    break
                case ToggleSwitchType.SPDT:
                    body = new RoundRectangle2D.Double(firstPoint.x - margin,
                    firstPoint.y - margin, 2 * margin, 2 * margin + 2
                    * spacing, margin, margin)
                    break
                case ToggleSwitchType.DPDT:
                    body = new RoundRectangle2D.Double(firstPoint.x - margin,
                    firstPoint.y - margin, 2 * margin + spacing, 2 * margin
                    + 2 * spacing, margin, margin)
                    break
                case ToggleSwitchType._3PDT:
                    body = new RoundRectangle2D.Double(firstPoint.x - margin,
                    firstPoint.y - margin, 2 * margin + 2 * spacing, 2
                    * margin + 2 * spacing, margin, margin)
                    break
                case ToggleSwitchType._4PDT:
                    body = new RoundRectangle2D.Double(firstPoint.x - margin,
                    firstPoint.y - margin, 2 * margin + 3 * spacing, 2
                    * margin + 2 * spacing, margin, margin)
                    break
                case ToggleSwitchType._5PDT:
                    body = new RoundRectangle2D.Double(firstPoint.x - margin,
                    firstPoint.y - margin, 2 * margin + 4 * spacing, 2
                    * margin + 2 * spacing, margin, margin)
                    break
            }
            if (getOrientation() == OrientationHV.HORIZONTAL) {
                AffineTransform xform = AffineTransform.getRotateInstance(
                        -Math.PI / 2, firstPoint.x, firstPoint.y)
                body = new Area(body)
                ((Area) body).transform(xform)
            }
        }
        return body
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int circleSize = 5 * width / 32
        graphicsContext.setColor(BODY_COLOR)
        graphicsContext.fillRoundRect(width / 4, 1, width / 2, height - 2, circleSize,
                circleSize)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawRoundRect(width / 4, 1, width / 2, height - 2, circleSize,
                circleSize)
        for (int i = 1; i <= 3; i++) {
            graphicsContext.setColor(CIRCLE_COLOR)
            graphicsContext.fillOval(width / 2 - circleSize / 2, i * height / 4 - 3,
                    circleSize, circleSize)
            graphicsContext.setColor(Colors.METAL_COLOR)
            graphicsContext.drawLine(width / 2 - circleSize / 2 + 1, i * height / 4 - 1,
                    width / 2 + circleSize / 2 - 1, i * height / 4 - 1)
        }
    }
}
