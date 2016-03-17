package org.diylc.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Point;
import java.awt.geom.CubicCurve2D;

import org.diylc.core.components.ComponentState;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.ObjectCache;
import org.diylc.core.Project;
import org.diylc.core.components.VisibilityPolicy;
import org.diylc.core.components.properties.EditableProperty;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.core.utils.Constants;

public abstract class AbstractCurvedComponent extends AbstractTransparentComponent {

	private static final long serialVersionUID = 1L;

	public static Size DEFAULT_SIZE = new Size(1d, SizeUnit.in);

	protected Point[] controlPoints = new Point[] {
			new Point(0, 0),
			new Point((int) (DEFAULT_SIZE.convertToPixels() / 2), 0),
			new Point((int) (DEFAULT_SIZE.convertToPixels() / 2), (int) (DEFAULT_SIZE
					.convertToPixels())),
			new Point((int) DEFAULT_SIZE.convertToPixels(), (int) DEFAULT_SIZE.convertToPixels()) };

	protected Color color = getDefaultColor();
	protected PointCount pointCount = PointCount.FOUR;

	/**
	 * Draws the specified curve onto graphics.
	 * 
	 * @param curve
	 * @param graphicsContext
	 * @param componentState
	 */
	protected abstract void drawCurve(CubicCurve2D curve, GraphicsContext graphicsContext,
			ComponentState componentState);

	/**
	 * @return default color.
	 */
	protected abstract Color getDefaultColor();

	@Override
	public void drawIcon(GraphicsContext graphicsContext, int width, int height) {
		graphicsContext.setColor(getDefaultColor().darker());
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(3));
		CubicCurve2D curve = new CubicCurve2D.Double(1, height - 1, width / 4, height / 3,
				3 * width / 4, 2 * height / 3, width - 1, 1);
		graphicsContext.draw(curve);
		graphicsContext.setColor(getDefaultColor());
		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1));
		graphicsContext.draw(curve);
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
			Project project, IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return;
		}
		if (componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING) {
			// Do not track guidelines.
			drawingObserver.stopTracking();
			graphicsContext.setStroke(Constants.DASHED_STROKE);
			graphicsContext.setColor(Colors.GUIDELINE_COLOR);
			graphicsContext.drawLine(controlPoints[0].x, controlPoints[0].y, controlPoints[1].x,
					controlPoints[1].y);
			graphicsContext.drawLine(controlPoints[1].x, controlPoints[1].y, controlPoints[2].x,
					controlPoints[2].y);
			graphicsContext.drawLine(controlPoints[2].x, controlPoints[2].y, controlPoints[3].x,
					controlPoints[3].y);
			drawingObserver.startTracking();
		}
		CubicCurve2D curve = new CubicCurve2D.Double(controlPoints[0].x, controlPoints[0].y,
				controlPoints[1].x, controlPoints[1].y, controlPoints[2].x, controlPoints[2].y,
				controlPoints[3].x, controlPoints[3].y);

		Composite oldComposite = graphicsContext.getComposite();
		if (alpha < Colors.MAX_ALPHA) {
			graphicsContext.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f * alpha
					/ Colors.MAX_ALPHA));
		}
		// g2d.setColor(color.darker());
		// g2d.setStroke(new BasicStroke(thickness));
		// g2d.draw(path);
		// g2d.setColor(color);
		// g2d.setStroke(new BasicStroke(thickness - 2));
		// g2d.draw(path);
		drawCurve(curve, graphicsContext, componentState);
		graphicsContext.setComposite(oldComposite);
	}

	@EditableProperty(name = "Point Count")
	public PointCount getPointCount() {
		if (pointCount == null) {
			pointCount = PointCount.FOUR;
		}
		return pointCount;
	}

	public void setPointCount(PointCount pointCount) {
		this.pointCount = pointCount;
		// Reset control points.
		for (int i = 0; i < getControlPointCount(); i++) {
			setControlPoint(getControlPoint(i), i);
		}
	}

	@Override
	public int getControlPointCount() {
		switch (getPointCount()) {
		case TWO:
			return 2;
		case THREE:
			return 3;
		case FOUR:
			return 4;
		}
		return 0;
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return index == 0 || index == getControlPointCount() - 1;
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.WHEN_SELECTED;
	}

	@Override
	public boolean canControlPointOverlap(int index) {
		// Only shape control points may overlap.
		switch (getPointCount()) {
		case TWO:
			return false;
		case THREE:
			return index == 1;
		case FOUR:
			return index == 1 || index == 2;
		}
		return false;
	}

	@Override
	public Point getControlPoint(int index) {
		if (index == 0) {
			return controlPoints[0];
		}
		switch (getPointCount()) {
		case TWO:
			switch (index) {
			case 1:
				return controlPoints[3];
			}
		case THREE:
			switch (index) {
			case 1:
				return controlPoints[1];
			case 2:
				return controlPoints[3];
			}
		case FOUR:
			switch (index) {
			case 1:
				return controlPoints[1];
			case 2:
				return controlPoints[2];
			case 3:
				return controlPoints[3];
			}
		}
		return controlPoints[index];
	}

	@Override
	public void setControlPoint(Point point, int index) {
		if (index == 0) {
			controlPoints[0].setLocation(point);
		} else {
			switch (getPointCount()) {
			case TWO:
				switch (index) {
				case 1:
					Point center = new Point((point.x + controlPoints[0].x) / 2,
							(point.y + controlPoints[0].y) / 2);
					controlPoints[1].setLocation(center);
					controlPoints[2].setLocation(center);
					controlPoints[3].setLocation(point);
					break;
				}
				break;
			case THREE:
				switch (index) {
				case 1:
					controlPoints[1].setLocation(point);
					controlPoints[2].setLocation(point);
					break;
				case 2:
					controlPoints[3].setLocation(point);
					break;
				}
				break;
			case FOUR:
				switch (index) {
				case 1:
					controlPoints[1].setLocation(point);
					break;
				case 2:
					controlPoints[2].setLocation(point);
					break;
				case 3:
					controlPoints[3].setLocation(point);
					break;
				}
				break;
			}
		}
	}

	@EditableProperty
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	enum PointCount {
		TWO, THREE, FOUR;

		@Override
		public String toString() {
			return name().substring(0, 1) + name().substring(1).toLowerCase();
		}
	}
}
