package org.diylc.components;

import java.awt.Rectangle;

import org.diylc.core.annotations.EditableProperty;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;

/**
 * Base class for radial components. The same as {@link AbstractRadialComponent}
 * but with added pin spacing.
 * 
 * @author bancika
 * 
 * @param <T>
 */
public abstract class AbstractRadialComponent extends
		AbstractLeadedComponent {

	private static final long serialVersionUID = 1L;

	public static Size PIN_SPACING = new Size(0.1d, SizeUnit.in);

    @EditableProperty(name = "Pin spacing")
	private Size pinSpacing = PIN_SPACING;

	@Override
	protected int calculatePinSpacing(Rectangle shapeRect) {
		return getClosestOdd(getPinSpacing().convertToPixels());
	}

	public Size getPinSpacing() {
		if (pinSpacing == null) {
			pinSpacing = PIN_SPACING;
		}
		return pinSpacing;
	}

	public void setPinSpacing(Size pinSpacing) {
		this.pinSpacing = pinSpacing;
	}

}
