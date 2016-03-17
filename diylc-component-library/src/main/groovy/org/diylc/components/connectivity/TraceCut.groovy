package org.diylc.components.connectivity

import org.diylc.components.Colors

import java.awt.Color
import java.awt.Point

import org.diylc.components.AbstractComponent
import org.diylc.components.boards.VeroBoard;
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.ComponentState
import org.diylc.core.components.annotations.ComponentEditOptions;
import org.diylc.core.components.IDIYComponent
import org.diylc.core.IDrawingObserver
import org.diylc.core.ObjectCache;
import org.diylc.core.Project
import org.diylc.core.components.VisibilityPolicy
import org.diylc.core.components.BomPolicy
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext
import org.diylc.core.measures.Size
import org.diylc.core.measures.SizeUnit

@ComponentEditOptions(stretchable = false)
@ComponentBomPolicy(BomPolicy.NEVER_SHOW)
@ComponentLayer(IDIYComponent.TRACE_CUT)
@ComponentDescriptor(name = "Trace Cut", category = "Connectivity", author = "Branislav Stojkovic", description = "Designates the place where a trace on the vero board needs to be cut", instanceNamePrefix = "Cut")
public class TraceCut extends AbstractComponent {

    public static final String id = "68b2c265-423d-4d1a-b7a7-33f0c8a55067"
    
	private static final long serialVersionUID = 1L

	private static Size SIZE = new Size(0.07d, SizeUnit.in)
	
    private static Size CUT_WIDTH = new Size(0.5d, SizeUnit.mm)
	
    private static Color FILL_COLOR = Color.white
	
    private static Color BORDER_COLOR = Color.red
	
    private static Color SELECTION_COLOR = Color.blue

    @EditableProperty
	Size size = SIZE
    
    @EditableProperty(name = "Fill")
	Color fillColor = FILL_COLOR
    
    @EditableProperty(name = "Border")
	Color borderColor = BORDER_COLOR
    
    @EditableProperty(name = "Board")
	Color boardColor = Colors.PCB_BOARD_COLOR
    
    @EditableProperty(name = "Cut between holes")
	Boolean cutBetweenHoles = true
    
    @EditableProperty(name = "Hole spacing")
	Size holeSpacing = VeroBoard.SPACING

	protected Point point = new Point(0, 0)

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return
		}
		int size = getClosestOdd((int) this.size.convertToPixels())		
		int cutWidth = getClosestOdd((int) CUT_WIDTH.convertToPixels())
		if (getCutBetweenHoles()) {
			int holeSpacing = getClosestOdd(getHoleSpacing().convertToPixels())
			graphicsContext
			.setColor(componentState == ComponentState.SELECTED
					|| componentState == ComponentState.DRAGGING ? SELECTION_COLOR
					: boardColor)
			graphicsContext.fillRect(point.x - holeSpacing / 2 - cutWidth / 2, point.y - size / 2 - 1,
					cutWidth, size + 2)
		} else {
			int dotDiameter = size - 6
			graphicsContext.setColor(fillColor)
			graphicsContext.fillRect(point.x - size / 2, point.y - size / 2, size, size)
			graphicsContext
					.setColor(componentState == ComponentState.SELECTED
							|| componentState == ComponentState.DRAGGING ? SELECTION_COLOR
							: borderColor)
			graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
			graphicsContext.drawRect(point.x - size / 2, point.y - size / 2, size, size)
			graphicsContext.fillOval(point.x - dotDiameter / 2, point.y - dotDiameter / 2,
					dotDiameter, dotDiameter)
		}
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int size = 16
		int dotDiameter = size / 4 * 2
		graphicsContext.setColor(FILL_COLOR)
		graphicsContext.fillRect((width - size) / 2, (height - size) / 2, size, size)
		graphicsContext.setColor(BORDER_COLOR)
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1))
		graphicsContext.drawRect((width - size) / 2, (height - size) / 2, size, size)
		graphicsContext.fillOval((width - dotDiameter) / 2, (height - dotDiameter) / 2,
				dotDiameter, dotDiameter)
	}

	@Override
	public int getControlPointCount() {
		return 1
	}

	@Override
	public Point getControlPoint(int index) {
		return point
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return false
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.NEVER
	}

	@Override
	public void setControlPoint(Point point, int index) {
		this.point.setLocation(point)
	}

	@Deprecated
	@Override
	public String getName() {
		return super.getName()
	}
}
