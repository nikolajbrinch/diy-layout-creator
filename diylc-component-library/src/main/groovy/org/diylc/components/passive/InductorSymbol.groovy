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
import org.diylc.core.components.properties.PositiveMeasureValidator
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Current
import org.diylc.core.measures.Inductance
import org.diylc.core.measures.Resistance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentAutoEdit
@ComponentPads(false)
@ComponentCreationMethod(CreationMethod.POINT_BY_POINT)
@ComponentDescriptor(name = "Inductor (schematic symbol)", author = "Branislav Stojkovic", category = "Schematics", instanceNamePrefix = "L", description = "Inductor schematic symbol")
public class InductorSymbol extends AbstractSchematicLeadedSymbol {

    public static final String id = "b0b6ddb2-98b3-48fb-82af-4431836f5be0"
    
    private static final long serialVersionUID = 1L

    private static Size DEFAULT_LENGTH = new Size(0.3, SizeUnit.in)
    
    private static Size DEFAULT_WIDTH = new Size(0.08, SizeUnit.in)

    @EditableProperty(validatorClass = PositiveMeasureValidator.class)
    Inductance value = null
    
    @EditableProperty
    Current current = null
    
    @EditableProperty
    Resistance resistance = null
    
    @EditableProperty
    boolean core = false

    @Override
    public String getValueForDisplay() {
        return value.toString() + (current == null ? "" : " " + current.toString())
    }

    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
        graphicsContext.setColor(Colors.SCHEMATIC_LEAD_COLOR)
        graphicsContext.drawLine(0, height / 2, width / 8, height / 2)
        graphicsContext.drawLine(width * 7 / 8, height / 2, width, height / 2)
        graphicsContext.setColor(Colors.SCHEMATIC_COLOR)

        GeneralPath polyline = new GeneralPath()
        polyline.moveTo(width / 8, height / 2)
        polyline.curveTo(width / 8, height / 4, width * 3 / 8, height / 4,
                width * 3 / 8, height / 2)
        polyline.curveTo(width * 3 / 8, height / 4, width * 5 / 8, height / 4,
                width * 5 / 8, height / 2)
        polyline.curveTo(width * 5 / 8, height / 4, width * 7 / 8, height / 4,
                width * 7 / 8, height / 2)

        polyline.moveTo(width / 8, height * 6 / 10)
        polyline.lineTo(width * 7 / 8, height * 6 / 10)
        polyline.moveTo(width / 8, height * 7 / 10)
        polyline.lineTo(width * 7 / 8, height * 7 / 10)
        graphicsContext.draw(polyline)
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
        double d = length / 10
        polyline.moveTo((double)0, (double)width / 2)
        polyline.curveTo((double)0, (double)0, (double)2 * d, (double)0, (double)2 * d, (double)width / 2)
        polyline.curveTo((double)2 * d, (double)0, (double)4 * d, (double)0, (double)4 * d, (double)width / 2)
        polyline.curveTo((double)4 * d, (double)0, (double)6 * d, (double)0, (double)6 * d, (double)width / 2)
        polyline.curveTo((double)6 * d, (double)0, (double)8 * d, (double)0, (double)8 * d, (double)width / 2)
        polyline.curveTo((double)8 * d, (double)0, (double)10 * d, (double)0, (double)10 * d,(double) width / 2)
        if (core) {
            polyline.moveTo((double)0, (double)width * 3 / 4)
            polyline.lineTo((double)length, (double)width * 3 / 4)
            polyline.moveTo((double)0, (double)width * 7 / 8)
            polyline.lineTo((double)length, (double)width * 7 / 8)
        }
        return polyline
    }

    @Override
    protected boolean useShapeRectAsPosition() {
        return false
    }
}
