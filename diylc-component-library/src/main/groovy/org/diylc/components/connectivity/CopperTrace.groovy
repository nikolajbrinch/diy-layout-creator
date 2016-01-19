package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Shape

import org.diylc.components.AbstractLeadedComponent
import org.diylc.components.ComponentDescriptor
import org.diylc.components.PCBLayer
import org.diylc.core.ComponentState
import org.diylc.core.CreationMethod
import org.diylc.core.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.VisibilityPolicy
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Copper Trace", author = "Branislav Stojkovic", category = "Connectivity", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "Trace", description = "Straight copper trace", zOrder = IDIYComponent.TRACE, bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false)
public class CopperTrace extends AbstractLeadedComponent {

	private static final long serialVersionUID = 1L

	public static Size THICKNESS = new Size(1d, SizeUnit.mm)
	public static Color COLOR = Color.black

    @EditableProperty(name = "Width")
	Size thickness = THICKNESS
    
    @EditableProperty
	private PCBLayer layer = PCBLayer._1
	
	public CopperTrace() {
		super()
		this.leadColor = COLOR
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(3))
		graphicsContext.setColor(COLOR)
		graphicsContext.drawLine(1, height - 2, width - 2, 1)
	}

	@Override
	protected Color getLeadColorForPainting(ComponentState componentState) {
		return componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : getLeadColor()
	}
	
	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.WHEN_SELECTED
	}

	@Override
	@EditableProperty(name = "Color")
	public Color getLeadColor() {
		return this.@leadColor
	}
	
	@Override
	protected int getLeadThickness() {
		return (int) getThickness().convertToPixels()
	}

	@Override
	protected boolean shouldShadeLeads() {
		return false
	}

	public Color getBodyColor() {
		return super.getBodyColor()
	}

	@Override
	public Color getBorderColor() {
		return super.getBorderColor()
	}

	@Override
	public Byte getAlpha() {
		return super.getAlpha()
	}

	@Override
	public Size getLength() {
		return super.getLength()
	}

	@Override
	public Size getWidth() {
		return super.getWidth()
	}

	@Override
	protected Shape getBodyShape() {
		return null
	}

	@Override
	protected Size getDefaultWidth() {
		return null
	}

	@Override
	protected Size getDefaultLength() {
		return null
	}
}
