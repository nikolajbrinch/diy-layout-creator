package org.diylc.components;

import org.diylc.core.annotations.EditableProperty;

public abstract class AbstractTransparentComponent extends AbstractComponent {

	private static final long serialVersionUID = 1L;

	protected byte alpha = Colors.MAX_ALPHA;

    @EditableProperty
	public Byte getAlpha() {
		return alpha;
	}

	public void setAlpha(Byte alpha) {
		this.alpha = alpha;
	}
}
