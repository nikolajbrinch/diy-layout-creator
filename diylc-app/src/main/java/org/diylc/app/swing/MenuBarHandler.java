package org.diylc.app.swing;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.diylc.app.Accelerators;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.menus.MenuConstants;
import org.diylc.core.platform.Platform;
import org.diylc.core.utils.SystemUtils;

public class MenuBarHandler {

  public void createDefaultMenuBar(ActionListener newProject, ActionListener openProject, ActionListener importProject, ActionListener exit, ActionListener restart) {
    setDefaultMenuBar(newProject, openProject, importProject, exit, restart);
  }

  private void setDefaultMenuBar(ActionListener newProject, ActionListener openProject, ActionListener importProject, ActionListener restart, ActionListener exit) {
    JMenuBar jMenuBar = new JMenuBar();
    JMenu fileMenu = new JMenu(MenuConstants.FILE_MENU);
    jMenuBar.add(fileMenu);
    fileMenu.add(new GenericAction("New", AppIconLoader.DocumentPlainYellow.getIcon(), Accelerators.NEW, newProject));
    fileMenu.add(new GenericAction("Open", AppIconLoader.FolderOut.getIcon(), Accelerators.OPEN, openProject));
    fileMenu.add(new JMenu(MenuConstants.FILE_OPEN_RECENT_MENU));
    fileMenu.addSeparator();
    fileMenu.add(new GenericAction("Import", AppIconLoader.ElementInto.getIcon(), Accelerators.IMPORT, importProject));
    fileMenu.addSeparator();
    fileMenu.add(new GenericAction("Restart", restart));

    if (!SystemUtils.isMac()) {
      fileMenu.addSeparator();
      fileMenu.add(new GenericAction("Exit", AppIconLoader.Exit.getIcon(), exit));
    }

    Platform.getPlatform().setDefaultMenuBar(jMenuBar);
  }


}
