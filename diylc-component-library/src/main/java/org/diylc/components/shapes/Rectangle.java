package org.diylc.components.shapes;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;

import org.diylc.common.ObjectCache;
import org.diylc.components.AbstractShape;
import org.diylc.components.ComponentDescriptor;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.Project;
import org.diylc.core.annotations.BomPolicy;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

@ComponentDescriptor(name = "Rectangle", author = "Branislav Stojkovic", category = "Shapes", instanceNamePrefix = "RECT", description = "Ractangular area, with or withouth rounded edges", zOrder = IDIYComponent.COMPONENT, flexibleZOrder = true, bomPolicy = BomPolicy.SHOW_ALL_NAMES, autoEdit = false)
public class Rectangle extends AbstractShape {

	private static final long serialVersionUID = 1L;

	protected Size edgeRadius = new Size(0d, SizeUnit.mm);

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke((int) borderThickness.convertToPixels()));
		int radius = (int) edgeRadius.convertToPixels();
		if (componentState != ComponentState.DRAGGING) {
			Composite oldComposite = graphicsContext.getComposite();
			if (alpha < MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f * alpha / MAX_ALPHA));
			}
			graphicsContext.setColor(color);
			graphicsContext.fillRoundRect(firstPoint.x, firstPoint.y, secondPoint.x
					- firstPoint.x, secondPoint.y - firstPoint.y, radius,
					radius);
			graphicsContext.setComposite(oldComposite);
		}
		// Do not track any changes that follow because the whole rect has been
		// tracked so far.
		drawingObserver.stopTracking();
		graphicsContext.setColor(componentState == ComponentState.SELECTED
				|| componentState == ComponentState.DRAGGING ? SELECTION_COLOR
				: borderColor);
		graphicsContext.drawRoundRect(firstPoint.x, firstPoint.y, secondPoint.x
				- firstPoint.x, secondPoint.y - firstPoint.y, radius, radius);
	}

	@EditableProperty(name = "Radius")
	public Size getEdgeRadius() {
		return edgeRadius;
	}

	public void setEdgeRadius(Size edgeRadius) {
		this.edgeRadius = edgeRadius;
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int factor = 32 / width;
		graphicsContext.setColor(COLOR);
		graphicsContext.fillRect(2 / factor, 2 / factor, width - 4 / factor, height - 4
				/ factor);
		graphicsContext.setColor(BORDER_COLOR);
		graphicsContext.drawRect(2 / factor, 2 / factor, width - 4 / factor, height - 4
				/ factor);
	}
}
