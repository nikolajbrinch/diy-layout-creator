package org.diylc.components.shapes

import org.diylc.components.Colors

import java.awt.AlphaComposite
import java.awt.Composite

import org.diylc.components.AbstractShape
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Project
import org.diylc.core.graphics.GraphicsContext

@ComponentLayer(flexible = true)
@ComponentDescriptor(name = "Ellipse", author = "Branislav Stojkovic", category = "Shapes", instanceNamePrefix = "ELL", description = "Elliptical area")
public class Ellipse extends AbstractShape {

    public static final String id = "328c2d67-51ee-4530-b0cc-41d20d4158e7"
    
    private static final long serialVersionUID = 1L

    @Override
    public void draw(GraphicsContext graphicsContext, ComponentState componentState,
            boolean outlineMode, Project project,
            IDrawingObserver drawingObserver) {
        graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke((int) borderThickness.convertToPixels()))

        if (componentState != ComponentState.DRAGGING) {
            Composite oldComposite = graphicsContext.getComposite()
            if (alpha < Colors.MAX_ALPHA) {
                graphicsContext.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1f * alpha / Colors.MAX_ALPHA))
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
                || componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR
                : borderColor)
        graphicsContext.drawOval(firstPoint.x, firstPoint.y, secondPoint.x - firstPoint.x,
                secondPoint.y - firstPoint.y)
    }

    @Override
    public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
        int factor = 32 / width
        graphicsContext.setColor(Colors.SHAPE_FILL_COLOR)
        graphicsContext.fillOval(2 / factor, 2 / factor, width - 4 / factor, height - 4  / factor)
		graphicsContext.setColor(Colors.SHAPE_BORDER_COLOR)
		graphicsContext.drawOval(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor)
	}
}
