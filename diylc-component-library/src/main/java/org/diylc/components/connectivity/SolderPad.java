package org.diylc.components.connectivity;

import java.awt.Color;
import java.awt.Point;

import org.diylc.components.AbstractComponent;
import org.diylc.components.ComponentDescriptor;
import org.diylc.components.PCBLayer;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.Project;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.BomPolicy;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.utils.Constants;

@ComponentDescriptor(name = "Solder Pad", category = "Connectivity", author = "Branislav Stojkovic", description = "Copper solder pad, round or square", instanceNamePrefix = "Pad", stretchable = false, zOrder = IDIYComponent.TRACE + 0.1, bomPolicy = BomPolicy.NEVER_SHOW, autoEdit = false)
public class SolderPad extends AbstractComponent<Void> {

	private static final long serialVersionUID = 1L;

	public static Size SIZE = new Size(0.09d, SizeUnit.in);
	public static Size HOLE_SIZE = new Size(0.8d, SizeUnit.mm);
	public static Color COLOR = Color.black;

	private Size size = SIZE;
	private Color color = COLOR;
	private Point point = new Point(0, 0);
	private Type type = Type.ROUND;
	private Size holeSize = HOLE_SIZE;
	private PCBLayer layer = PCBLayer._1;

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return;
		}
		int diameter = getClosestOdd((int) getSize().convertToPixels());
		int holeDiameter = getClosestOdd((int) getHoleSize().convertToPixels());
		graphicsContext.setColor(componentState == ComponentState.SELECTED
				|| componentState == ComponentState.DRAGGING ? SELECTION_COLOR
				: color);
        
		int x = point.x - diameter / 2;
        int y = point.y - diameter / 2;
        
		if (type == Type.ROUND) {
			graphicsContext.fillOval(x, y, diameter, diameter);
		} else {
			graphicsContext.fillRect(x, y, diameter, diameter);
		}
		
		if (getHoleSize().getValue() > 0) {
			graphicsContext.setColor(Constants.CANVAS_COLOR);
			graphicsContext.fillOval(point.x - (int) holeDiameter / 2, point.y - (int) holeDiameter / 2, holeDiameter, holeDiameter);
		}
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int diameter = getClosestOdd(width / 2);
		int holeDiameter = 5;
		graphicsContext.setColor(COLOR);
		graphicsContext.fillOval((int) (width - diameter) / 2, (int) (height - diameter) / 2, diameter, diameter);
		graphicsContext.setColor(Constants.CANVAS_COLOR);
		graphicsContext.fillOval((int) (width - holeDiameter) / 2, (int) (height - holeDiameter) / 2, holeDiameter, holeDiameter);
	}

	@EditableProperty
	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	@EditableProperty(name = "Hole")
	public Size getHoleSize() {
		if (holeSize == null) {
			holeSize = HOLE_SIZE;
		}
		return holeSize;
	}

	public void setHoleSize(Size holeSize) {
		this.holeSize = holeSize;
	}

	@EditableProperty
	public PCBLayer getLayer() {
		if (layer == null) {
			layer = PCBLayer._1;
		}
		return layer;
	}

	public void setLayer(PCBLayer layer) {
		this.layer = layer;
	}

	@Override
	public String getName() {
		return super.getName();
	}

	@Override
	public int getControlPointCount() {
		return 1;
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return true;
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.NEVER;
	}

	@Override
	public Point getControlPoint(int index) {
		return point;
	}

	@Override
	public void setControlPoint(Point point, int index) {
		this.point.setLocation(point);
	}

	@EditableProperty(name = "Color")
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@EditableProperty
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Void getValue() {
		return null;
	}

	@Override
	public void setValue(Void value) {
	}

	static enum Type {
		ROUND, SQUARE;

		@Override
		public String toString() {
			return name().substring(0, 1) + name().substring(1).toLowerCase();
		}
	}
}
