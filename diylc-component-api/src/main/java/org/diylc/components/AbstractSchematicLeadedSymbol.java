package org.diylc.components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

import org.diylc.common.LabelPosition;
import org.diylc.core.ComponentState;
import org.diylc.core.annotations.EditableProperty;

public abstract class AbstractSchematicLeadedSymbol<T> extends AbstractLeadedComponent<T> {

	private static final long serialVersionUID = 1L;

	protected LabelPosition labelPosition = LabelPosition.ABOVE;

	public AbstractSchematicLeadedSymbol() {
		super();
		// We don't want to fill the body, so use null.
		this.bodyColor = null;
		this.leadColor = Colors.SCHEMATIC_LEAD_COLOR;
		this.borderColor = Colors.SCHEMATIC_COLOR;
	}

	@Override
	public Color getBodyColor() {
		return super.getBodyColor();
	}

	@Override
	protected boolean shouldShadeLeads() {
		return false;
	}

	@Override
	protected int getLeadThickness() {
		return 1;
	}

	@Override
	protected int calculateLabelYCoordinate(Rectangle2D shapeRect, Rectangle2D textRect,
			FontMetrics fontMetrics) {
		if (labelPosition == LabelPosition.ABOVE) {
			return -1;
		} else {
			return (int) (shapeRect.getHeight() + textRect.getHeight() - 1);
		}
	}

	@EditableProperty(name = "Label position")
	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	@Override
	@EditableProperty(name = "Color")
	public Color getBorderColor() {
		return super.getBorderColor();
	}

	public void setLabelPosition(LabelPosition labelPosition) {
		this.labelPosition = labelPosition;
	}

	@Override
	protected Color getLeadColorForPainting(ComponentState componentState) {
		return componentState == ComponentState.SELECTED
				|| componentState == ComponentState.DRAGGING ? Colors.SELECTION_COLOR : getLeadColor();
	}
}
