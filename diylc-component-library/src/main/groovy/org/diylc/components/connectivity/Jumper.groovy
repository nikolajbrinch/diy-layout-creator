package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Shape

import org.diylc.components.AbstractLeadedComponent
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentCreationMethod;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.CreationMethod
import org.diylc.core.Display;
import org.diylc.core.components.IDIYComponent
import org.diylc.core.ObjectCache;
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size

@ComponentBomPolicy(BomPolicy.NEVER_SHOW)
@ComponentCreationMethod(CreationMethod.POINT_BY_POINT)
@ComponentDescriptor(name = "Jumper", author = "Branislav Stojkovic", category = "Connectivity", instanceNamePrefix = "J", description = "")
public class Jumper extends AbstractLeadedComponent {

    public static final String id = "3ea8ad8f-fc99-4712-9569-9b028c5cc6a5"
    
	private static final long serialVersionUID = 1L

	private static Color COLOR = Color.blue
	
	@Deprecated
	private Color color
	
	public Jumper() {
		super()
		this.leadColor = COLOR
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(3))
		graphicsContext.setColor(COLOR.darker())
		graphicsContext.drawLine(1, height - 2, width - 2, 1)
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.setColor(COLOR)
		graphicsContext.drawLine(1, height - 2, width - 2, 1)
	}

	@Override
	public Color getLeadColorForPainting(ComponentState componentState) {
		return componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : getLeadColor()
	}
	
	@Override
	@EditableProperty(name = "Color")
	public Color getLeadColor() {
		if (color != null) {
			this.leadColor = color
		}
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
