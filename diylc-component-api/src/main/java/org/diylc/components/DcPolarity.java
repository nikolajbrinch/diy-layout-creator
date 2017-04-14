package org.diylc.components;

public enum DcPolarity {

	NONE("None"), CENTER_POSITIVE("Center Positive"), CENTER_NEGATIVE("Center Negative");

	private String title;

	private DcPolarity(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return title;
	}
}
