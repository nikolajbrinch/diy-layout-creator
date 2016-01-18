package org.diylc.app.menus.components;

import java.util.EnumSet;

import javax.swing.SwingConstants;

import org.diylc.app.BadPositionException;
import org.diylc.app.EventType;
import org.diylc.app.IPlugIn;
import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.StatusBar;
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
