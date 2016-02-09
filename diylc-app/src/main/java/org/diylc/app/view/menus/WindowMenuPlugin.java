package org.diylc.app.view.menus;

import org.diylc.app.Accelerators;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.WindowController;
import org.diylc.app.model.Model;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

/**
 * Entry point class for File management utilities.
 * 
 * @author Branislav Stojkovic
 */
public class WindowMenuPlugin extends AbstractMenuPlugin<WindowController> {

	public WindowMenuPlugin(WindowController windowController, View view, Model model) {
	    super(windowController, view, model);
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		addMenuAction(new GenericAction("Minimize", Accelerators.MINIMIZE, (event) -> getController().minimize()), MenuConstants.WINDOW_MENU);
		addMenuAction(new GenericAction("Zoom", (event) -> getController().zoom()),MenuConstants.WINDOW_MENU);
        addMenuSeparator(MenuConstants.WINDOW_MENU);
	}

}
