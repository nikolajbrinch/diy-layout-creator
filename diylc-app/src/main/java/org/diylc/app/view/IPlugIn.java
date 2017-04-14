package org.diylc.app.view;

/**
 * Interface for plug-ins.
 * 
 * @author Branislav Stojkovic
 */
public interface IPlugIn {

  /**
   * Method that connects the plug-in with {@link IPlugInPort}. Called by the application when
   * plug-in is installed.
   * 
   * @param plugInPort
   */
  default void connect(IPlugInPort plugInPort) {};
}
