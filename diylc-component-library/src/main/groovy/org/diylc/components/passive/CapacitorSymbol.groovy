package org.diylc.components.passive

import java.awt.Shape
import java.awt.geom.GeneralPath

import org.diylc.components.AbstractSchematicLeadedSymbol
import org.diylc.components.ComponentDescriptor
import org.diylc.core.CreationMethod
import org.diylc.core.IDIYComponent
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.annotations.PositiveMeasureValidator
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Capacitance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Capacitor (schematic symbol)", author = "Branislav Stojkovic", category = "Schematics", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "C", description = "Capacitor schematic symbol with an optional polarity sign", zOrder = IDIYComponent.COMPONENT)
public class CapacitorSymbol extends AbstractSchematicLeadedSymbol<Capacitance> {

    private static final long serialVersionUID = 1L

    public static Size DEFAULT_LENGTH = new Size(0.05, SizeUnit.in)
    public static Size DEFAULT_WIDTH = new Size(0.15, SizeUnit.in)

    private Capacitance value = null
    @Deprecated
    private Voltage voltage = Voltage._63V
    private Voltage voltageNew = null
    private boolean polarized = false

    @EditableProperty(validatorClass = PositiveMeasureValidator.class)
    public Capacitance getValue() {
        return value
    }

    public void setValue(Capacitance value) {
        this.value = value
    }

    @Override
    public String getValueForDisplay() {
        return getValue().toString() + (getVoltageNew() == null ? "" : " " + getVoltageNew().toString())
    }

    @Deprecated
    public Voltage getVoltage() {
        return voltage
    }

    @Deprecated
    public void setVoltage(Voltage voltage) {
        this.voltage = voltage
    }

    @EditableProperty(name = "Voltage")
    public Voltage getVoltageNew() {
        return voltageNew
    }

    public void setVoltageNew(Voltage voltageNew) {
        this.voltageNew = voltageNew
    }

    @EditableProperty
    public boolean getPolarized() {
        return polarized
    }

    public void setPolarized(boolean polarized) {
        this.polarized = polarized
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
        graphicsContext.setColor(LEAD_COLOR)
        graphicsContext.drawLine(0, height / 2, 13, height / 2)
        graphicsContext.drawLine(width - 13, height / 2, width, height / 2)
        graphicsContext.setColor(COLOR)
        graphicsContext.drawLine(14, height / 2 - 6, 14, height / 2 + 6)
        graphicsContext.drawLine(width - 14, height / 2 - 6, width - 14, height / 2 + 6)
    }

    @Override
    protected Size getDefaultWidth() {
        return DEFAULT_WIDTH
    }

    @Override
    protected Size getDefaultLength() {
        return DEFAULT_LENGTH
    }

    @Override
    protected Shape getBodyShape() {
        GeneralPath polyline = new GeneralPath()
        double length = getLength().convertToPixels()
        double width = getWidth().convertToPixels()
        polyline.moveTo((double)0, (double)0)
        polyline.lineTo((double)0, (double)width)
        polyline.moveTo((double)length, (double)0)
        polyline.lineTo((double)length, (double)width)
        return polyline
    }

    @Override
    protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
        if (polarized) {
            // Draw + sign.
            graphicsContext.setColor(getBorderColor())
            int plusSize = getClosestOdd(getWidth().convertToPixels() / 4)
            int x = -plusSize
            int y = plusSize
            graphicsContext.drawLine(x - plusSize / 2, y, x + plusSize / 2, y)
            graphicsContext.drawLine(x, y - plusSize / 2, x, y + plusSize / 2)
        }
    }
}
