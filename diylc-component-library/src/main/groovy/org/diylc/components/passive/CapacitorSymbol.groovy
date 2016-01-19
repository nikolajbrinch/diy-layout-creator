package org.diylc.components.passive

import org.diylc.components.Colors

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
public class CapacitorSymbol extends AbstractSchematicLeadedSymbol {

    private static final long serialVersionUID = 1L

    public static Size DEFAULT_LENGTH = new Size(0.05, SizeUnit.in)
    public static Size DEFAULT_WIDTH = new Size(0.15, SizeUnit.in)

    @EditableProperty(validatorClass = PositiveMeasureValidator.class)
    Capacitance value = null

    @Deprecated
    Voltage voltage = Voltage._63V

    @EditableProperty(name = "Voltage")
    Voltage voltageNew = null

    @EditableProperty
    boolean polarized = false

    @Override
    public String getValueForDisplay() {
        return getValue().toString() + (getVoltageNew() == null ? "" : " " + getVoltageNew().toString())
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.with {
            rotate(-Math.PI / 4, width / 2, height / 2)
            setColor(Colors.SCHEMATIC_LEAD_COLOR)
            drawLine(0, height / 2, 13, height / 2)
            drawLine(width - 13, height / 2, width, height / 2)
            setColor(Colors.SCHEMATIC_COLOR)
            drawLine(14, height / 2 - 6, 14, height / 2 + 6)
            drawLine(width - 14, height / 2 - 6, width - 14, height / 2 + 6)
        }
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

        polyline.with {
            moveTo((double)0, (double)0)
            lineTo((double)0, (double)width)
            moveTo((double)length, (double)0)
            lineTo((double)length, (double)width)
        }

        return polyline
    }

    @Override
    protected void decorateComponentBody(GraphicsContext graphicsContext, boolean outlineMode) {
        if (polarized) {
            graphicsContext.with {
                setColor(getBorderColor())
                int plusSize = getClosestOdd(getWidth().convertToPixels() / 4)
                int x = -plusSize
                int y = plusSize
                drawLine(x - plusSize / 2, y, x + plusSize / 2, y)
                drawLine(x, y - plusSize / 2, x, y + plusSize / 2)
            }
        }
    }
}
