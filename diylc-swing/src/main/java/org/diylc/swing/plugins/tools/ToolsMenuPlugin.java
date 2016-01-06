package org.diylc.swing.plugins.tools;

import java.util.EnumSet;

import org.diylc.common.EventType;
import org.diylc.common.IPlugIn;
import org.diylc.common.IPlugInPort;
import org.diylc.images.CoreIconLoader;
import org.diylc.swing.ActionFactory;
import org.diylc.swing.ISwingUI;

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

		ActionFactory actionFactory = ActionFactory.getInstance();
		swingUI.injectSubmenu(TRACE_MASK_TITLE, CoreIconLoader.TraceMask.getIcon(), TOOLS_TITLE);
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
