package org.diylc.components.passive

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Shape
import java.awt.geom.Rectangle2D

import org.diylc.components.AbstractLeadedComponent
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.IDIYComponent
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.annotations.PositiveMeasureValidator
import org.diylc.core.components.CreationMethod;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Capacitance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

import com.fasterxml.jackson.annotation.JsonIgnore;

@ComponentDescriptor(name = "Film Capacitor (axial)", author = "Branislav Stojkovic", category = "Passive", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "C", description = "Axial film capacitor, similar to Mallory 150s", zOrder = IDIYComponent.COMPONENT)
public class AxialFilmCapacitor extends AbstractLeadedComponent {

    public static final String id = "76fd7d46-587d-431d-b52b-1c008c928b7a"
    
    private static final long serialVersionUID = 1L

    private static Size DEFAULT_WIDTH = new Size(1d / 2, SizeUnit.in)
    
    private static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in)
    
    private static Color BODY_COLOR = Color.decode("#FFE303")
    
    private static Color BORDER_COLOR = BODY_COLOR.darker()

    @EditableProperty(validatorClass = PositiveMeasureValidator.class)
    Capacitance value = null
    
    @Deprecated
    @JsonIgnore
    Voltage voltage = Voltage._63V
    
    @EditableProperty(name = "Voltage")
    org.diylc.core.measures.Voltage voltageNew = null

    public AxialFilmCapacitor() {
        super()
        this.bodyColor = BODY_COLOR
        this.borderColor = BORDER_COLOR
    }

    @Override
    protected boolean supportsStandingMode() {
        return true
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

    @EditableProperty(name = "Reverse (standing)")
    public boolean getFlipStanding() {
        return super.getFlipStanding()
    }
}
