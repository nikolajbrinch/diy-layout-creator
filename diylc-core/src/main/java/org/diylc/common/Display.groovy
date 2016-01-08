package org.diylc.common

enum Display {

	NAME, VALUE;

	@Override
	public String toString() {
		return "${name().substring(0, 1)}${name().substring(1).toLowerCase()}";
	}
}
