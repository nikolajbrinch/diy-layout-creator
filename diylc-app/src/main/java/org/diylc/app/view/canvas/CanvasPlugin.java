package org.diylc.app.view.canvas;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.diylc.app.BadPositionException;
import org.diylc.app.EventType;
import org.diylc.app.IPlugIn;
import org.diylc.app.IPlugInPort;
import org.diylc.app.ISwingUI;
import org.diylc.app.MouseButton;
import org.diylc.app.menus.arrange.ArrangeActionFactory;
import org.diylc.app.menus.edit.ComponentTransferable;
import org.diylc.app.menus.edit.EditActionFactory;
import org.diylc.app.menus.edit.ExpansionMode;
import org.diylc.app.menus.file.ProjectDrawingProvider;
import org.diylc.app.utils.AppIconLoader;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Template;
import org.diylc.core.config.Configuration;
import org.diylc.core.config.ConfigurationListener;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanvasPlugin implements IPlugIn, ClipboardOwner {

    private static final Logger LOG = LoggerFactory.getLogger(CanvasPlugin.class);

    private RulerScrollPane scrollPane;
    private CanvasPanel canvasPanel;
    private JPopupMenu popupMenu;
    private JMenu selectionMenu;
    private JMenu expandMenu;
    private JMenu applyTemplateMenu;

    private EditActionFactory.CutAction cutAction;
    private EditActionFactory.CopyAction copyAction;
    private EditActionFactory.PasteAction pasteAction;
    private EditActionFactory.EditSelectionAction editSelectionAction;
    private EditActionFactory.DeleteSelectionAction deleteSelectionAction;
    private EditActionFactory.SaveAsTemplateAction saveAsTemplateAction;
    private ArrangeActionFactory.GroupAction groupAction;
    private ArrangeActionFactory.UngroupAction ungroupAction;
    private ArrangeActionFactory.SendToBackAction sendToBackAction;
    private ArrangeActionFactory.BringToFrontAction bringToFrontAction;
    private EditActionFactory.ExpandSelectionAction expandSelectionAllAction;
    private EditActionFactory.ExpandSelectionAction expandSelectionImmediateAction;
    private EditActionFactory.ExpandSelectionAction expandSelectionSameTypeAction;
    private ArrangeActionFactory.RotateSelectionAction rotateClockwiseAction;
    private ArrangeActionFactory.RotateSelectionAction rotateCounterclockwiseAction;

    private IPlugInPort plugInPort;
    private ISwingUI swingUI;

    private Clipboard clipboard;

    private double zoomLevel = 1;

    public CanvasPlugin(ISwingUI swingUI) {
        this.swingUI = swingUI;
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Override
    public void connect(IPlugInPort plugInPort) {
        this.plugInPort = plugInPort;
        try {
            swingUI.injectGUIComponent(getScrollPane(), SwingConstants.CENTER);
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

                    // Invoke the rest of the code later so we get the chance to
                    // process selection messages.
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (plugInPort.getNewComponentTypeSlot() == null && e.isPopupTrigger()) {
                                // Enable actions.
                                boolean enabled = !plugInPort.getSelectedComponents().isEmpty();
                                getCutAction().setEnabled(enabled);
                                getCopyAction().setEnabled(enabled);
                                try {
                                    getPasteAction().setEnabled(clipboard.isDataFlavorAvailable(ComponentTransferable.listFlavor));
                                } catch (Exception ex) {
                                    getPasteAction().setEnabled(false);
                                }
                                getEditSelectionAction().setEnabled(enabled);
                                getDeleteSelectionAction().setEnabled(enabled);
                                getExpandSelectionAllAction().setEnabled(enabled);
                                getExpandSelectionImmediateAction().setEnabled(enabled);
                                getExpandSelectionSameTypeAction().setEnabled(enabled);
                                getGroupAction().setEnabled(enabled);
                                getUngroupAction().setEnabled(enabled);
                                getSendToBackAction().setEnabled(enabled);
                                getBringToFrontAction().setEnabled(enabled);

                                getSaveAsTemplateAction().setEnabled(plugInPort.getSelectedComponents().size() == 1);

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
                    plugInPort.mouseClicked(e.getPoint(), MouseButton.getButton(e), e.isControlDown(), e.isShiftDown(), e.isAltDown(), e.isMetaDown(),
                            e.getClickCount());
                }
            });
        }
        return canvasPanel;
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
        getPopupMenu().show(canvasPanel, x, y);
    }

    public JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
            popupMenu.add(getSelectionMenu());
            popupMenu.addSeparator();
            popupMenu.add(getCutAction());
            popupMenu.add(getCopyAction());
            popupMenu.add(getPasteAction());
            popupMenu.addSeparator();
            popupMenu.add(getEditSelectionAction());
            popupMenu.add(getDeleteSelectionAction());
            popupMenu.add(getRotateClockwiseAction());
            popupMenu.add(getRotateCounterclockwiseAction());
            popupMenu.add(getGroupAction());
            popupMenu.add(getUngroupAction());
            popupMenu.add(getSendToBackAction());
            popupMenu.add(getBringToFrontAction());
            popupMenu.add(getSaveAsTemplateAction());
            popupMenu.add(getApplyTemplateMenu());
            popupMenu.add(getExpandMenu());
            popupMenu.addSeparator();
            popupMenu.add(EditActionFactory.INSTANCE.createEditProjectAction(plugInPort));
        }
        return popupMenu;
    }

    public JMenu getSelectionMenu() {
        if (selectionMenu == null) {
            selectionMenu = new JMenu("Select");
            selectionMenu.setIcon(AppIconLoader.ElementsSelection.getIcon());
        }
        return selectionMenu;
    }

    public JMenu getExpandMenu() {
        if (expandMenu == null) {
            expandMenu = new JMenu("Expand Selection");
            expandMenu.setIcon(AppIconLoader.BranchAdd.getIcon());
            expandMenu.add(getExpandSelectionAllAction());
            expandMenu.add(getExpandSelectionImmediateAction());
            expandMenu.add(getExpandSelectionSameTypeAction());
        }
        return expandMenu;
    }

    public JMenu getApplyTemplateMenu() {
        if (applyTemplateMenu == null) {
            applyTemplateMenu = new JMenu("Apply Template");
            applyTemplateMenu.setIcon(AppIconLoader.BriefcaseInto.getIcon());
        }
        return applyTemplateMenu;
    }

    private void updateSelectionMenu(int x, int y) {
        getSelectionMenu().removeAll();
        for (IDIYComponent component : plugInPort.findComponentsAt(new Point(x, y))) {
            JMenuItem item = new JMenuItem(component.getName());
            final IDIYComponent finalComponent = component;
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();
                    newSelection.add(finalComponent);
                    plugInPort.updateSelection(newSelection);
                    plugInPort.refresh();
                }
            });
            getSelectionMenu().add(item);
        }
    }

    private void updateApplyTemplateMenu() {
        getApplyTemplateMenu().removeAll();
        List<Template> templates = null;

        try {
            templates = plugInPort.getTemplatesForSelection();
        } catch (Exception e) {
            LOG.info("Could not get templates for selection");
            getApplyTemplateMenu().setEnabled(false);
        }

        if (templates == null)
            return;

        getApplyTemplateMenu().setEnabled(templates.size() > 0);

        for (Template template : templates) {
            JMenuItem item = new JMenuItem(template.getName());
            final Template finalTemplate = template;
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    plugInPort.applyTemplateToSelection(finalTemplate);
                }
            });
            getApplyTemplateMenu().add(item);
        }
    }

    public EditActionFactory.CutAction getCutAction() {
        if (cutAction == null) {
            cutAction = EditActionFactory.INSTANCE.createCutAction(plugInPort, clipboard, this);
        }
        return cutAction;
    }

    public EditActionFactory.CopyAction getCopyAction() {
        if (copyAction == null) {
            copyAction = EditActionFactory.INSTANCE.createCopyAction(plugInPort, clipboard, this);
        }
        return copyAction;
    }

    public EditActionFactory.PasteAction getPasteAction() {
        if (pasteAction == null) {
            pasteAction = EditActionFactory.INSTANCE.createPasteAction(plugInPort, clipboard);
        }
        return pasteAction;
    }

    public EditActionFactory.EditSelectionAction getEditSelectionAction() {
        if (editSelectionAction == null) {
            editSelectionAction = EditActionFactory.INSTANCE.createEditSelectionAction(plugInPort);
        }
        return editSelectionAction;
    }

    public EditActionFactory.DeleteSelectionAction getDeleteSelectionAction() {
        if (deleteSelectionAction == null) {
            deleteSelectionAction = EditActionFactory.INSTANCE.createDeleteSelectionAction(plugInPort);
        }
        return deleteSelectionAction;
    }

    public ArrangeActionFactory.RotateSelectionAction getRotateClockwiseAction() {
        if (rotateClockwiseAction == null) {
            rotateClockwiseAction = ArrangeActionFactory.INSTANCE.createRotateSelectionAction(plugInPort, 1);
        }
        return rotateClockwiseAction;
    }

    public ArrangeActionFactory.RotateSelectionAction getRotateCounterclockwiseAction() {
        if (rotateCounterclockwiseAction == null) {
            rotateCounterclockwiseAction = ArrangeActionFactory.INSTANCE.createRotateSelectionAction(plugInPort, -1);
        }
        return rotateCounterclockwiseAction;
    }

    public EditActionFactory.SaveAsTemplateAction getSaveAsTemplateAction() {
        if (saveAsTemplateAction == null) {
            saveAsTemplateAction = EditActionFactory.INSTANCE.createSaveAsTemplateAction(plugInPort);
        }
        return saveAsTemplateAction;
    }

    public ArrangeActionFactory.GroupAction getGroupAction() {
        if (groupAction == null) {
            groupAction = ArrangeActionFactory.INSTANCE.createGroupAction(plugInPort);
        }
        return groupAction;
    }

    public ArrangeActionFactory.UngroupAction getUngroupAction() {
        if (ungroupAction == null) {
            ungroupAction = ArrangeActionFactory.INSTANCE.createUngroupAction(plugInPort);
        }
        return ungroupAction;
    }

    public ArrangeActionFactory.SendToBackAction getSendToBackAction() {
        if (sendToBackAction == null) {
            sendToBackAction = ArrangeActionFactory.INSTANCE.createSendToBackAction(plugInPort);
        }
        return sendToBackAction;
    }

    public ArrangeActionFactory.BringToFrontAction getBringToFrontAction() {
        if (bringToFrontAction == null) {
            bringToFrontAction = ArrangeActionFactory.INSTANCE.createBringToFrontAction(plugInPort);
        }
        return bringToFrontAction;
    }

    public EditActionFactory.ExpandSelectionAction getExpandSelectionAllAction() {
        if (expandSelectionAllAction == null) {
            expandSelectionAllAction = EditActionFactory.INSTANCE.createExpandSelectionAction(plugInPort, ExpansionMode.ALL);
        }
        return expandSelectionAllAction;
    }

    public EditActionFactory.ExpandSelectionAction getExpandSelectionImmediateAction() {
        if (expandSelectionImmediateAction == null) {
            expandSelectionImmediateAction = EditActionFactory.INSTANCE.createExpandSelectionAction(plugInPort, ExpansionMode.IMMEDIATE);
        }
        return expandSelectionImmediateAction;
    }

    public EditActionFactory.ExpandSelectionAction getExpandSelectionSameTypeAction() {
        if (expandSelectionSameTypeAction == null) {
            expandSelectionSameTypeAction = EditActionFactory.INSTANCE.createExpandSelectionAction(plugInPort, ExpansionMode.SAME_TYPE);
        }
        return expandSelectionSameTypeAction;
    }

    @Override
    public EnumSet<EventType> getSubscribedEventTypes() {
        return EnumSet.of(EventType.PROJECT_LOADED, EventType.ZOOM_CHANGED, EventType.REPAINT);
    }

    @Override
    public void processMessage(final EventType eventType, Object... params) {
        switch (eventType) {
            case PROJECT_LOADED:
                refreshSize();
                if ((Boolean) params[1]) {
                    // Scroll to the center.
                    Rectangle visibleRect = canvasPanel.getVisibleRect();
                    visibleRect.setLocation((canvasPanel.getWidth() - visibleRect.width) / 2,
                            (canvasPanel.getHeight() - visibleRect.height) / 2);
                    canvasPanel.scrollRectToVisible(visibleRect);
                    canvasPanel.revalidate();
                }
                break;
            case ZOOM_CHANGED:
                Rectangle visibleRect = canvasPanel.getVisibleRect();
                refreshSize();
                // Try to set the visible area to be centered with the previous
                // one.
                double zoomFactor = (Double) params[0] / zoomLevel;
                visibleRect.setBounds((int) (visibleRect.x * zoomFactor), (int) (visibleRect.y * zoomFactor), visibleRect.width,
                        visibleRect.height);
                canvasPanel.scrollRectToVisible(visibleRect);
                canvasPanel.revalidate();

                zoomLevel = (Double) params[0];
                break;
            case REPAINT:
                canvasPanel.repaint();
                break;
            default:
                break;
        }
    }

    private void refreshSize() {
        Dimension d = plugInPort.getCanvasDimensions(true);
        canvasPanel.setSize(d);
        canvasPanel.setPreferredSize(d);
        getScrollPane().setZoomLevel(plugInPort.getZoomLevel());
    }

    /**
     * Causes ruler scroll pane to refresh by sending a fake mouse moved message
     * to the canvasPanel.
     */
    public void refresh() {
        MouseEvent event = new MouseEvent(canvasPanel, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 1, 1, 0, false);

        canvasPanel.dispatchEvent(event);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
