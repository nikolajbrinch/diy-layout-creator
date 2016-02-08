package org.diylc.app.view.menus;

import org.diylc.app.Accelerators;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.controllers.FileController;
import org.diylc.app.model.Model;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.View;
import org.diylc.core.platform.DefaultQuitResponse;
import org.diylc.core.platform.RestartQuitResponse;
import org.diylc.core.utils.SystemUtils;

/**
 * Entry point class for File management utilities.
 * 
 * @author Branislav Stojkovic
 */
public class FileMenuPlugin extends AbstractMenuPlugin<FileController> {

	private final ApplicationController applicationController;

	public FileMenuPlugin(ApplicationController applicationController, FileController fileController, View view, Model model) {
	    super(fileController, view, model);
        this.applicationController = applicationController;
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		addMenuAction(new GenericAction("New", AppIconLoader.DocumentPlainYellow.getIcon(), Accelerators.NEW, (event) -> getApplicationController().createNewProject()), MenuConstants.FILE_MENU);
		addMenuAction(new GenericAction("Open", AppIconLoader.FolderOut.getIcon(), Accelerators.OPEN, (event) -> getApplicationController().open()), MenuConstants.FILE_MENU);
		addSubmenu(MenuConstants.FILE_OPEN_RECENT_MENU, null, MenuConstants.FILE_MENU);
        addMenuSeparator(MenuConstants.FILE_MENU);
        addMenuAction(new GenericAction("Close", AppIconLoader.FolderOut.getIcon(), Accelerators.CLOSE, (event) -> getController().close()), MenuConstants.FILE_MENU);
        addMenuSeparator(MenuConstants.FILE_MENU);
		addMenuAction(new GenericAction("Save", AppIconLoader.DiskBlue.getIcon(), Accelerators.SAVE, (event) -> getController().save()), MenuConstants.FILE_MENU);
		addMenuAction(new GenericAction("Save As", AppIconLoader.DiskBlue.getIcon(), Accelerators.SAVE_AS, (event) -> getController().saveAs()), MenuConstants.FILE_MENU);
        addMenuSeparator(MenuConstants.FILE_MENU);
        addMenuAction(new GenericAction("Import", AppIconLoader.ElementInto.getIcon(), Accelerators.IMPORT, (event) -> getApplicationController().importProject()), MenuConstants.FILE_MENU);
        addMenuAction(new GenericAction("Export to PDF", AppIconLoader.PDF.getIcon(), (event) -> getController().exportPdf()), MenuConstants.FILE_MENU);
        addMenuAction(new GenericAction("Export to PNG", AppIconLoader.Image.getIcon(), (event) -> getController().exportPng()), MenuConstants.FILE_MENU);
        addMenuSeparator(MenuConstants.FILE_MENU);
		addMenuAction(new GenericAction("Print...", AppIconLoader.Print.getIcon(), Accelerators.PRINT, (event) -> getController().print()), MenuConstants.FILE_MENU);
        addMenuSeparator(MenuConstants.FILE_MENU);
        addMenuAction(new GenericAction("Restart", (event) -> getApplicationController().exit(new RestartQuitResponse())), MenuConstants.FILE_MENU);
		
		if (!SystemUtils.isMac()) {
			addMenuSeparator(MenuConstants.FILE_MENU);
			addMenuAction(new GenericAction("Exit", AppIconLoader.Exit.getIcon(), (event) -> getApplicationController().exit(new DefaultQuitResponse())), MenuConstants.FILE_MENU);
		}
	}

    public ApplicationController getApplicationController() {
        return applicationController;
    }
}
