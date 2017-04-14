package org.diylc.app.swing;

import javax.swing.JOptionPane;

public class ScriptWatch {
  
  private static final String SCRIPT_RUN = "org.diylc.scriptRun";

  public static void warnScriptRun() {
    String val = System.getProperty(SCRIPT_RUN);
    if (!"true".equals(val)) {
      int response = JOptionPane.showConfirmDialog(null,
          "It is not recommended to run DIYLC by clicking on the diylc.jar file.\n"
              + "Please use diylc.exe on Windows or run.sh on OSX/Linux to ensure the best\n"
              + "performance and reliability. Do you want to continue?",
          "DIYLC", JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);
      if (response != JOptionPane.YES_OPTION) {
        System.exit(0);
      }
    }
  }
}
