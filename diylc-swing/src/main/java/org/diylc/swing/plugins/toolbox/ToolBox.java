package org.diylc.swing.plugins.toolbox;

import java.util.EnumSet;

import javax.swing.SwingConstants;

import org.diylc.common.BadPositionException;
import org.diylc.presenter.plugin.EventType;
import org.diylc.presenter.plugin.IPlugIn;
import org.diylc.presenter.plugin.IPlugInPort;
import org.diylc.swing.ISwingUI;
import org.diylc.swing.plugins.statusbar.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ToolBox implements IPlugIn {
	
	private static final Logger LOG = LoggerFactory.getLogger(StatusBar.class);
	
	private ISwingUI swingUI;
	private IPlugInPort plugInPort;
	
	private ComponentTabbedPane componentTabbedPane;

	public ToolBox(ISwingUI swingUI) {
		this.swingUI = swingUI;
	}

	@Override
	public void connect(IPlugInPort plugInPort) {
		this.plugInPort = plugInPort;
		try {
			swingUI.injectGUIComponent(getComponentTabbedPane(), SwingConstants.TOP);
		} catch (BadPositionException e) {
			LOG.error("Could not install the toolbox", e);
		}
	}
	
	public ComponentTabbedPane getComponentTabbedPane() {
		if (componentTabbedPane == null) {
			componentTabbedPane = new ComponentTabbedPane(plugInPort);
		}
		return componentTabbedPane;
	}

	@Override
	public EnumSet<EventType> getSubscribedEventTypes() {
		return null;
	}

	@Override
	public void processMessage(EventType eventType, Object... params) {
	}
}
