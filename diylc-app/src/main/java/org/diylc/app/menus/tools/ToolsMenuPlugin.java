package org.diylc.app.menus.tools;

import java.util.EnumSet;

import org.diylc.app.AppIconLoader;
import org.diylc.app.EventType;
import org.diylc.app.IPlugIn;
import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.menus.file.FileActionFactory;

/**
 * Entry point class for File management utilities.
 * 
 * @author Branislav Stojkovic
 */
public class ToolsMenuPlugin implements IPlugIn {

	private static final String TOOLS_TITLE = "Tools";
	private static final String TRACE_MASK_TITLE = "Trace Mask";

	private TraceMaskDrawingProvider traceMaskDrawingProvider;

	private ISwingUI swingUI;

	public ToolsMenuPlugin(ISwingUI swingUI) {
		super();
		this.swingUI = swingUI;
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		this.traceMaskDrawingProvider = new TraceMaskDrawingProvider(plugInPort);

		FileActionFactory actionFactory = FileActionFactory.INSTANCE;
		swingUI.injectSubmenu(TRACE_MASK_TITLE, AppIconLoader.TraceMask.getIcon(), TOOLS_TITLE);
		swingUI.injectMenuAction(actionFactory.createExportPDFAction(traceMaskDrawingProvider, swingUI), TRACE_MASK_TITLE);
		swingUI.injectMenuAction(actionFactory.createExportPNGAction(traceMaskDrawingProvider, swingUI), TRACE_MASK_TITLE); 
		swingUI.injectMenuAction(actionFactory.createPrintAction(traceMaskDrawingProvider), TRACE_MASK_TITLE);
		swingUI.injectMenuAction(actionFactory.createBomAction(plugInPort), TOOLS_TITLE);
	}

	@Override
	public EnumSet<EventType> getSubscribedEventTypes() {
		return null;
	}

	@Override
	public void processMessage(EventType eventType, Object... params) {
	}
	
}
