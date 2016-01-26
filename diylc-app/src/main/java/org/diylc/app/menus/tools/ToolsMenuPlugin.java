package org.diylc.app.menus.tools;

import java.util.EnumSet;

import org.diylc.app.EventType;
import org.diylc.app.IPlugIn;
import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.actions.ExportPDFAction;
import org.diylc.app.actions.ExportPNGAction;
import org.diylc.app.actions.PrintAction;
import org.diylc.app.menus.tools.actions.CreateBomAction;
import org.diylc.app.utils.AppIconLoader;

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

		swingUI.injectSubmenu(TRACE_MASK_TITLE, AppIconLoader.TraceMask.getIcon(), TOOLS_TITLE);
		swingUI.injectMenuAction(new ExportPDFAction(traceMaskDrawingProvider, swingUI), TRACE_MASK_TITLE);
		swingUI.injectMenuAction(new ExportPNGAction(traceMaskDrawingProvider, swingUI), TRACE_MASK_TITLE); 
		swingUI.injectMenuAction(new PrintAction(traceMaskDrawingProvider), TRACE_MASK_TITLE);
		swingUI.injectMenuAction(new CreateBomAction(plugInPort), TOOLS_TITLE);
	}

	@Override
	public EnumSet<EventType> getSubscribedEventTypes() {
		return null;
	}

	@Override
	public void processMessage(EventType eventType, Object... params) {
	}
	
}
