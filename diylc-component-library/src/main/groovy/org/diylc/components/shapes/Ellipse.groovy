package org.diylc.components.shapes

import java.awt.AlphaComposite
import java.awt.Composite
import java.awt.Graphics2D

import org.diylc.common.ObjectCache
import org.diylc.components.AbstractShape
import org.diylc.components.ComponentDescriptor
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy
import org.diylc.core.graphics.GraphicsContext

@ComponentDescriptor(name = "Ellipse", author = "Branislav Stojkovic", category = "Shapes", instanceNamePrefix = "ELL", description = "Elliptical area", zOrder = IDIYComponent.COMPONENT, flexibleZOrder = true, bomPolicy = BomPolicy.SHOW_ALL_NAMES, autoEdit = false)
public class Ellipse extends AbstractShape {

    private static final long serialVersionUID = 1L

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke((int) borderThickness.convertToPixels()))

        if (componentState != ComponentState.DRAGGING) {
            Composite oldComposite = graphicsContext.getComposite()
            if (alpha < MAX_ALPHA) {
                graphicsContext.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1f * alpha / MAX_ALPHA))
            }
            graphicsContext.setColor(color)
            graphicsContext.fillOval(firstPoint.x, firstPoint.y, secondPoint.x
                    - firstPoint.x, secondPoint.y - firstPoint.y)
            graphicsContext.setComposite(oldComposite)
        }
        
        /* 
         * Do not track any changes that follow because the whole oval has been
         * tracked so far.
         */
        drawingObserver.stopTracking()
        graphicsContext.setColor(componentState == ComponentState.SELECTED
                || componentState == ComponentState.DRAGGING ? SELECTION_COLOR
                : borderColor)
        graphicsContext.drawOval(firstPoint.x, firstPoint.y, secondPoint.x - firstPoint.x,
                secondPoint.y - firstPoint.y)
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width
        graphicsContext.setColor(COLOR)
        graphicsContext.fillOval(2 / factor, 2 / factor, width - 4 / factor, height - 4
                / factor)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.drawOval(2 / factor, 2 / factor, width - 4 / factor, height - 4
        				/ factor)
	}
}
