package org.diylc.components.shapes

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Composite
import java.awt.Graphics2D

import org.diylc.components.AbstractShape
import org.diylc.components.Geometry;
import org.diylc.core.ComponentDescriptor;
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.annotations.EditableProperty
import org.diylc.core.components.ComponentState;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentDescriptor(name = "Rectangle", author = "Branislav Stojkovic", category = "Shapes", instanceNamePrefix = "RECT", description = "Ractangular area, with or withouth rounded edges", zOrder = IDIYComponent.COMPONENT, flexibleZOrder = true, bomPolicy = BomPolicy.SHOW_ALL_NAMES, autoEdit = false)
public class Rectangle extends AbstractShape implements Geometry {

    public static final String id = "dbf3323b-d17b-4561-bde1-c654ec615a36"
    
	private static final long serialVersionUID = 1L

    @EditableProperty(name = "Radius")
	Size edgeRadius = new Size(0d, SizeUnit.mm)

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke((int) borderThickness.convertToPixels()))
		int radius = (int) edgeRadius.convertToPixels()
		if (componentState != ComponentState.DRAGGING) {
			Composite oldComposite = graphicsContext.getComposite()
			if (alpha < Colors.MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, toFloat(alpha / Colors.MAX_ALPHA)))
			}
			graphicsContext.setColor(color)
			graphicsContext.fillRoundRect(firstPoint.x, firstPoint.y, secondPoint.x
					- firstPoint.x, secondPoint.y - firstPoint.y, radius,
					radius)
			graphicsContext.setComposite(oldComposite)
		}
		// Do not track any changes that follow because the whole rect has been
		// tracked so far.
		drawingObserver.stopTracking()
		graphicsContext.setColor(componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : borderColor)
		graphicsContext.drawRoundRect(firstPoint.x, firstPoint.y, secondPoint.x
				- firstPoint.x, secondPoint.y - firstPoint.y, radius, radius)
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int factor = 32 / width
		graphicsContext.setColor(Colors.SHAPE_FILL_COLOR)
		graphicsContext.fillRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor)
		graphicsContext.setColor(Colors.SHAPE_BORDER_COLOR)
		graphicsContext.drawRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor)
	}
}
