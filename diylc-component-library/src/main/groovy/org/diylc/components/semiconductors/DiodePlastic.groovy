package org.diylc.components.semiconductors

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
import org.diylc.core.Theme
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.config.Configuration
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentAutoEdit
@ComponentCreationMethod(CreationMethod.POINT_BY_POINT)
@ComponentDescriptor(name = "Diode (plastic)", author = "Branislav Stojkovic", category = "Semiconductors", instanceNamePrefix = "D", description = "Plastic diode, like most rectifier, zener, schottky, etc.")
public class DiodePlastic extends AbstractLeadedComponent {

    public static final String id = "3a0eb638-786d-421e-87d1-3711f9f84854"
    
	private static final long serialVersionUID = 1L

	private static Size DEFAULT_WIDTH = new Size(1d / 4, SizeUnit.in)
	
    private static Size DEFAULT_HEIGHT = new Size(1d / 8, SizeUnit.in)
	
    private static Size MARKER_WIDTH = new Size(1d, SizeUnit.mm)
	
    private static Color BODY_COLOR = Color.darkGray
	
    private static Color MARKER_COLOR = Color.decode("#DDDDDD")
	
    private static Color LABEL_COLOR = Color.white
	
    private static Color BORDER_COLOR = BODY_COLOR.darker()

	@EditableProperty
	String value = ""

	@EditableProperty(name = "Marker")
	Color markerColor = MARKER_COLOR

	public DiodePlastic() {
		super()
		this.labelColor = LABEL_COLOR
		this.bodyColor = BODY_COLOR
		this.borderColor = BORDER_COLOR
	}

	@Override
	protected boolean supportsStandingMode() {
		return true
	}

	@Override
	public Color getStandingBodyColor() {
		return getFlipStanding() ? getBodyColor() : getMarkerColor()
	}

	@EditableProperty(name = "Reverse (standing)")
	public boolean getFlipStanding() {
		return super.getFlipStanding()
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.rotate(-Math.PI / 4, width / 2, height / 2)
		graphicsContext.setColor(Colors.LEAD_COLOR_ICON)
		graphicsContext.drawLine(0, height / 2, width, height / 2)
		graphicsContext.setColor(BODY_COLOR)
		graphicsContext.fillRect(6, height / 2 - 3, width - 12, 6)
		graphicsContext.setColor(MARKER_COLOR)
		int markerWidth = 4 * width / 32
		graphicsContext.fillRect(width - 6 - markerWidth, height / 2 - 3, markerWidth, 6)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawRect(6, height / 2 - 3, width - 12, 6)
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
		Color finalMarkerColor
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme()
			finalMarkerColor = theme.getOutlineColor()
		} else {
			finalMarkerColor = markerColor
		}
		graphicsContext.setColor(finalMarkerColor)
		int width = (int) getLength().convertToPixels()
		int markerWidth = (int) MARKER_WIDTH.convertToPixels()
		graphicsContext.fillRect(width - markerWidth, 0, markerWidth,
				getClosestOdd(getWidth().convertToPixels()))
	}

}
