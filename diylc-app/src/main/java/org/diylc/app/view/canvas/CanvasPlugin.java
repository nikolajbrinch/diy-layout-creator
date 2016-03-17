package org.diylc.app.view.canvas;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.diylc.app.Accelerators;
import org.diylc.app.ComponentTransferable;
import org.diylc.app.ExpansionMode;
import org.diylc.app.actions.GenericAction;
import org.diylc.app.controllers.CanvasController;
import org.diylc.app.model.Model;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.app.view.BadPositionException;
import org.diylc.app.view.IPlugInPort;
import org.diylc.app.view.ISwingUI;
import org.diylc.app.view.MenuUtils;
import org.diylc.app.view.MouseButton;
import org.diylc.app.view.ProjectDrawingProvider;
import org.diylc.app.view.menus.AbstractPlugin;
import org.diylc.core.components.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.components.Template;
import org.diylc.core.config.Configuration;
import org.diylc.core.config.ConfigurationListener;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanvasPlugin extends AbstractPlugin<CanvasController> implements Canvas {

    private static final Logger LOG = LoggerFactory.getLogger(CanvasPlugin.class);

    private RulerScrollPane scrollPane;

    private CanvasPanel canvasPanel;

    private JPopupMenu popupMenu;

    private IPlugInPort plugInPort;

    private double zoomLevel = 1;

    public CanvasPlugin(CanvasController canvasController, ISwingUI swingUI, Model model) {
        super(canvasController, swingUI, model);
    }

    public void setCanvasPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        this.plugInPort = plugInPort;

        try {
            getView().addPluginComponent(getScrollPane(), SwingConstants.CENTER);
        } catch (BadPositionException e) {
            LOG.error("Could not install canvas plugin", e);
        }
    }

    public CanvasPanel getCanvasPanel() {
        if (canvasPanel == null) {
            canvasPanel = new CanvasPanel(plugInPort);
            canvasPanel.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    canvasPanel.requestFocus();
                    mouseReleased(e);
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    canvasPanel.requestFocus();

                    /*
                     * Invoke the rest of the code later so we get the chance to
                     * process selection messages.
                     */
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (plugInPort.getNewComponentTypeSlot() == null && e.isPopupTrigger()) {
                                // Enable actions.
                                boolean enabled = !plugInPort.getSelectedComponents().isEmpty();
                                findAction("Cut").setEnabled(enabled);
                                findAction("Copy").setEnabled(enabled);
                                try {
                                    findAction("Paste").setEnabled(
                                            getController().getClipboard().isDataFlavorAvailable(ComponentTransferable.listFlavor));
                                } catch (Exception ex) {
                                    findAction("Paste").setEnabled(false);
                                }
                                findAction("Edit Selection").setEnabled(enabled);
                                findAction("Delete Selection").setEnabled(enabled);
                                findAction("All Connected").setEnabled(enabled);
                                findAction("Immediate Only").setEnabled(enabled);
                                findAction("Same Type Only").setEnabled(enabled);
                                findAction("Group Selection").setEnabled(enabled);
                                findAction("Ungroup Selection").setEnabled(enabled);
                                findAction("Send Backward").setEnabled(enabled);
                                findAction("Bring Forward").setEnabled(enabled);
                                findAction("Save as Template").setEnabled(plugInPort.getSelectedComponents().size() == 1);

                                showPopupAt(e.getX(), e.getY());
                            }
                        }
                    });
                }
            });

            canvasPanel.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    if (plugInPort.keyPressed(e.getKeyCode(), e.isControlDown(), e.isShiftDown(), e.isAltDown(), e.isMetaDown())) {
                        e.consume();
                    }
                }
            });

            canvasPanel.addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseMoved(MouseEvent e) {
                    canvasPanel.setCursor(plugInPort.getCursorAt(e.getPoint()));
                    plugInPort.mouseMoved(e.getPoint(), e.isControlDown(), e.isShiftDown(), e.isAltDown(), e.isMetaDown());
                }
            });

            canvasPanel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    plugInPort.mouseClicked(e.getPoint(), MouseButton.getButton(e), e.isControlDown(), e.isShiftDown(), e.isAltDown(),
                            e.isMetaDown(), e.getClickCount());
                }
            });
        }
        return canvasPanel;
    }

    public JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();

            JMenu selectionMenu = new JMenu("Select");
            selectionMenu.setIcon(AppIconLoader.ElementsSelection.getIcon());
            popupMenu.add(selectionMenu);

            popupMenu.addSeparator();
            popupMenu.add(new GenericAction("Cut", AppIconLoader.Cut.getIcon(), Accelerators.CUT, (event) -> getController().cut()));
            popupMenu.add(new GenericAction("Copy", AppIconLoader.Copy.getIcon(), Accelerators.COPY, (event) -> getController().copy()));
            popupMenu
                    .add(new GenericAction("Paste", AppIconLoader.Paste.getIcon(), Accelerators.PASTE, (event) -> getController().paste()));
            popupMenu.add(new GenericAction("Delete Selection", AppIconLoader.Delete.getIcon(), Accelerators.DELETE,
                    (event) -> getController().delete()));
            popupMenu.add(new GenericAction("Edit Selection", AppIconLoader.EditComponent.getIcon(), Accelerators.EDIT_SELECTION,
                    (event) -> getController().edit()));
            popupMenu.addSeparator();
            popupMenu.add(new GenericAction("Rotate Clockwise", AppIconLoader.RotateCW.getIcon(), Accelerators.ROTATE_RIGHT,
                    (event) -> getController().rotateSelection(1)));
            popupMenu.add(new GenericAction("Rotate Counterclockwise", AppIconLoader.RotateCCW.getIcon(), Accelerators.ROTATE_LEFT,
                    (event) -> getController().rotateSelection(-1)));
            popupMenu.add(new GenericAction("Group Selection", AppIconLoader.Group.getIcon(), Accelerators.GROUP,
                    (event) -> getController().group()));
            popupMenu.add(new GenericAction("Ungroup Selection", AppIconLoader.Ungroup.getIcon(), Accelerators.UNGROUP,
                    (event) -> getController().ungroup()));
            popupMenu.add(new GenericAction("Send Backward", AppIconLoader.Back.getIcon(), Accelerators.SEND_TO_BACK,
                    (event) -> getController().sendBackward()));
            popupMenu.add(new GenericAction("Bring Forward", AppIconLoader.Front.getIcon(), Accelerators.BRING_TO_FRONT,
                    (event) -> getController().bringForward()));
            popupMenu.add(new GenericAction("Save as Template", AppIconLoader.BriefcaseAdd.getIcon(), (event) -> getController()
                    .saveAsTemplate()));

            JMenu applyTemplateMenu = new JMenu("Apply Template");
            applyTemplateMenu.setIcon(AppIconLoader.BriefcaseInto.getIcon());
            popupMenu.add(applyTemplateMenu);

            JMenu expandMenu = new JMenu("Expand Selection");
            expandMenu.setIcon(AppIconLoader.BranchAdd.getIcon());
            expandMenu.add(new GenericAction("All Connected", (event) -> getController().expandSelection(ExpansionMode.ALL)));
            expandMenu.add(new GenericAction("Immediate Only", (event) -> getController().expandSelection(ExpansionMode.IMMEDIATE)));
            expandMenu.add(new GenericAction("Same Type Only", (event) -> getController().expandSelection(ExpansionMode.SAME_TYPE)));
            popupMenu.add(expandMenu);

            popupMenu.addSeparator();
            popupMenu.add(new GenericAction("Edit Project", AppIconLoader.DocumentEdit.getIcon(), (event) -> getController().editProject()));
        }

        return popupMenu;
    }

    public void refreshSize() {
        Dimension d = plugInPort.getCanvasDimensions(true);
        getCanvasPanel().setSize(d);
        getCanvasPanel().setPreferredSize(d);
        getScrollPane().setZoomLevel(plugInPort.getZoomLevel());
    }

    /**
     * Causes ruler scroll pane to refresh by sending a fake mouse moved message
     * to the canvasPanel.
     */
    public void refresh() {
        MouseEvent event = new MouseEvent(getCanvasPanel(), MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 1, 1, 0, false);

        getCanvasPanel().dispatchEvent(event);
    }

    private RulerScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new RulerScrollPane(getCanvasPanel(), new ProjectDrawingProvider(plugInPort, true, false), new Size(1d,
                    SizeUnit.cm).convertToPixels(), new Size(1d, SizeUnit.in).convertToPixels());
            scrollPane.setMoveSmallIcon(AppIconLoader.MoveSmall.getIcon());
            boolean metric = Configuration.INSTANCE.getMetric();
            boolean wheelZoom = Configuration.INSTANCE.getWheelZoom();
            Configuration.INSTANCE.addListener(Configuration.Key.WHEEL_ZOOM, new ConfigurationListener() {
                @Override
                public void onValueChanged(Object oldValue, Object newValue) {
                    scrollPane.setWheelScrollingEnabled(!(Boolean) newValue);
                }
            });
            scrollPane.setMetric(metric);
            scrollPane.setWheelScrollingEnabled(!wheelZoom);
            scrollPane.addUnitListener(new IRulerListener() {

                @Override
                public void unitsChanged(boolean isMetric) {
                    plugInPort.setMetric(isMetric);
                }
            });
            scrollPane.addMouseWheelListener(new MouseWheelListener() {

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    boolean wheelZoom = Configuration.INSTANCE.getWheelZoom();
                    if (!wheelZoom) {
                        return;
                    }
                    double d = plugInPort.getZoomLevel();
                    Double[] availableZoomLevels = plugInPort.getAvailableZoomLevels();
                    if (e.getWheelRotation() > 0) {
                        int i = availableZoomLevels.length - 1;
                        while (i > 0 && availableZoomLevels[i] >= d) {
                            i--;
                        }
                        plugInPort.setZoomLevel(availableZoomLevels[i]);
                    } else {
                        int i = 0;
                        while (i < availableZoomLevels.length - 1 && availableZoomLevels[i] <= d) {
                            i++;
                        }
                        plugInPort.setZoomLevel(availableZoomLevels[i]);
                    }
                }
            });
        }
        return scrollPane;
    }

    private void showPopupAt(int x, int y) {
        updateSelectionMenu(x, y);
        updateApplyTemplateMenu();
        getPopupMenu().show(getCanvasPanel(), x, y);
    }

    private MenuElement findMenu(String menuName) {
        return MenuUtils.findMenu(getPopupMenu(), menuName);
    }

    private Action findAction(String actionName) {
        return MenuUtils.findAction(getPopupMenu(), actionName);
    }

    private void updateSelectionMenu(int x, int y) {
        JMenu selectionMenu = (JMenu) findMenu("Select");
        selectionMenu.removeAll();

        for (IDIYComponent component : plugInPort.findComponentsAt(new Point(x, y))) {
            JMenuItem item = new JMenuItem(component.getName());
            final IDIYComponent finalComponent = component;
            item.addActionListener((event) -> getController().selectComponent(finalComponent));
            selectionMenu.add(item);
        }
    }

    private void updateApplyTemplateMenu() {
        JMenuItem applyTemplateAction = (JMenuItem) findMenu("Apply Template");
        applyTemplateAction.removeAll();

        List<Template> templates = null;

        try {
            templates = plugInPort.getTemplatesForSelection();
        } catch (Exception e) {
            LOG.info("Could not get templates for selection");
            applyTemplateAction.setEnabled(false);
        }

        if (templates == null) {
            return;
        }

        applyTemplateAction.setEnabled(templates.size() > 0);

        for (Template template : templates) {
            JMenuItem item = new JMenuItem(template.getName());
            final Template finalTemplate = template;
            item.addActionListener((event) -> getController().applyTemplate(finalTemplate));
            applyTemplateAction.add(item);
        }
    }

    @Override
    public Rectangle getVisibleRect() {
        return getCanvasPanel().getVisibleRect();
    }

    @Override
    public int getWidth() {
        return getCanvasPanel().getWidth();
    }

    @Override
    public int getHeight() {
        return getCanvasPanel().getHeight();
    }

    @Override
    public void scrollRectToVisible(Rectangle visibleRect) {
        getCanvasPanel().scrollRectToVisible(visibleRect);
    }

    @Override
    public void revalidate() {
        getCanvasPanel().revalidate();
    }

    @Override
    public void repaint() {
        getCanvasPanel().repaint();
    }

    @Override
    public void updateZoomLevel(double zoomLevel) {
        getController().updateZoomLevel(zoomLevel);
    }

    @Override
    public void init(Project project, boolean freshStart) {
        getController().init(project, freshStart);
    }

}
