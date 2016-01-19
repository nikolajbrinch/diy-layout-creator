package org.diylc.components.passive

import java.awt.Color
import java.awt.Shape
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D

import org.diylc.components.AbstractRadialComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.core.CreationMethod
import org.diylc.core.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.Theme
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.annotations.PositiveMeasureValidator
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Capacitance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Electrolytic Capacitor", author = "Branislav Stojkovic", category = "Passive", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "C", description = "Vertical mounted electrolytic capacitor, polarized or bipolar", zOrder = IDIYComponent.COMPONENT)
public class RadialElectrolytic extends AbstractRadialComponent {

    private static final long serialVersionUID = 1L

    public static Size DEFAULT_SIZE = new Size(1d / 4, SizeUnit.in)
    public static Color BODY_COLOR = Color.decode("#EAADEA")
    public static Color BORDER_COLOR = BODY_COLOR.darker()
    public static Color MARKER_COLOR = Color.gray
    public static Color TICK_COLOR = Color.white
    public static Size HEIGHT = new Size(0.4d, SizeUnit.in)
    public static Size EDGE_RADIUS = new Size(1d, SizeUnit.mm)

    @Deprecated
    Voltage voltage = Voltage._63V

    @EditableProperty(validatorClass = PositiveMeasureValidator.class)
    Capacitance value = null

    @EditableProperty(name = "Voltage")
    org.diylc.core.measures.Voltage voltageNew = null

    @EditableProperty(name = "Marker")
    Color markerColor = MARKER_COLOR

    @EditableProperty(name = "Tick")
    Color tickColor = TICK_COLOR
    
    @EditableProperty(name = "Polarized")
    boolean polarized = true
    
    @EditableProperty
    boolean folded = false
    
    @EditableProperty
    Size height = HEIGHT
    
    @EditableProperty(name = "Invert polarity")
    boolean invert = false

    public RadialElectrolytic() {
        super()
        this.bodyColor = BODY_COLOR
        this.borderColor = BORDER_COLOR
    }

    @Override
    public String getValueForDisplay() {
        return getValue().toString() + (getVoltageNew() == null ? "" : " " + getVoltageNew().toString())
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.setColor(BODY_COLOR)
        int margin = 3
        Ellipse2D body = new Ellipse2D.Double(margin, margin,
                getClosestOdd(width - 2 * margin), getClosestOdd(width - 2
                * margin))
        graphicsContext.fill(body)
        Area marker = new Area(body)
        marker.subtract(new Area(new Rectangle2D.Double(margin, margin, width
                - 4 * margin, getClosestOdd(width - 2 * margin))))
        graphicsContext.setColor(MARKER_COLOR)
        graphicsContext.fill(marker)
        graphicsContext.setColor(TICK_COLOR)
        graphicsContext.drawLine(width - 2 * margin, height / 2 - 2, width - 2 * margin,
                height / 2 + 2)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.draw(body)
        // g2d.setColor(COVER_COLOR.darker());
        // g2d.drawLine(innerMargin + 2, innerMargin + 2, width - innerMargin -
        // 2, width - innerMargin
        // - 2);
        // g2d.drawLine(innerMargin + 2, width - innerMargin - 2, width -
        // innerMargin - 2,
        // innerMargin + 2);
    }

    @Override
    protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
        int height = (int) getHeight().convertToPixels()
        if (polarized) {
            int totalDiameter = getClosestOdd(getLength().convertToPixels())
            if (!outlineMode) {
                if (folded) {
                    Area area = new Area(getBodyShape())
                    if (invert) {
                        area.subtract(new Area(new Rectangle2D.Double(
                                totalDiameter * 0.2, -height, totalDiameter,
                                height * 2)))
                    } else {
                        area.subtract(new Area(new Rectangle2D.Double(0,
                                -height, totalDiameter * 0.8, height * 2)))
                    }
                    graphicsContext.setColor(markerColor)
                    graphicsContext.fill(area)
                } else {
                    Area area = new Area(getBodyShape())
                    if (invert) {
                        area.subtract(new Area(new Rectangle2D.Double(
                                totalDiameter * 0.2, 0, totalDiameter,
                                totalDiameter)))
                    } else {
                        area.subtract(new Area(new Rectangle2D.Double(0, 0,
                                totalDiameter * 0.8, totalDiameter)))
                    }
                    graphicsContext.setColor(markerColor)
                    graphicsContext.fill(area)
                }
            }
            Color finalTickColor
            if (outlineMode) {
                Theme theme = Configuration.INSTANCE.getTheme()
                finalTickColor = theme.getOutlineColor()
            } else {
                finalTickColor = tickColor
            }
            graphicsContext.setColor(finalTickColor)
            graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2))
            if (folded) {
                int tickLength = height / 7
                for (int i = 0; i < 3; i++) {
                    graphicsContext.drawLine(
                            (int) (totalDiameter * (invert ? 0.08 : 0.92)),
                            -height / 2 + tickLength + i * tickLength * 2,
                            (int) (totalDiameter * (invert ? 0.08 : 0.92)),
                            -height / 2 + tickLength + i * tickLength * 2
                            + tickLength)
                }
            } else {
                graphicsContext.drawLine((int) (totalDiameter * (invert ? 0.1 : 0.9)),
                        totalDiameter / 2 - (int) (totalDiameter * 0.05),
                        (int) (totalDiameter * (invert ? 0.1 : 0.9)),
                        totalDiameter / 2 + (int) (totalDiameter * 0.05))
            }
        }
        // int coverDiameter = getClosestOdd(totalDiameter * 3 / 4);
        // g2d.setColor(coverColor);
        // int position = (totalDiameter - coverDiameter) / 2;
        // g2d.fillOval(position, position, coverDiameter, coverDiameter);
        // g2d.setColor(coverColor.darker());
        // g2d.drawLine(position + coverDiameter / 5, position + coverDiameter /
        // 5, position + 4
        // * coverDiameter / 5, position + 4 * coverDiameter / 5);
        // g2d.drawLine(position + coverDiameter / 5, position + 4 *
        // coverDiameter / 5, position + 4
        // * coverDiameter / 5, position + coverDiameter / 5);
    }

    @Override
    protected Size getDefaultWidth() {
        return null
    }

    @Override
    public Size getWidth() {
        return super.getWidth()
    }

    @Override
    protected Size getDefaultLength() {
        // We'll reuse width property to set the diameter.
        return DEFAULT_SIZE
    }

    @EditableProperty(name = "Diameter")
    @Override
    public Size getLength() {
        return super.getLength()
    }

    @Override
    protected Shape getBodyShape() {
        double height = (int) getHeight().convertToPixels()
        double diameter = (int) getLength().convertToPixels()
        if (folded) {
            return new RoundRectangle2D.Double(0f, -height / 2
                    - LEAD_THICKNESS.convertToPixels() / 2,
                    getClosestOdd(diameter), getClosestOdd(height), EDGE_RADIUS
                    .convertToPixels(), EDGE_RADIUS.convertToPixels())
        }
        return new Ellipse2D.Double(0f, 0f, getClosestOdd(diameter),
                getClosestOdd(diameter))
    }
}
