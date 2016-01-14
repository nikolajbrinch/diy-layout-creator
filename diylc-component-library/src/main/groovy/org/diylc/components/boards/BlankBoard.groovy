package org.diylc.components.boards

import org.diylc.core.graphics.GraphicsContext

import java.awt.Shape

import org.diylc.components.AbstractBoard
import org.diylc.components.Colors
import org.diylc.components.ComponentDescriptor
import org.diylc.core.ComponentState
import org.diylc.core.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.Project
import org.diylc.core.annotations.BomPolicy


@ComponentDescriptor(name = "Blank Board", category = "Boards", author = "Branislav Stojkovic", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Blank circuit board", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
class BlankBoard extends AbstractBoard {

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
