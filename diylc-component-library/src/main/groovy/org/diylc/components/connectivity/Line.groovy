package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Shape

import org.diylc.components.AbstractLeadedComponent
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.Display;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.components.ComponentState;
import org.diylc.core.components.CreationMethod;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size

@ComponentDescriptor(name = "Line", author = "Branislav Stojkovic", category = "Shapes", creationMethod = CreationMethod.POINT_BY_POINT, instanceNamePrefix = "LN", description = "", zOrder = IDIYComponent.COMPONENT, bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false)
public class Line extends AbstractLeadedComponent {

    public static final String id = "eed5c764-569b-494a-b16c-f0766142886e"
    
	private static final long serialVersionUID = 1L

	private static Color COLOR = Color.black

    @EditableProperty
	Color color = COLOR

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.setColor(COLOR)
		graphicsContext.drawLine(1, height - 2, width - 2, 1)
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.setColor(color)
		graphicsContext.drawLine(getControlPoint(0).x, getControlPoint(0).y, getControlPoint(1).x,
				getControlPoint(1).y)
	}

	@Override
	public Color getLeadColorForPainting(ComponentState componentState) {
		return componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : color
	}

	@Override
	public Color getLeadColor() {
		return super.getLeadColor()
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
	
	@Deprecated
	@Override
	public Color getLabelColor() {
		return super.getLabelColor()
	}

	@Deprecated
	@Override
	public String getName() {
		return super.getName()
	}

	@Deprecated
	@Override
	public Display getDisplay() {
		return super.getDisplay()
	}
}
