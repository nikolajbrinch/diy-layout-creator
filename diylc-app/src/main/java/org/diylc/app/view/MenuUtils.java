package org.diylc.app.view;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

public class MenuUtils {

  public static MenuElement findMenu(MenuElement menu, String menuName) {
    MenuElement menuElement = null;

    String name = null;

    if (menu instanceof JMenuItem) {
      name = ((JMenuItem) menu).getText();
    }

    if (menuName.equals(name)) {
      menuElement = menu;
    } else {
      MenuElement[] menuElements = menu.getSubElements();
      for (MenuElement element : menuElements) {
        menuElement = findMenu(element, menuName);

        if (menuElement != null) {
          break;
        }
      }
    }

    return menuElement;
  }

  public static Action findAction(MenuElement menu, String actionName) {
    Action action = null;

    if (menu instanceof JPopupMenu || menu instanceof JMenu) {
      MenuElement[] elements = menu.getSubElements();

      for (MenuElement menuElement : elements) {
        action = findAction(menuElement, actionName);
        if (action != null) {
          break;
        }
      }
    } else if (menu instanceof JMenuItem) {
      JMenuItem item = (JMenuItem) menu;
      Action nextAction = item.getAction();

      if (nextAction != null) {
        String value = (String) nextAction.getValue(Action.NAME);
        if (actionName.equals(value)) {
          action = nextAction;
        }
      }
    }

    return action;
  }
}
