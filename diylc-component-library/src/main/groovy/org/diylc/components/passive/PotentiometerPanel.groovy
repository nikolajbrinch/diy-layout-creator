package org.diylc.components.passive

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.FontMetrics
import java.awt.Point
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D

import org.diylc.components.AbstractPotentiometer
import org.diylc.components.Geometry
import org.diylc.core.components.annotations.ComponentAutoEdit
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.annotations.ComponentEditOptions
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache
import org.diylc.core.Orientation
import org.diylc.core.Project
import org.diylc.core.Theme
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentAutoEdit
@ComponentEditOptions(stretchable = false)
@ComponentDescriptor(name = "Potentiometer (panel mount)", author = "Branislav Stojkovic", category = "Passive", instanceNamePrefix = "VR", description = "Panel mount potentiometer with solder lugs")
public class PotentiometerPanel extends AbstractPotentiometer implements Geometry {

    public static final String id = "40127c86-2949-479a-a537-9c86be087e85"

    private static final long serialVersionUID = 1L

    private static Size BODY_DIAMETER = new Size(1d, SizeUnit.in)
    
    private static Size SPACING = new Size(0.3d, SizeUnit.in)
    
    private static Size LUG_DIAMETER = new Size(0.15d, SizeUnit.in)
    
    private static Color BODY_COLOR = Color.gray.brighter()
    
    private static Color BORDER_COLOR = Color.gray

    // Array of 7 elements: 3 lug connectors, 1 pot body and 3 lugs
    transient protected Area[] body = null

    @EditableProperty(name = "Diameter")
    Size bodyDiameter = BODY_DIAMETER

    @EditableProperty
    Size spacing = SPACING

    @EditableProperty(name = "Lug size")
    Size lugDiameter = LUG_DIAMETER

    @EditableProperty(name = "Body")
    Color bodyColor = BODY_COLOR

    @EditableProperty(name = "Border")
    Color borderColor = BORDER_COLOR

    public PotentiometerPanel() {
        controlPoints = points( point(0, 0), point(0, 0),
                point(0, 0) )
        updateControlPoints()
    }

    protected void updateControlPoints() {
        int spacing = (int) this.spacing.convertToPixels()
        switch (orientation) {
            case Orientation.DEFAULT:
                controlPoints[1].setLocation(controlPoints[0].x + spacing,
                controlPoints[0].y)
                controlPoints[2].setLocation(controlPoints[0].x + 2 * spacing,
                        controlPoints[0].y)
                break
            case Orientation._90:
                controlPoints[1].setLocation(controlPoints[0].x, controlPoints[0].y
                + spacing)
                controlPoints[2].setLocation(controlPoints[0].x, controlPoints[0].y
                        + 2 * spacing)
                break
            case Orientation._180:
                controlPoints[1].setLocation(controlPoints[0].x - spacing,
                controlPoints[0].y)
                controlPoints[2].setLocation(controlPoints[0].x - 2 * spacing,
                        controlPoints[0].y)
                break
            case Orientation._270:
                controlPoints[1].setLocation(controlPoints[0].x, controlPoints[0].y
                - spacing)
                controlPoints[2].setLocation(controlPoints[0].x, controlPoints[0].y
                        - 2 * spacing)
                break
            default:
                break
        }
    }

