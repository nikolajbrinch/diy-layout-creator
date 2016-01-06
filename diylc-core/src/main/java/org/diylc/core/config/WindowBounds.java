package org.diylc.core.config;

import java.awt.Dimension;
import java.awt.Point;

public class WindowBounds {

	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final int extendedState;

	public WindowBounds(Point location, Dimension size, int extendedState) {
		this(location.x, location.y, size.width, size.height, extendedState);
	}

	public WindowBounds(int x, int y, int width, int height, int extendedState) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.extendedState = extendedState;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getExtendedState() {
		return extendedState;
	}

}
