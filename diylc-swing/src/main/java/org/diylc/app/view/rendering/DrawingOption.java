package org.diylc.app.view.rendering;

import java.awt.Graphics;

import org.diylc.app.IPlugInPort;

/**
 * Enumerates all options that can be used when drawing a project.
 * 
 * @see IPlugInPort#draw(java.awt.Graphics2D, java.util.Set, IComponentFiler)
 * 
 * @author Branislav Stojkovic
 */
public enum DrawingOption {

	/**
	 * Selection rectangle will be drawn when needed and selected components may
	 * be rendered differently.
	 */
	SELECTION,
	/**
	 * Selected zoom level will be applied to scale the {@link Graphics} before
	 * drawing.
	 */
	ZOOM,
	/**
	 * Grid lines are drawn.
	 */
	GRID,
	/**
	 * Control points are drawn.
	 */
	CONTROL_POINTS,
	/**
	 * Anti-aliasing is used when drawing.
	 */
	ANTIALIASING,
	/**
	 * Draw components in outline mode.
	 */
	OUTLINE_MODE;
}
