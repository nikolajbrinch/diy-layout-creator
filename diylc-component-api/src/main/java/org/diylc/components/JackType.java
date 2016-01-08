package org.diylc.components;

public enum JackType {

	MONO, STEREO;

	@Override
	public String toString() {
		return name().substring(0, 1) + name().substring(1).toLowerCase();
	}
}
