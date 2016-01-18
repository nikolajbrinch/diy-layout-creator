package org.diylc.core;

public enum OrientationHV {

	VERTICAL, HORIZONTAL;

	@Override
	public String toString() {
		return name().substring(0, 1) + name().substring(1).toLowerCase();
	}
}
