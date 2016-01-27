package org.diylc.app.window;

import org.diylc.app.events.IMessageListener;
import org.diylc.app.view.EventType;
import org.diylc.app.view.IPlugInPort;


/**
 * Interface for plug-ins.
 * 
 * @author Branislav Stojkovic
 */
public interface IPlugIn extends IMessageListener<EventType> {

	/**
	 * Method that connects the plug-in with {@link IPlugInPort}. Called by the
	 * application when plug-in is installed.
	 * 
	 * @param plugInPort
	 */
	void connect(IPlugInPort plugInPort);
}
