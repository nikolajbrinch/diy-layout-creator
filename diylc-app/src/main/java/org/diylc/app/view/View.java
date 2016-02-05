package org.diylc.app.view;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu.Separator;

import org.diylc.app.Drawing;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.view.canvas.Canvas;

public interface View {

    public void refreshActions();

    public Canvas getCanvas();

    public void addPluginComponent(JComponent component, int index) throws BadPositionException;

    public double getZoomLevel();

    public void setZoomLevel(double zoomLevel);

    public void block();

    public void unblock();

    public void showMessage(String message, String title, int messageType);

    public DrawingModel getModel();

    public void dispose();

    public JFrame getFrame();

    public DrawingController getController();

    /**
     * Adds a custom menu action into application's main menu. If
     * <code>action</code> is set to null {@link Separator} will be added. If
     * the specified menu does not exist it will be automatically created.
     * 
     * @param action
     *            {@link Action} to inser
     * @param menuName
     *            name of the menu to insert into
     */
    public void addMenuAction(Action action, String menuName);

    /**
     * Adds a custom submenu into application's main menu. If
     * <code>action</code> is set to null {@link Separator} will be added. If
     * the specified menu does not exist it will be automatically created.
     * 
     * @param name
     * @param icon
     * @param parentMenuName
     */
    public void addSubmenu(String name, Icon icon, String parentMenuName);

    /**
     * Removes an action from a menu
     * 
     * @param action
     * @param menuName
     */
    public void removeMenuAction(Action action, String menuName);

    /**
     * Clear a menus items
     * 
     * @param menuName
     */
    public void clearMenuItems(String menuName);

    /**
     * Get the drawing this view is part of
     * 
     * @return
     */
    public Drawing getDrawing();
}