    public Area[] getBody() {
        int spacing = (int) this.spacing.convertToPixels()
        int diameter = getClosestOdd(bodyDiameter.convertToPixels())
        if (this.@body == null) {
            this.@body = new Area[7]

            // Add lugs.
            int lugDiameter = getClosestOdd(this.lugDiameter.convertToPixels())
            int holeDiameter = getClosestOdd(this.lugDiameter.convertToPixels() / 2)
            for (int i = 0; i < 3; i++) {
                Area area = new Area(new Ellipse2D.Double(controlPoints[i].x - lugDiameter / 2, controlPoints[i].y - lugDiameter / 2, lugDiameter, lugDiameter))
                this.@body[4 + i] = area
            }

            switch (orientation) {
                case Orientation.DEFAULT:
                    this.@body[3] = new Area(new Ellipse2D.Double(controlPoints[0].x + spacing - diameter / 2, controlPoints[0].y - spacing / 2 - diameter, diameter, diameter))
                    for (int i = 0; i < 3; i++) {
                        this.@body[i] = new Area(new Rectangle2D.Double(controlPoints[i].x - holeDiameter / 2, controlPoints[i].y - (spacing + diameter) / 2, holeDiameter, (spacing + diameter) / 2))
                    }
                    break
                case Orientation._90:
                    this.@body[3] = new Area(new Ellipse2D.Double(controlPoints[0].x + spacing / 2, controlPoints[0].y + spacing - diameter / 2, diameter, diameter))
                    for (int i = 0; i < 3; i++) {
                        this.@body[i] = new Area(new Rectangle2D.Double(
                                controlPoints[i].x, controlPoints[i].y
                                - holeDiameter / 2,
                                (spacing + diameter) / 2, holeDiameter))
                    }
                    break
                case Orientation._180:
                    this.@body[3] = new Area(new Ellipse2D.Double(controlPoints[0].x - spacing - diameter / 2, controlPoints[0].y + spacing / 2, diameter, diameter))
                    for (int i = 0; i < 3; i++) {
                        this.@body[i] = new Area(new Rectangle2D.Double(controlPoints[i].x - holeDiameter / 2, controlPoints[i].y, holeDiameter, (spacing + diameter) / 2))
                    }
                    break
                case Orientation._270:
                    this.@body[3] = new Area(new Ellipse2D.Double(controlPoints[0].x - spacing / 2 - diameter, controlPoints[0].y - spacing - diameter / 2, diameter, diameter))
                    for (int i = 0; i < 3; i++) {
                        this.@body[i] = new Area(new Rectangle2D.Double(
                                controlPoints[i].x - (spacing + diameter) / 2,
                                controlPoints[i].y - holeDiameter / 2,
                                (spacing + diameter) / 2, holeDiameter))
                    }
                    break
                default:
                    break
            }
            for (int i = 0; i < 3; i++) {
                for (int j = 3; j < 7; j++) {
                    this.@body[i].subtract(this.@body[j])
                }
            }
            // Make holes in the lugs.
            for (int i = 0; i < 3; i++) {
                this.@body[4 + i].subtract(new Area(new Ellipse2D.Double(
                        controlPoints[i].x - holeDiameter / 2,
                        controlPoints[i].y - holeDiameter / 2, holeDiameter,
                        holeDiameter)))
            }
        }
        return this.@body
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
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        Theme theme = Configuration.INSTANCE.getTheme()
        for (Area shape : getBody()) {
            if (shape != null) {
                graphicsContext.setColor(bodyColor)
                Composite oldComposite = graphicsContext.getComposite()
                if (alpha < Colors.MAX_ALPHA) {
                    graphicsContext.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
                }
                if (!outlineMode) {
                    graphicsContext.fill(shape)
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
                graphicsContext.draw(shape)
            }
        }
        // Draw caption.
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
        Rectangle2D bodyRect = getBody()[3].getBounds2D()
        Rectangle2D rect = fontMetrics.getStringBounds(getName(), graphicsContext.graphics2D)

        int textHeight = (int) rect.getHeight()
        int textWidth = (int) rect.getWidth()
        int panelHeight = (int) bodyRect.getHeight()
        int panelWidth = (int) bodyRect.getWidth()

        int x = (panelWidth - textWidth) / 2
        int y = panelHeight / 2 - textHeight + fontMetrics.getAscent()

        graphicsContext.drawString(getName(), (int) (bodyRect.getX() + x), (int) (bodyRect
                .getY() + y))

        // Draw value.
        rect = fontMetrics.getStringBounds(getValueForDisplay(), graphicsContext.graphics2D)

        textHeight = (int) rect.getHeight()
        textWidth = (int) rect.getWidth()

        x = (panelWidth - textWidth) / 2
        y = panelHeight / 2 + fontMetrics.getAscent()

        graphicsContext.drawString(getValueForDisplay(), (int) (bodyRect.getX() + x),
                (int) (bodyRect.getY() + y))
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int margin = 4 * width / 32
        int spacing = width / 3 - 1
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(toInt(2 * width / 32)))
        graphicsContext.drawLine(width / 2 - spacing, height / 2, width / 2 - spacing,
                height - margin)
        graphicsContext.drawLine(width / 2 + spacing, height / 2, width / 2 + spacing,
                height - margin)
        graphicsContext.drawLine(width / 2, height / 2, width / 2, height - margin)
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(toInt(4 * width / 32)))
        graphicsContext.drawLine(width / 2 - spacing, height - margin, width / 2 - spacing,
                height - margin)
        graphicsContext.drawLine(width / 2 + spacing, height - margin, width / 2 + spacing,
                height - margin)
        graphicsContext.drawLine(width / 2, height - margin, width / 2, height - margin)
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
        graphicsContext.setColor(BODY_COLOR)
        graphicsContext.fillOval(margin, margin / 2, width - 2 * margin, height - 2
                * margin)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawOval(margin, margin / 2, width - 2 * margin, height - 2
                * margin)
    }

    public void setSpacing(Size spacing) {
        this.@spacing = spacing
        updateControlPoints()
        body = null
    }

    public void setBodyDiameter(Size bodyDiameter) {
        this.@bodyDiameter = bodyDiameter
        body = null
    }

    public void setLugDiameter(Size lugDiameter) {
        this.@lugDiameter = lugDiameter
        body = null
    }

}
