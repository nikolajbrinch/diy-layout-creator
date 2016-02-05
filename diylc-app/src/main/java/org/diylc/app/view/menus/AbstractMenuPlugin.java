package org.diylc.app.view.menus;

import javax.swing.Action;
import javax.swing.Icon;

import org.diylc.app.controllers.Controller;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.View;

public abstract class AbstractMenuPlugin<T extends Controller> extends AbstractPlugin<T> {

    public AbstractMenuPlugin(T controller, View view, DrawingModel model) {
        super(controller, view, model);
    }

    public void addMenuAction(Action action, String menuName) {
        getView().addMenuAction(action, menuName);
    }

    public void addSubmenu(String submenuName, Icon icon, String menuName) {
        getView().addSubmenu(submenuName, icon, menuName);
    }

    public void addMenuSeparator(String menuName) {
        getView().addMenuAction(null, menuName);
    }

}
