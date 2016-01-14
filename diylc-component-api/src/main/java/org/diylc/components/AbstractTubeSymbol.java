package org.diylc.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;

import org.diylc.common.Display;
import org.diylc.common.HorizontalAlignment;
import org.diylc.common.ObjectCache;
import org.diylc.common.VerticalAlignment;
import org.diylc.core.ComponentState;
import org.diylc.core.IDrawingObserver;
import org.diylc.core.Project;
import org.diylc.core.Theme;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.config.Configuration;
import org.diylc.core.graphics.GraphicsContext;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

public abstract class AbstractTubeSymbol extends AbstractComponent<String> {

	private static final long serialVersionUID = 1L;

	public static Size PIN_SPACING = new Size(0.1d, SizeUnit.in);
	protected String value = "";

	protected Color color = Colors.TUBE_COLOR;
	protected Display display = Display.NAME;
	transient protected Shape[] body;
	protected boolean showHeaters;

	@Override
	public void draw(GraphicsContext graphicsContext, ComponentState componentState,
					 boolean outlineMode, Project project,
					 IDrawingObserver drawingObserver) {
		if (checkPointsClipped(graphicsContext.getClip())) {
			return;
		}
		Color finalColor;
		if (componentState == ComponentState.SELECTED
				|| componentState == ComponentState.DRAGGING) {
			finalColor = Colors.SELECTION_COLOR;
		} else if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme();
			finalColor = theme.getOutlineColor();
		} else {
			finalColor = color;
		}
		graphicsContext.setColor(finalColor);

		// Draw tube

		Shape[] body = getBody();

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(2));
		graphicsContext.draw(body[0]);

		graphicsContext.setStroke(ObjectCache.getInstance().fetchBasicStroke(1));
		graphicsContext.draw(body[1]);

		if (body[2] != null) {
			graphicsContext.draw(body[2]);
		}

		// Draw label
		graphicsContext.setFont(LABEL_FONT);
		Color finalLabelColor;
		if (outlineMode) {
			Theme theme = Configuration.INSTANCE.getTheme();
			finalLabelColor = componentState == ComponentState.SELECTED
					|| componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
					: theme.getOutlineColor();
		} else {
			finalLabelColor = componentState == ComponentState.SELECTED
					|| componentState == ComponentState.DRAGGING ? Colors.LABEL_COLOR_SELECTED
					: Colors.LABEL_COLOR;
		}
		graphicsContext.setColor(finalLabelColor);
		Point p = getTextLocation();
		drawCenteredText(graphicsContext,
				display == Display.VALUE ? getValue() : getName(), new Point(p.x, p.y),
				HorizontalAlignment.LEFT, VerticalAlignment.TOP);
	}

	@EditableProperty
	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
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

	@EditableProperty(name = "Heaters")
	public boolean getShowHeaters() {
		return showHeaters;
	}

	public void setShowHeaters(boolean showHeaters) {
		this.showHeaters = showHeaters;
		// Invalidate body
		body = null;
	}

	/**
	 * Returns transistor shape consisting of 3 parts, in this order:
	 * electrodes, connectors, bulb.
	 * 
	 * @return
	 */
	protected abstract Shape[] getBody();
	
	protected abstract Point getTextLocation();
}
