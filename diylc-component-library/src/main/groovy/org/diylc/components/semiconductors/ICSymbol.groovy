package org.diylc.components.semiconductors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Font;
import java.awt.Point
import java.awt.Polygon
import java.awt.Rectangle
import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.GeneralPath
import java.awt.geom.Rectangle2D

import org.diylc.components.AbstractTransparentComponent
import org.diylc.components.Angle
import org.diylc.components.Colors
import org.diylc.components.ControlPoint
import org.diylc.components.Geometry
import org.diylc.components.ICPointCount
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.Display
import org.diylc.core.IDIYComponent
import org.diylc.core.HorizontalAlignment
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.VerticalAlignment
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.components.ComponentState;
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit
import org.diylc.core.utils.Constants
import org.diylc.specifications.SpecificationModel
import org.diylc.specifications.ic.ICPinCount
import org.diylc.specifications.ic.ICSpecification
import org.diylc.specifications.ic.ICSpecificationEditor
import org.diylc.specifications.ic.ICSpecificationModel

import com.fasterxml.jackson.annotation.JsonProperty;

@ComponentDescriptor(name = "IC Symbol", author = "Nikolaj Brinch Jørgensen", category = "Schematics", instanceNamePrefix = "IC", description = "IC symbol", stretchable = false, zOrder = IDIYComponent.COMPONENT, rotatable = false)
public class ICSymbol extends AbstractTransparentComponent implements Geometry {

    public static final String id = "710e39a6-5e08-4a6c-9abd-85a88aa669ba"
    
    private static final long serialVersionUID = 1L

    private static int PIN_SPACING = new Size(0.1d, SizeUnit.in).convertToPixels()

    private static Color BODY_COLOR = Color.white

    private static Color BORDER_COLOR = Color.black

    private static float PIN_FONT_SIZE = 9f

    private String value

    transient private Shape[] body

    ControlPoint[] controlPoints = points(point(0, 0))

    @SpecificationModel(category = "IC", type = ICSpecification.class, editor = ICSpecificationEditor.class)
    @EditableProperty
    ICSpecificationModel icSpecificationModel = new ICSpecificationModel()

    @EditableProperty
    String value = ""

    @EditableProperty(name = "Body")
    Color bodyColor = BODY_COLOR

    @EditableProperty(name = "Border")
    Color borderColor = BORDER_COLOR

    @EditableProperty
    Display display = Display.NAME

    public ICSymbol() {
        super()
        updateControlPoints()
    }

