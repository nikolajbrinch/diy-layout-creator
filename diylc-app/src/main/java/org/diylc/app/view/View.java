package org.diylc.app.view;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu.Separator;

import org.diylc.app.Drawing;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.view.canvas.Canvas;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.PropertyWrapper;

public interface View {

    public static final int ERROR_MESSAGE = 0;
    public static final int INFORMATION_MESSAGE = 1;
    public static final int WARNING_MESSAGE = 2;
    public static final int QUESTION_MESSAGE = 3;
    public static final int PLAIN_MESSAGE = -1;
    public static final int DEFAULT_OPTION = -1;
    public static final int YES_NO_OPTION = 0;
    public static final int YES_NO_CANCEL_OPTION = 1;
    public static final int OK_CANCEL_OPTION = 2;

    public static final int YES_OPTION = 0;
    public static final int NO_OPTION = 1;
    public static final int CANCEL_OPTION = 2;
    public static final int OK_OPTION = 0;
    public static final int CLOSED_OPTION = -1;

    public static final String CHECK_BOX_MENU_ITEM = "org.diylc.checkBoxMenuItem";
    public static final String RADIO_BUTTON_GROUP_KEY = "org.diylc.radioButtonGroup";

    int showConfirmDialog(String message, String title, int optionType, int messageType);

    boolean editProperties(List<PropertyWrapper> properties, Set<PropertyWrapper> defaultedProperties);

    Path promptFileSave();

    public void refreshActions();

    public Canvas getCanvas();

    public void addPluginComponent(JComponent component, int index) throws BadPositionException;

    public double getZoomLevel();

    public void setZoomLevel(double zoomLevel);

    public void block();

    public void unblock();

    public void showMessage(String message, String title, int messageType);

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
     * @return 
     */
    public JMenuItem addMenuAction(Action action, String menuName);

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

    public void repaintCanvas();

    public void updateStatusBar();

    public void updateStatusBar(String message);

    public void updateZoomLevel(double zoomLevel);

    public void updateLockedLayers();

    public void updateTitle();

    public void initCanvas(Project project, boolean freshStart);

    public void selectionStateChanged(List<IDIYComponent> selection, Collection<IDIYComponent> stuckComponents);

    public void minimize();

    public void zoom();
}
