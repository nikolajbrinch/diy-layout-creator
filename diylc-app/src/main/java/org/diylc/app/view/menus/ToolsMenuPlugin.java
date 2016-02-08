package org.diylc.app.view.menus;

import org.diylc.app.Accelerators;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.ToolsController;
import org.diylc.app.model.Model;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;

/**
 * Entry point class for File management utilities.
 * 
 * @author Branislav Stojkovic
 */
public class ToolsMenuPlugin extends AbstractMenuPlugin<ToolsController> {

	public ToolsMenuPlugin(ToolsController toolsController, View view, Model model) {
	    super(toolsController, view, model);
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		addMenuAction(new GenericAction("Export to PDF", AppIconLoader.PDF.getIcon(), (event) -> getController().exportPdf()), MenuConstants.TOOLS_MENU);
		addMenuAction(new GenericAction("Export to PNG", AppIconLoader.Image.getIcon(), (event) -> getController().exportPng()), MenuConstants.TOOLS_MENU);
        addMenuAction(new GenericAction("Print...", AppIconLoader.Print.getIcon(), Accelerators.PRINT, (event) -> getController().exportPng()), MenuConstants.TOOLS_MENU);
		addMenuAction(new GenericAction("Create B.O.M.", AppIconLoader.BOM.getIcon(), (event) -> getController().createBom()), MenuConstants.TOOLS_MENU);
	}

}
