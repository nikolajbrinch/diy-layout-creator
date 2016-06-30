package org.diylc.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;

import org.diylc.core.Display;
import org.diylc.core.HorizontalAlignment;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.ObjectCache;
import org.diylc.core.Project;
import org.diylc.core.Theme;
import org.diylc.core.VerticalAlignment;
import org.diylc.core.VisibilityPolicy;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.components.ComponentState;
import org.diylc.core.config.Configuration;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

public abstract class AbstractTransistorSymbol extends AbstractComponent {

	private static final long serialVersionUID = 1L;

	public static Size PIN_SPACING = new Size(0.1d, SizeUnit.in);
	
	protected String value = "";
	
    private Point[] controlPoints = new Point[] { new Point(0, 0), new Point(0, 0),
			new Point(0, 0) };
    
	protected Color color = Colors.TRANSISTOR_COLOR;
	
	protected Display display = Display.NAME;
	
	transient protected Shape[] body;

	public AbstractTransistorSymbol() {
		super();
		updateControlPoints();
	}

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState, boolean outlineMode,
					 Project project, IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return;
		}
		int pinSpacing = (int) PIN_SPACING.convertToPixels();
		Color finalColor;
		if (componentState == ComponentState.SELECTED || componentState == ComponentState.DRAGGING) {
			finalColor = Colors.SELECTION_COLOR;
		} else if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme();
			finalColor = theme.getOutlineColor();
		} else {
			finalColor = color;
		}
		graphicsContext.setColor(finalColor);

		// Draw transistor

		int x = getControlPoints()[0].x;
		int y = getControlPoints()[0].y;

		Shape[] body = getBody();

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2));
		graphicsContext.draw(body[0]);

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1));
		graphicsContext.draw(body[1]);

		graphicsContext.fill(body[2]);

		// Draw label
		graphicsContext.setFont(LABEL_FONT);
		Color finalLabelColor;
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme();
			finalLabelColor = componentState == ComponentState.SELECTED
					|| componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED : theme
					.getOutlineColor();
		} else {
			finalLabelColor = componentState == ComponentState.SELECTED
					|| componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
					: Colors.LABEL_COLOR;
		}
		graphicsContext.setColor(finalLabelColor);
		drawCenteredText(graphicsContext, display == Display.VALUE ? getValue() : getName(),
				new Point(x + pinSpacing * 2, y), HorizontalAlignment.LEFT, VerticalAlignment.CENTER);
	}

	@Override
	public Point getControlPoint(int index) {
		return getControlPoints()[index];
	}

	@Override
	public int getControlPointCount() {
		return getControlPoints().length;
	}

	private void updateControlPoints() {
		int pinSpacing = (int) PIN_SPACING.convertToPixels();
		// Update control points.
		int x = getControlPoints()[0].x;
		int y = getControlPoints()[0].y;

		getControlPoints()[1].x = x + pinSpacing * 2;
		getControlPoints()[1].y = y - pinSpacing * 2;

		getControlPoints()[2].x = x + pinSpacing * 2;
		getControlPoints()[2].y = y + pinSpacing * 2;
	}

	@Override
	public VisibilityPolicy getControlPointVisibilityPolicy(int index) {
		return VisibilityPolicy.WHEN_SELECTED;
	}

	@EditableProperty
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean isControlPointSticky(int index) {
		return true;
	}

	@Override
	public void setControlPoint(Point point, int index) {
		getControlPoints()[index].setLocation(point);
		// Invalidate body
		body = null;
	}

	@EditableProperty
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@EditableProperty
	public Display getDisplay() {
		return display;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	/**
	 * Returns transistor shape consisting of 3 parts, in this order: main body,
	 * connectors, polarity arrow.
	 * 
	 * @return
	 */
	protected abstract Shape[] getBody();

    public Point[] getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(Point[] controlPoints) {
        this.controlPoints = controlPoints;
    }
}
