package org.diylc.components.boards;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;

import org.diylc.components.AbstractBoard;
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
import org.diylc.utils.Constants;

@ComponentDescriptor(name = "Perf Board w/ Pads", category = "Boards", author = "Branislav Stojkovic", zOrder = IDIYComponent.BOARD, instanceNamePrefix = "Board", description = "Perforated board with solder pads", bomPolicy = BomPolicy.SHOW_ONLY_TYPE_NAME, autoEdit = false)
public class PerfBoard extends AbstractBoard {

	private static final long serialVersionUID = 1L;

	public static Color COPPER_COLOR = Color.decode("#DA8A67");

	public static Size SPACING = new Size(0.1d, SizeUnit.in);
	public static Size PAD_SIZE = new Size(0.08d, SizeUnit.in);
	public static Size HOLE_SIZE = new Size(0.7d, SizeUnit.mm);

	// private Area copperArea;
	protected Size spacing = SPACING;
	protected Color padColor = COPPER_COLOR;

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		Shape clip = graphicsContext.getClip();
		if (checkPointsClipped(clip)
				&& !clip.contains(firstPoint.x, secondPoint.y)
				&& !clip.contains(secondPoint.x, firstPoint.y)) {
			return;
		}
		super.draw(graphicsContext, componentState, outlineMode, project, drawingObserver);
		if (componentState != ComponentState.DRAGGING) {
			if (alpha < MAX_ALPHA) {
				graphicsContext.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 1f * alpha / MAX_ALPHA));
			}
			Point p = new Point(firstPoint);
			int diameter = getClosestOdd((int) PAD_SIZE.convertToPixels());
			int holeDiameter = getClosestOdd((int) HOLE_SIZE.convertToPixels());
			int spacing = (int) this.spacing.convertToPixels();

			while (p.y < secondPoint.y - spacing) {
				p.x = firstPoint.x;
				p.y += spacing;
				while (p.x < secondPoint.x - spacing - diameter) {
					p.x += spacing;
					graphicsContext.setColor(padColor);
					graphicsContext.fillOval(p.x - diameter / 2, p.y - diameter / 2,
							diameter, diameter);
					graphicsContext.setColor(padColor.darker());
					graphicsContext.drawOval(p.x - diameter / 2, p.y - diameter / 2,
							diameter, diameter);
					graphicsContext.setColor(Constants.CANVAS_COLOR);
					graphicsContext.fillOval(p.x - holeDiameter / 2,
							p.y - holeDiameter / 2, holeDiameter, holeDiameter);
					graphicsContext.setColor(padColor.darker());
					graphicsContext.drawOval(p.x - holeDiameter / 2,
							p.y - holeDiameter / 2, holeDiameter, holeDiameter);
				}
			}
			super.drawCoordinates(graphicsContext, spacing);
		}
	}

	@EditableProperty(name = "Pad color")
	public Color getPadColor() {
		return padColor;
	}

	public void setPadColor(Color padColor) {
		this.padColor = padColor;
	}

	@EditableProperty
	public Size getSpacing() {
		return spacing;
	}

	public void setSpacing(Size spacing) {
		this.spacing = spacing;
	}

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		int factor = 32 / width;
		graphicsContext.setColor(BOARD_COLOR);
		graphicsContext.fillRect(2 / factor, 2 / factor, width - 4 / factor, height - 4
				/ factor);
		graphicsContext.setColor(BORDER_COLOR);
		graphicsContext.drawRect(2 / factor, 2 / factor, width - 4 / factor, height - 4
				/ factor);
		graphicsContext.setColor(COPPER_COLOR);
		graphicsContext.fillOval(width / 4, width / 4, width / 2, width / 2);
		graphicsContext.setColor(COPPER_COLOR.darker());
		graphicsContext.drawOval(width / 4, width / 4, width / 2, width / 2);
		graphicsContext.setColor(Constants.CANVAS_COLOR);
		graphicsContext.fillOval(width / 2 - 2 / factor, width / 2 - 2 / factor,
				getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor));
		graphicsContext.setColor(COPPER_COLOR.darker());
		graphicsContext.drawOval(width / 2 - 2 / factor, width / 2 - 2 / factor,
				getClosestOdd(5.0 / factor), getClosestOdd(5.0 / factor));
	}
}
