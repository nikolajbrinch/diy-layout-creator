package org.diylc.app.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.diylc.app.ComponentTransferable;
import org.diylc.app.Drawing;
import org.diylc.app.FileFilterEnum;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.ApplicationController;
import org.diylc.app.controllers.ArrangeMenuController;
import org.diylc.app.controllers.CanvasController;
import org.diylc.app.controllers.ConfigController;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.controllers.EditMenuController;
import org.diylc.app.controllers.FileController;
import org.diylc.app.controllers.HelpController;
import org.diylc.app.controllers.LayersController;
import org.diylc.app.controllers.ToolsController;
import org.diylc.app.controllers.ViewController;
import org.diylc.app.controllers.WindowController;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.canvas.Canvas;
import org.diylc.app.view.canvas.CanvasPlugin;
import org.diylc.app.view.dialogs.ButtonDialog;
import org.diylc.app.view.dialogs.DialogFactory;
import org.diylc.app.view.dialogs.PropertyEditorDialog;
import org.diylc.app.view.menus.ArrangeMenuPlugin;
import org.diylc.app.view.menus.ConfigMenuPlugin;
import org.diylc.app.view.menus.EditMenuPlugin;
import org.diylc.app.view.menus.FileMenuPlugin;
import org.diylc.app.view.menus.HelpMenuPlugin;
import org.diylc.app.view.menus.LayersMenuPlugin;
import org.diylc.app.view.menus.MenuConstants;
import org.diylc.app.view.menus.ToolsMenuPlugin;
import org.diylc.app.view.menus.ViewMenuPlugin;
import org.diylc.app.view.menus.WindowMenuPlugin;
import org.diylc.app.view.properties.PropertyPlugin;
import org.diylc.app.view.toolbox.ToolBox;
import org.diylc.core.EventType;
import org.diylc.core.LRU;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.config.Configuration;
import org.diylc.core.config.WindowBounds;
import org.diylc.core.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawingView extends JFrame implements ISwingUI, View {

    private static final Logger LOG = LoggerFactory.getLogger(DrawingView.class);

    private static final long serialVersionUID = 1L;

    private final ApplicationController applicationController;

    private DrawingController drawingController;

    private DrawingModel model;

    private final Presenter presenter;

    private JPanel centerPanel;

    private JPanel leftPanel;

    public JPanel rightPanel;

    private JPanel topPanel;

    private JPanel bottomPanel;

    private JMenuBar mainMenuBar;

    private Map<String, JMenu> menuMap = new HashMap<String, JMenu>();

    private Map<String, ButtonGroup> buttonGroupMap = new HashMap<String, ButtonGroup>();

    private CanvasPlugin canvasPlugin;

    private PropertyPlugin propertyPlugin;

    private Drawing drawing;

    public DrawingView(ApplicationController applicationController, Drawing drawing) {
        super("DIY Layout Creator");
        this.applicationController = applicationController;
        this.drawing = drawing;

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        WindowBounds windowBounds = Configuration.INSTANCE.getWindowBounds();
        setPreferredSize(new Dimension(windowBounds.getWidth(), windowBounds.getHeight()));
        createBasePanels();
        setIconImages(Arrays.asList(AppIconLoader.IconSmall.getImage(), AppIconLoader.IconMedium.getImage(),
                AppIconLoader.IconLarge.getImage()));
        DialogFactory.getInstance().initialize(this);

        this.presenter = new Presenter(this);
        this.model = new DrawingModel(presenter);

        installPlugins();
        presenter.configure();
        presenter.createNewProject();

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                getController().close();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                getController().close();
            }
        });

        setLocation(new Point(windowBounds.getX(), windowBounds.getY()));
        setExtendedState(windowBounds.getExtendedState());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                JFrame frame = (JFrame) e.getComponent();
                frame.setPreferredSize(frame.getSize());
                storeWindowBounds((JFrame) e.getComponent());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                storeWindowBounds((JFrame) e.getComponent());
            }

        });

        setGlassPane(new CustomGlassPane());
    }

    private void installPlugins() {
        presenter.installPlugin(new ToolBox(this));
        presenter.installPlugin(new FileMenuPlugin(getApplication(), new FileController(getApplication(), this, getModel(), presenter,
                new ProjectDrawingProvider(presenter, false, true)), this, getModel()));
        presenter
                .installPlugin(new EditMenuPlugin(new EditMenuController(getApplication(), this, getModel(), presenter), this, getModel()));
        presenter.installPlugin(new ViewMenuPlugin(new ViewController(getApplication(), this, getModel(), presenter), this, getModel()));
        presenter.installPlugin(new ArrangeMenuPlugin(new ArrangeMenuController(getApplication(), this, getModel(), presenter), this,
                getModel()));
        presenter
                .installPlugin(new ConfigMenuPlugin(new ConfigController(getApplication(), this, getModel(), presenter), this, getModel()));
        presenter
                .installPlugin(new LayersMenuPlugin(new LayersController(getApplication(), this, getModel(), presenter), this, getModel()));
        presenter.installPlugin(new ToolsMenuPlugin(new ToolsController(getApplication(), this, getModel(), presenter,
                new TraceMaskDrawingProvider(presenter)), this, getModel()));
        presenter
                .installPlugin(new WindowMenuPlugin(new WindowController(getApplication(), this, getModel(), presenter), this, getModel()));
        presenter.installPlugin(new HelpMenuPlugin(new HelpController(getApplication(), this, getModel(), presenter), this, getModel()));
        presenter.installPlugin(new StatusBar(this));
        canvasPlugin = new CanvasPlugin(new CanvasController(getApplication(), this, getModel(), presenter), this, getModel());
        presenter.installPlugin(canvasPlugin);
        propertyPlugin = new PropertyPlugin(this);
        presenter.installPlugin(propertyPlugin);
        presenter.installPlugin(new FramePlugin());
    }

    protected void storeWindowBounds(JFrame frame) {
        Configuration.INSTANCE.setWindowBounds(new WindowBounds(frame.getLocation(), frame.getSize(), frame.getExtendedState()));
    }

    public Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        /*
         * FIXME: hack to prevent painting issues in the scroll bar rulers. Find
         * a better fix if possible.
         */
        Timer timer = new Timer(500, (ActionEvent e) -> canvasPlugin.refresh());
        timer.setRepeats(false);
        timer.start();
    }

    private void createBasePanels() {
        Container c = new Container();
        c.setLayout(new BorderLayout());

        centerPanel = new JPanel(new BorderLayout());
        c.add(centerPanel, BorderLayout.CENTER);

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        c.add(topPanel, BorderLayout.NORTH);

        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        c.add(leftPanel, BorderLayout.WEST);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        c.add(bottomPanel, BorderLayout.SOUTH);

        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        c.add(rightPanel, BorderLayout.EAST);

        setContentPane(c);
    }

    private JMenuBar getMainMenuBar() {
        if (mainMenuBar == null) {
            mainMenuBar = new JMenuBar();
            setJMenuBar(mainMenuBar);
        }
        return mainMenuBar;
    }

    // IView

    @Override
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    @Override
    public int showConfirmDialog(String message, String title, int optionType, int messageType) {
        return JOptionPane.showConfirmDialog(this, message, title, optionType, messageType);
    }

    @Override
    public boolean editProperties(List<PropertyWrapper> properties, Set<PropertyWrapper> defaultedProperties) {
        PropertyEditorDialog editor = DialogFactory.getInstance().createPropertyEditorDialog(properties, "Edit Selection");
        editor.setVisible(true);
        defaultedProperties.addAll(editor.getDefaultedProperties());

        return ButtonDialog.OK.equals(editor.getSelectedButtonCaption());
    }

    private JMenu findOrCreateMenu(String menuName) {
        JMenu menu = findMenu(menuName);

        if (menu == null) {
            menu = new JMenu(menuName);
            menuMap.put(menuName, menu);
            getMainMenuBar().add(menu);
        }

        return menu;
    }

    private JMenu findMenu(String menuName) {
        JMenu menu = null;

        if (menuMap.containsKey(menuName)) {
            menu = menuMap.get(menuName);
        }

        return menu;
    }

    @Override
    public void injectGUIComponent(JComponent component, int position) throws BadPositionException {
        LOG.trace(String.format("injectGUIComponent(%s, %s)", component.getClass().getName(), position));

        switch (position) {
        case SwingConstants.TOP:
            topPanel.add(component);
            break;
        case SwingConstants.LEFT:
            leftPanel.add(component);
            break;
        case SwingConstants.BOTTOM:
            bottomPanel.add(component);
            break;
        case SwingConstants.RIGHT:
            rightPanel.add(component);
            break;
        case SwingConstants.CENTER:
            centerPanel.add(component, BorderLayout.CENTER);
            break;
        default:
            throw new BadPositionException();
        }

        pack();
    }

    @Override
    public void removeGUIComponent(int position) throws BadPositionException {
        LOG.trace(String.format("removeGUIComponent(%s)", position));

        switch (position) {
        case SwingConstants.TOP:
            topPanel.removeAll();
            break;
        case SwingConstants.LEFT:
            leftPanel.removeAll();
            break;
        case SwingConstants.BOTTOM:
            bottomPanel.removeAll();
            break;
        case SwingConstants.RIGHT:
            rightPanel.removeAll();
            break;
        default:
            throw new BadPositionException();
        }

        pack();
    }

    @Override
    public void addMenuAction(Action action, String menuName) {
        LOG.trace(String.format("injectMenuAction(%s, %s)", action == null ? "Separator" : action.getValue(Action.NAME), menuName));
        JMenu menu = findOrCreateMenu(menuName);
        if (action == null) {
            menu.addSeparator();
        } else {
            Boolean isCheckBox = (Boolean) action.getValue(IView.CHECK_BOX_MENU_ITEM);
            String groupName = (String) action.getValue(IView.RADIO_BUTTON_GROUP_KEY);
            if (isCheckBox != null && isCheckBox) {
                menu.add(new JCheckBoxMenuItem(action));
            } else if (groupName != null) {
                ButtonGroup group;
                if (buttonGroupMap.containsKey(groupName)) {
                    group = buttonGroupMap.get(groupName);
                } else {
                    group = new ButtonGroup();
                    buttonGroupMap.put(groupName, group);
                }
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(action);
                group.add(item);
                menu.add(item);
            } else {
                menu.add(action);
            }
        }
    }

    @Override
    public void removeMenuAction(Action action, String menuName) {
        LOG.trace(String.format("removeMenuAction(%s, %s)", action == null ? "Separator" : action.getValue(Action.NAME), menuName));
        JMenu menu = findMenu(menuName);

        if (menu != null) {
            int itemCount = menu.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                JMenuItem item = menu.getItem(i);
                if (item.getAction() == action) {
                    menu.remove(i);
                    break;
                }
            }
        }
    }

    public Action findMenuAction(String menuName, String actionName) {
        LOG.trace(String.format("getMenuAction(%s, %s)", menuName, actionName));
        JMenu menu = findMenu(menuName);

        Action action = null;

        if (menu != null) {
            int itemCount = menu.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                JMenuItem item = menu.getItem(i);
                if (item != null) {
                    Action nextAction = item.getAction();
                    if (nextAction != null) {
                        String value = (String) nextAction.getValue(Action.NAME);
                        if (actionName.equals(value)) {
                            action = nextAction;
                            break;
                        }
                    }
                }
            }
        }
        return action;
    }

    @Override
    public void clearMenuItems(String menuName) {
        LOG.trace(String.format("clearMenuItems(%s)", menuName));
        JMenu menu = findMenu(menuName);
        menu.removeAll();
    }

    @Override
    public void addSubmenu(String name, Icon icon, String parentMenuName) {
        LOG.trace(String.format("injectSubmenu(%s, icon, %s)", name, parentMenuName));
        JMenu menu = findOrCreateMenu(parentMenuName);
        JMenu submenu = new JMenu(name);
        submenu.setIcon(icon);
        menu.add(submenu);
        menuMap.put(name, submenu);
    }

    @Override
    public Path promptFileSave() {
        return DialogFactory.getInstance().showSaveDialog(FileFilterEnum.DIY.getFilter(), Configuration.INSTANCE.getLastPath(), null,
                FileFilterEnum.DIY.getExtensions()[0], null);
    }

    class FramePlugin implements IPlugIn {

        private IPlugInPort plugInPort;

        @Override
        public void connect(IPlugInPort plugInPort) {
            this.plugInPort = plugInPort;
        }

        @Override
        public EnumSet<EventType> getSubscribedEventTypes() {
            return EnumSet.of(EventType.FILE_STATUS_CHANGED);
        }

        @Override
        public void processMessage(EventType eventType, Object... params) {
            if (eventType == EventType.FILE_STATUS_CHANGED) {
                Path path = (Path) params[0];
                if (path == null) {
                    path = Paths.get("Untitled");
                }
                if (!SystemUtils.isMac()) {
                    String modified = (Boolean) params[1] ? " (modified)" : "";
                    setTitle(String.format("DIYLC %s - %s %s", plugInPort.getCurrentVersionNumber(), path.getFileName().toString(),
                            modified));
                } else {
                    setTitle(path.getFileName().toString());
                    JRootPane root = getRootPane();
                    root.putClientProperty("Window.documentModified", (Boolean) params[1]);
                }
            }
        }
    }

    @Override
    public void block() {
        getGlassPane().setVisible(true);
    }

    @Override
    public void unblock() {
        getGlassPane().setVisible(false);
    }

    @Override
    public void refreshActions() {
        boolean enabled = !getModel().getSelectedComponents().isEmpty();
        enableMenuAction(MenuConstants.ARRANGE_MENU, "Group Selection", enabled);
        enableMenuAction(MenuConstants.ARRANGE_MENU, "Ungroup Selection", enabled);
        enableMenuAction(MenuConstants.ARRANGE_MENU, "Send Backward", enabled);
        enableMenuAction(MenuConstants.ARRANGE_MENU, "Bring Forward", enabled);
        enableMenuAction(MenuConstants.ARRANGE_MENU, "Rotate Clockwise", enabled);
        enableMenuAction(MenuConstants.ARRANGE_MENU, "Rotate Counterclockwise", enabled);

        enableMenuAction(MenuConstants.EDIT_MENU, "Cut", enabled);
        enableMenuAction(MenuConstants.EDIT_MENU, "Copy", enabled);
        try {
            enableMenuAction(MenuConstants.EDIT_MENU, "Paste",
                    getController().getClipboard().isDataFlavorAvailable(ComponentTransferable.listFlavor));
        } catch (Exception e) {
            enableMenuAction(MenuConstants.EDIT_MENU, "Copy", enabled);
        }
        enableMenuAction(MenuConstants.EDIT_MENU, "Edit Selection", enabled);
        enableMenuAction(MenuConstants.EDIT_MENU, "Delete Selection", enabled);
        enableMenuAction(MenuConstants.EDIT_MENU, "All Connected", enabled);
        enableMenuAction(MenuConstants.EDIT_RENUMBER_MENU, "Immediate Only", enabled);
        enableMenuAction(MenuConstants.EDIT_RENUMBER_MENU, "Same Type Only", enabled);
        enableMenuAction(MenuConstants.EDIT_RENUMBER_MENU, "Save as Template", enabled);
    }

    private void enableMenuAction(String menuName, String actionName, boolean enabled) {
        Action action = findMenuAction(menuName, actionName);

        if (action != null) {
            action.setEnabled(enabled);
        }
    }

    public DrawingModel getModel() {
        return model;
    }

    public void setModel(DrawingModel model) {
        this.model = model;
    }

    @Override
    public Canvas getCanvas() {
        return canvasPlugin;
    }

    @Override
    public void addPluginComponent(JComponent component, int index) throws BadPositionException {
        injectGUIComponent(component, index);
    }

    @Override
    public double getZoomLevel() {
        return getCanvas().getZoomLevel();
    }

    @Override
    public void setZoomLevel(double zoomLevel) {
        getCanvas().setZoomLevel(zoomLevel);
    }

    @Override
    public JFrame getFrame() {
        return this;
    }

    @Override
    public Drawing getDrawing() {
        return drawing;
    }

    public DrawingController getController() {
        return drawingController;
    }

    public void setController(DrawingController controller) {
        this.drawingController = controller;
    }

    public ApplicationController getApplication() {
        return applicationController;
    }

    public void updateLru(LRU<Path> lru) {
        clearMenuItems(MenuConstants.FILE_OPEN_RECENT_MENU);

        for (Path path : lru.getItems()) {
            addMenuAction(new GenericAction(createDisplayName(path), (event) -> getApplication().open(path)),
                    MenuConstants.FILE_OPEN_RECENT_MENU);
        }

        Configuration.INSTANCE.setLru(lru);
    }

    private String createDisplayName(Path path) {
        String displayName = path.toString();

        Path lastPath = Configuration.INSTANCE.getLastPath();

        if (lastPath != null) {
            try {
                lastPath = lastPath.toAbsolutePath().normalize();
                Path displayPath = lastPath.toAbsolutePath().normalize().relativize(path);

                displayName = displayPath.toString();
            } catch (IllegalArgumentException e) {
                /*
                 * Ignore - a relative path cannot be calculated.
                 */
            }
        }

        return displayName;
    }

}
