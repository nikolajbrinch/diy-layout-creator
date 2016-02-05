package org.diylc.app;

import org.diylc.core.IDIYComponent;

public enum Layer {
	CHASSIS("Chassis", IDIYComponent.CHASSIS), BOARD("Board",
			IDIYComponent.BOARD), TRACE("Trace", IDIYComponent.TRACE), COMPONENT(
			"Component", IDIYComponent.COMPONENT), TEXT("Text",
			IDIYComponent.TEXT);

	String title;
	double zOrder;

	private Layer(String title, double order) {
		this.title = title;
		zOrder = order;
	}

	public String getTitle() {
		return title;
	}

	public double getZOrder() {
		return zOrder;
	}
}