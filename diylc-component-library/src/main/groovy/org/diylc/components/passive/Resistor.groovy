package org.diylc.components.passive

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Shape
import java.awt.geom.Rectangle2D

import org.diylc.common.ObjectCache
import org.diylc.components.AbstractLeadedComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.ResistorColorCode
import org.diylc.core.CreationMethod
import org.diylc.core.IDIYComponent
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.annotations.PositiveMeasureValidator
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Resistance
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Resistor", author = "Branislav Stojkovic", category = "Passive", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "R", description = "Resistor layout symbol", zOrder = IDIYComponent.COMPONENT)
public class Resistor extends AbstractLeadedComponent<Resistance> {

    private static final long serialVersionUID = 1L

    public static Size DEFAULT_WIDTH = new Size(1d / 2, SizeUnit.in)
    public static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in)
    public static Color BODY_COLOR = Color.decode("#82CFFD")
    public static Color BORDER_COLOR = BODY_COLOR.darker()
    public static int BAND_SPACING = 5
    public static int FIRST_BAND = 4

    @Deprecated
    Power power = Power.HALF

    @EditableProperty(validatorClass = PositiveMeasureValidator.class)
    Resistance value = null

    @EditableProperty(name = "Power rating")
    org.diylc.core.measures.Power powerNew = null

    @EditableProperty(name = "Color code")
    ResistorColorCode colorCode = ResistorColorCode._5_BAND

    public Resistor() {
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
        return getValue().toString() + (getPowerNew() == null ? "" : " " + getPowerNew().toString())
    }

    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
        graphicsContext.setColor(Colors.LEAD_COLOR_ICON)
        graphicsContext.drawLine(0, height / 2, width, height / 2)
        graphicsContext.setColor(BODY_COLOR)
        graphicsContext.fillRect(4, height / 2 - 3, width - 8, 6)
        graphicsContext.setColor(Color.red)
        graphicsContext.drawLine(7, height / 2 - 3, 7, height / 2 + 3)
        graphicsContext.setColor(Color.orange)
        graphicsContext.drawLine(11, height / 2 - 3, 11, height / 2 + 3)
        graphicsContext.setColor(Color.black)
        graphicsContext.drawLine(15, height / 2 - 3, 15, height / 2 + 3)
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
        // int width = getWidth().convertToPixels();
        if (colorCode == ResistorColorCode.NONE || outlineMode || value == null) {
            return
        }
        int height = getClosestOdd(getWidth().convertToPixels())
        Color[] bands = value.getColorCode(colorCode)
        int x = FIRST_BAND
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2))
        for (int i = 0; i < bands.length; i++) {
            graphicsContext.setColor(bands[i])
            graphicsContext.drawLine(x, 1, x, height - 1)
            x += BAND_SPACING
        }
    }

    @Override
    protected int getLabelOffset(int bodyWidth, int labelWidth) {
        if (value == null) {
            return 0
        }

        Color[] bands = value.getColorCode(colorCode)
        int bandArea = FIRST_BAND + BAND_SPACING * (bands.length - 1)
        
        // Only offset the label if overlaping with the band area.
        if (labelWidth > bodyWidth - 2 * bandArea) {
            return bandArea / 2
        }

        return 0
    }

    @EditableProperty(name = "Reverse (standing)")
    public boolean getFlipStanding() {
        return super.getFlipStanding()
    }
}
