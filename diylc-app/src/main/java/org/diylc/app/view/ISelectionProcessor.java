package org.diylc.app.view;

import org.diylc.app.ExpansionMode;

public interface ISelectionProcessor {

	/**
	 * Expands the current selection to include surrounding components. Options
	 * are controlled with <code>expansionMode</code> flag.
	 * 
	 * @param expansionMode
	 */
	void expandSelection(ExpansionMode expansionMode);

	/**
	 * Selects all components in the project.
	 * 
	 * @param int layer if > 0, designates which layer to select. If <= 0 we
	 *        should select all regardless of layer
	 */
	void selectAll(double layer);

	/**
	 * Rotates selection for 90 degrees.
	 * 
	 * @param direction
	 *            1 for clockwise, -1 for counter-clockwise
	 */
	void rotateSelection(int direction);

}
