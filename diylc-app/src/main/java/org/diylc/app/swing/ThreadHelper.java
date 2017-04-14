package org.diylc.app.swing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class ThreadHelper {

  public static void invokeAndWait(final Runnable doRun)
      throws InterruptedException, InvocationTargetException {
    SwingUtilities.invokeAndWait(doRun);

  }

}
