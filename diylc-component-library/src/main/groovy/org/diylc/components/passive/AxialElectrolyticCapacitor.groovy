package org.diylc.components.passive

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Shape
import java.awt.geom.Rectangle2D

import org.diylc.components.AbstractLeadedComponent
import org.diylc.core.components.annotations.ComponentAutoEdit;
import org.diylc.core.components.annotations.ComponentCreationMethod;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.CreationMethod
import org.diylc.core.components.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.Theme
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.components.properties.PositiveMeasureValidator
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Capacitance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentAutoEdit
@ComponentCreationMethod(CreationMethod.POINT_BY_POINT)
@ComponentDescriptor(name = "Electrolytic Capacitor (axial)", author = "Branislav Stojkovic", category = "Passive", instanceNamePrefix = "C", description = "Axial electrolytic capacitor, similar to Sprague Atom, F&T, etc")
public class AxialElectrolyticCapacitor extends AbstractLeadedComponent {

    public static final String id = "513075ee-b4b5-4850-afbc-4353b231c5fe"
    
    private static final long serialVersionUID = 1L

    private static Size DEFAULT_WIDTH = new Size(1d / 2, SizeUnit.in)
    
    private static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in)
    
    private static Color BODY_COLOR = Color.decode("#EAADEA")
    
    private static Color BORDER_COLOR = BODY_COLOR.darker()
    
    private static Color MARKER_COLOR = Color.gray
    
    private static Color TICK_COLOR = Color.white

    @EditableProperty(validatorClass = PositiveMeasureValidator.class)
    Capacitance value = null

    @Deprecated
    Voltage voltage = Voltage._63V

    @EditableProperty(name = "Voltage")
    org.diylc.core.measures.Voltage voltageNew = null

    @EditableProperty(name = "Marker")
    Color markerColor = MARKER_COLOR
    
    @EditableProperty(name = "Tick")
    Color tickColor = TICK_COLOR
    
    @EditableProperty(name = "Polarized")
    boolean polarized = true

    public AxialElectrolyticCapacitor() {
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
        graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
        graphicsContext.setColor(Colors.LEAD_COLOR_ICON)
        graphicsContext.drawLine(0, height / 2, width, height / 2)
        graphicsContext.setColor(BODY_COLOR)
        graphicsContext.fillRect(4, height / 2 - 3, width - 8, 6)
        graphicsContext.setColor(MARKER_COLOR)
        graphicsContext.fillRect(width - 9, height / 2 - 3, 5, 6)
        graphicsContext.setColor(TICK_COLOR)
        graphicsContext.drawLine(width - 6, height / 2 - 1, width - 6, height / 2 + 1)
        graphicsContext.setColor(BORDER_COLOR)
        graphicsContext.drawRect(4, height / 2 - 3, width - 8, 6)
    }

    @Override
    protected Size getDefaultWidth() {
        return DEFAULT_HEIGHT
    }

    @Override
    protected Size getDefaultLength() {
        return DEFAULT_WIDTH
    }

    @Override
    protected Shape getBodyShape() {
        return new Rectangle2D.Double(0f, 0f, getLength().convertToPixels(),
                getClosestOdd(getWidth().convertToPixels()))
    }

    @Override
    protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
        if (polarized) {
            int width = getClosestOdd(getWidth().convertToPixels())
            int markerLength = (int) (getLength().convertToPixels() * 0.2)
            if (!outlineMode) {
                graphicsContext.setColor(markerColor)
                graphicsContext.fillRect(
                        (int) getLength().convertToPixels() - markerLength, 0,
                        markerLength, width)
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
            graphicsContext.drawLine(
                    (int) getLength().convertToPixels() - markerLength / 2,
                    (int) (width / 2 - width * 0.15), (int) getLength()
                    .convertToPixels()
                    - markerLength / 2,
                    (int) (width / 2 + width * 0.15))
        }
    }
}
