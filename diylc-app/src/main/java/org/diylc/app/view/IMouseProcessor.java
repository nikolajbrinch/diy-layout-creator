package org.diylc.app.view;

import java.awt.Point;

import org.diylc.app.MouseButton;


public interface IMouseProcessor {

	/**
	 * Notifies the presenter that mouse is clicked.
	 * 
	 * Note: point coordinates are display based, i.e. scaled for zoom factor.
	 * 
	 * @param point
	 * @param mouseButton
	 * @param ctrlDown
	 * @param shiftDown
	 * @param altDown
	 * @param clickCount
	 */
	void mouseClicked(Point point, MouseButton button, boolean ctrlDown, boolean shiftDown, boolean altDown,
			boolean metaDown, int clickCount);

	/**
	 * Notifies the presenter that mouse is moved.
	 * 
	 * Note: point coordinates are display based, i.e. scaled for zoom factor.
	 * 
	 * @param point
	 * @param ctrlDown
	 * @param shiftDown
	 * @param altDown
	 * @param metaDown
	 */
	void mouseMoved(Point point, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean metaDown);

	/**
	 * Notification that drag has been started from the specified point.
	 * 
	 * Note: point coordinates are scaled for zoom factor.
	 * 
	 * @param point
	 * @param dragAction
	 */
	void dragStarted(Point point, int dragAction);

	/**
	 * Checks if it's possible to drop over the specified point.
	 * 
	 * Note: point coordinates are scaled for zoom factor.
	 * 
	 * @param point
	 * @return
	 */
	boolean dragOver(Point point);

	/**
	 * Changes the current drag action during the dragging.
	 * 
	 * @param dragAction
	 */
	void dragActionChanged(int dragAction);

	/**
	 * Notification that drag has been ended in the specified point.
	 * 
	 * Note: point coordinates are scaled for zoom factor.
	 * 
	 * @param point
	 */
	void dragEnded(Point point);
}