    public void setIcSpecificationModel(ICSpecificationModel icSpecificationModel) {
        this.icSpecificationModel = icSpecificationModel
        updateControlPoints()
        body = null
    }

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
            Project project, IDrawingObserver drawingObserver) {
        if (checkPointsClipped(graphicsContext.getClip())) {
            return
        }
        int pinSpacing = PIN_SPACING
        Composite oldComposite = graphicsContext.getComposite()
        if (alpha < Colors.MAX_ALPHA) {
            graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
        }

        Shape[] body = getBody()

        graphicsContext.setColor(outlineMode ? Constants.TRANSPARENT_COLOR : bodyColor)
        graphicsContext.fill(body[0])
        graphicsContext.setComposite(oldComposite)
        Color finalBorderColor
        if (outlineMode) {
            Theme theme = Configuration.INSTANCE.getTheme()
            finalBorderColor = theme.getOutlineColor()
        } else {
            finalBorderColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : borderColor
        }
        graphicsContext.setColor(finalBorderColor)
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        graphicsContext.draw(body[1])
        
        componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : borderColor
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2))
        graphicsContext.draw(body[0])

        graphicsContext.setFont(LABEL_FONT.deriveFont(PIN_FONT_SIZE))

        controlPoints.each { ControlPoint controlPoint ->
            Point pinIdPoint = point(controlPoint.x, controlPoint.y)
            HorizontalAlignment pinIdHAlign = HorizontalAlignment.CENTER
            VerticalAlignment pinIdVAlign = VerticalAlignment.CENTER

            Point pinNamePoint = point(controlPoint.x, controlPoint.y)
            HorizontalAlignment pinNameHAlign = HorizontalAlignment.CENTER
            VerticalAlignment pinNameVAlign = VerticalAlignment.CENTER

            String side = controlPoint.properties['side']
            Angle angle = Angle._0

            switch (side) {
                case 'left':
                    pinIdPoint.translate(pinSpacing.intdiv(2), -pinSpacing.intdiv(3))
                    pinNamePoint.translate(pinSpacing + 4, 0)
                    pinNameHAlign = HorizontalAlignment.LEFT
                    break
                case 'top':
                    pinIdPoint.translate(pinSpacing.intdiv(2), pinSpacing.intdiv(3) * 2)
                    pinNamePoint.translate(3, pinSpacing + 5)
                    pinNameHAlign = HorizontalAlignment.LEFT
                    pinNameVAlign = VerticalAlignment.BOTTOM
                    angle = Angle._90
                    break
                case 'right':
                    pinIdPoint.translate(-pinSpacing.intdiv(2), -pinSpacing.intdiv(3))
                    pinNamePoint.translate(-(pinSpacing + 3), 0)
                    pinNameHAlign = HorizontalAlignment.RIGHT
                    break
                case 'bottom':
                    pinIdPoint.translate(pinSpacing.intdiv(2), pinSpacing.intdiv(3) * -2)
                    pinNamePoint.translate(-2, -(pinSpacing + 5))
                    pinNameHAlign = HorizontalAlignment.LEFT
                    pinNameVAlign = VerticalAlignment.BOTTOM
                    angle = Angle._270
                    break
            }

            drawCenteredText(graphicsContext, String.valueOf(controlPoint.properties['id']), pinIdPoint, pinIdHAlign, pinIdVAlign)
            drawCenteredText(graphicsContext, String.valueOf(controlPoint.properties['name'] ?: ''), pinNamePoint, pinNameHAlign, pinNameVAlign, angle)
        }

        /* 
         * Draw label
         */
        graphicsContext.setFont(LABEL_FONT)
        Color finalLabelColor
        if (outlineMode) {
            Theme theme = Configuration.INSTANCE.getTheme()
            finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED : theme
                    .getOutlineColor()
        } else {
            finalLabelColor = componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
                    : Colors.LABEL_COLOR
        }

        graphicsContext.setColor(finalLabelColor)
        ControlPoint firstPoint = controlPoints[0]
        int x = firstPoint.x
        int y = firstPoint.y
        
        if (firstPoint.properties['side'] == 'top') {
            x = x - 3 * pinSpacing
            y = y + 3 * pinSpacing
        }
        
        x = x + pinSpacing + body[0].bounds.width / 2
        y = y - 2 * pinSpacing + body[0].bounds.height / 2

        if (getValue()) {
            drawCenteredText(graphicsContext, getName(), point(x, y - 20), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM)
            
            Font smallFont = LABEL_FONT.deriveFont(toFloat(LABEL_FONT.getSize2D() - 3.0))
            graphicsContext.setFont(smallFont)
            
            drawCenteredText(graphicsContext, getValue(), point(x, y), HorizontalAlignment.CENTER, VerticalAlignment.TOP)
        } else {
            drawCenteredText(graphicsContext, getName(), point(x, y), HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        }
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int margin = 3 * width / 32
        Area area = new Area(new Polygon([margin, margin, width - margin ] as int[], [margin, height - margin, height / 2 ] as int[], 3))
        area.intersect(new Area(new Rectangle2D.Double(2 * margin, 0, width, height)))
        graphicsContext.setColor(BODY_COLOR)
        graphicsContext.fill(area)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.setFont(LABEL_FONT.deriveFont(8f))
        drawCenteredText(graphicsContext, "-", point(3 * margin, height / 3), HorizontalAlignment.CENTER,
                VerticalAlignment.CENTER)
        drawCenteredText(graphicsContext, "+", point(3 * margin + 1, height * 2 / 3), HorizontalAlignment.CENTER,
                VerticalAlignment.CENTER)
        graphicsContext.draw(area)
    }

    @Override
    public Point getControlPoint(int index) {
        return controlPoints[index]
    }

    @Override
    public int getControlPointCount() {
        return controlPoints.length
    }

    private void updateControlPoints() {
        int pinSpacing = PIN_SPACING

        int x = controlPoints[0].x
        int y = controlPoints[0].y

        List<ControlPoint> controlPoints = []

        icSpecificationModel.pinsLeft.eachWithIndex { pin, i ->
            controlPoints << point(x, y + i * pinSpacing, pin + ['side': 'left'])
        }
        if (!icSpecificationModel.pinsLeft.empty) {
            icSpecificationModel.pinsTop.eachWithIndex { pin, i ->
                controlPoints << point(x + 3 * pinSpacing + i * pinSpacing, y - 3 * pinSpacing, pin + ['side': 'top'])
            }
        } else {
            icSpecificationModel.pinsTop.eachWithIndex { pin, i ->
                controlPoints << point(x + i * pinSpacing, y, pin + ['side': 'top'])
            }

            x = x - 3 * pinSpacing
            y = y + 3 * pinSpacing            
        }

        icSpecificationModel.pinsRight.eachWithIndex { pin, i ->
            controlPoints << point(x + calculateWidth(pinSpacing) + 5 * pinSpacing, y + i * pinSpacing, pin + ['side': 'right'])
        }
        icSpecificationModel.pinsBottom.eachWithIndex { pin, i ->
            controlPoints << point(x + 3 * pinSpacing + i * pinSpacing, y + calculateHeight(pinSpacing) + 2 * pinSpacing, pin + ['side': 'bottom'])
        }

        this.controlPoints = controlPoints as ControlPoint[]
    }

    private Shape[] getBody() {
        if (body == null) {

            int pinSpacing = PIN_SPACING
            ControlPoint firstPoint = controlPoints[0]
            int x = firstPoint.x
            int y = firstPoint.y
            
            if (firstPoint.properties['side'] == 'top') {
                x = x - 3 * pinSpacing
                y = y + 3 * pinSpacing
            }

            int x1 = x + pinSpacing
            int y1 = y - 2 * pinSpacing
            int width = calculateWidth(pinSpacing) + 3 * pinSpacing
            int height = calculateHeight(pinSpacing) + 3 * pinSpacing
            
            Rectangle rectangle = rectangle(x1, y1, width, height)

            GeneralPath path = new GeneralPath()

            controlPoints.each { ControlPoint controlPoint ->
                if (controlPoint.x < rectangle.x && controlPoint.y > rectangle.y) {
                    path.moveTo(controlPoint.x, controlPoint.y)
                    path.lineTo(rectangle.x, controlPoint.y)
                } else if (controlPoint.y < rectangle.y) {
                    path.moveTo(controlPoint.x, controlPoint.y)
                    path.lineTo(controlPoint.x, rectangle.y)
                } else if (controlPoint.y > rectangle.y + rectangle.height) {
                    path.moveTo(controlPoint.x, rectangle.y + rectangle.height)
                    path.lineTo(controlPoint.x, controlPoint.y)
                } else {
                    path.moveTo(rectangle.x + rectangle.width, controlPoint.y)
                    path.lineTo(controlPoint.x, controlPoint.y)
                }
            }

            body = [rectangle, path] as Shape[]
        }

        return body
    }
    
    private double calculateWidth(int pinSpacing) {
        int width = icSpecificationModel?.specification?.width ?: 4
        
        return Math.max(width * pinSpacing, Math.max(icSpecificationModel.pinsTop.size(), icSpecificationModel.pinsBottom.size()) * pinSpacing)
    }
    
    private double calculateHeight(int pinSpacing) {
        int height = icSpecificationModel?.specification?.height ?: -1
        
        return Math.max(height * pinSpacing, Math.max(icSpecificationModel.pinsLeft.size(), icSpecificationModel.pinsRight.size()) * pinSpacing)
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
    public void setControlPoint(Point point, int index) {
        controlPoints[index].setLocation(point)
        body = null
    }

    public void setIcPointCount(ICPointCount icPointCount) {
        this.icPointCount = icPointCount
        updateControlPoints()
        body = null
    }

    public String getValue() {
        return icSpecificationModel.value
    }
}
