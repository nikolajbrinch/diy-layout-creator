package org.diylc.app.view;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Interface for plugin access to the swing front end.
 * 
 * @author Branislav Stojkovic
 */
public interface ISwingUI extends IView, View {

	/**
	 * Injects a custom GUI panels provided by the plug-in and desired position
	 * in the window. Application will layout plug-in panels accordingly. <br>
	 * Valid positions are:
	 * <ul>
	 * <li> {@link SwingConstants#TOP}</li>
	 * <li> {@link SwingConstants#BOTTOM}</li>
	 * <li> {@link SwingConstants#LEFT}</li>
	 * <li> {@link SwingConstants#RIGHT}</li>
	 * </ul>
	 * 
	 * Center position is reserved for the main canvas panel and cannot be used.
	 * 
	 * @param component
	 * @param position
	 * @throws BadPositionException
	 *             in case invalid position is specified
	 */
	void injectGUIComponent(JComponent component, int position) throws BadPositionException;

    void removeGUIComponent(int position) throws BadPositionException;
}
