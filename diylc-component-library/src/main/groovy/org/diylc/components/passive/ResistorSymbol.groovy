package org.diylc.components.passive

import org.diylc.components.Colors

import java.awt.Shape
import java.awt.geom.GeneralPath

import org.diylc.components.AbstractSchematicLeadedSymbol
import org.diylc.core.components.annotations.ComponentAutoEdit;
import org.diylc.core.components.annotations.ComponentCreationMethod;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentPads;
import org.diylc.core.components.CreationMethod
import org.diylc.core.components.IDIYComponent
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.PowerUnit
import org.diylc.core.measures.Resistance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentAutoEdit
@ComponentPads(false)
@ComponentCreationMethod(CreationMethod.POINT_BY_POINT)
@ComponentDescriptor(name = "Resistor (schematic symbol)", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "R", description = "Resistor schematic symbol")
public class ResistorSymbol extends AbstractSchematicLeadedSymbol {

    public static final String id = "8d9ceeae-f01e-41b1-af46-fb8bbec5a42e"
    
    private static final long serialVersionUID = 1L

    private static Size DEFAULT_LENGTH = new Size(0.3, SizeUnit.in)
    
    private static Size DEFAULT_WIDTH = new Size(0.08, SizeUnit.in)

    @EditableProperty
    Resistance value = null

    @Deprecated
    Power power = Power.HALF
    
    @EditableProperty(name = "Power rating")
    org.diylc.core.measures.Power powerNew = new org.diylc.core.measures.Power(0.5, PowerUnit.W)

    @Override
    public String getValueForDisplay() {
        return getValue().toString() + (getPowerNew() == null ? "" : " " + getPowerNew().toString())
    }

    public org.diylc.core.measures.Power getPowerNew() {
        // Backward compatibility
        if (powerNew == null) {
            powerNew = power.convertToNewFormat()
            // Clear old value, don't need it anymore
            power = null
        }
        return powerNew
    }

    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
        graphicsContext.setColor(Colors.SCHEMATIC_LEAD_COLOR)
        graphicsContext.drawLine(0, height / 2, 4, height / 2)
        graphicsContext.drawLine(width - 4, height / 2, width, height / 2)
        graphicsContext.setColor(Colors.SCHEMATIC_COLOR)
        graphicsContext.drawPolyline([ 4, 6, 10, 14, 18, 22, 26, 28 ] as int[], [ height / 2,
            height / 2 + 2, height / 2 - 2, height / 2 + 2, height / 2 - 2, height / 2 + 2,
            height / 2 - 2, height / 2 ] as int[], 8)
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
        polyline.moveTo(0, width / 2)
        polyline.lineTo(length / 16, width)
        polyline.lineTo(3 * length / 16, 0)
        polyline.lineTo(5 * length / 16, width)
        polyline.lineTo(7 * length / 16, 0)
        polyline.lineTo(9 * length / 16, width)
        polyline.lineTo(11 * length / 16, 0)
        polyline.lineTo(13 * length / 16, width)
        polyline.lineTo(15 * length / 16, 0)
        polyline.lineTo(length, width / 2)
        return polyline
    }
}
