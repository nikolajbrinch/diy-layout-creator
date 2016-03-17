package org.diylc.components.boards

import org.diylc.core.graphics.GraphicsContext

import java.awt.Shape

import org.diylc.components.AbstractBoard
import org.diylc.components.Colors
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.components.BomPolicy


@ComponentBomPolicy(BomPolicy.SHOW_ONLY_TYPE_NAME)
@ComponentLayer(IDIYComponent.BOARD)
@ComponentDescriptor(name = "Blank Board", category = "Boards", author = "Branislav Stojkovic", instanceNamePrefix = "Board", description = "Blank circuit board")
class BlankBoard extends AbstractBoard {

    public static final String id = "fa60e2a4-5f68-4f82-b9ff-604e7843c502"
    
	private static final long serialVersionUID = 1L
    
	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int factor = 32 / width
        graphicsContext.drawFilledRect(2 / factor, 2 / factor, width - 4 / factor, height - 4 / factor, Colors.PCB_BORDER_COLOR, Colors.PCB_BOARD_COLOR)
	}
	
	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode, Project project, IDrawingObserver drawingObserver) {
        Shape clip = graphicsContext.getClip()
		
        if (checkPointsClipped(clip) && !clip.contains(firstPoint.x, secondPoint.y) && !clip.contains(secondPoint.x, firstPoint.y)) {
			return
		}
                
		super.draw(graphicsContext, componentState, outlineMode, project, drawingObserver)
	}
	
	@Override
	public boolean getDrawCoordinates() {
		/* 
		 * Override to prevent editing.
		 */
		return super.getDrawCoordinates()
	}
}
