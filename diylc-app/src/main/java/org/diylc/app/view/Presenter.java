package org.diylc.app.view;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.diylc.app.io.ProjectFileManager;
import org.diylc.app.model.Model;
import org.diylc.app.utils.CalcUtils;
import org.diylc.app.utils.ModelUtils;
import org.diylc.app.utils.StringUtils;
import org.diylc.app.view.rendering.DrawingContext;
import org.diylc.app.view.rendering.DrawingManager;
import org.diylc.app.view.rendering.DrawingOption;
import org.diylc.app.view.rendering.RenderingConstants;
import org.diylc.appframework.update.Version;
import org.diylc.appframework.update.VersionNumber;
import org.diylc.components.IComponentFilter;
import org.diylc.components.connectivity.SolderPad;
import org.diylc.components.registry.ComparatorFactory;
import org.diylc.components.registry.ComponentProcessor;
import org.diylc.components.registry.ComponentRegistry;
import org.diylc.components.registry.ComponentType;
import org.diylc.core.EventType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.Template;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
import org.diylc.core.events.MessageDispatcher;
import org.diylc.core.measures.SizeUnit;
import org.diylc.core.utils.Constants;
import org.diylc.core.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The main presenter class, contains core app logic and drawing routines.
 *
 * @author Branislav Stojkovic
 */
public class Presenter implements IPlugInPort {

    private static final Logger LOG = LoggerFactory.getLogger(Presenter.class);

    public static VersionNumber CURRENT_VERSION = new VersionNumber(4, 0, 0);

    public static final String DEFAULTS_KEY_PREFIX = "default.";

    public static final int ICON_SIZE = 32;

    /*
     * Read the latest version from the local update.xml file
     */
    static {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(SystemUtils.getConfigFile("update.xml")));
            XStream xStream = new XStream(new DomDriver());
            @SuppressWarnings("unchecked")
            List<Version> allVersions = (List<Version>) xStream.fromXML(in);
            in.close();
            CURRENT_VERSION = allVersions.get(allVersions.size() - 1).getVersionNumber();
        } catch (IOException e) {
            LOG.error("Could not find version number, using default", e);
        }
    }

    /*
     * Maps component class names to ComponentType objects.
     */
    private List<IPlugIn> plugIns = new ArrayList<IPlugIn>();

    /*
     * Maps components that have at least one dragged point to set of indices
     * that designate which of their control points are being dragged.
     */
    private Map<IDIYComponent, Set<Integer>> controlPointMap;

    private Model model;

    /*
     * Utilities
     */
    private final DrawingManager drawingManager;

    private final ProjectFileManager projectFileManager;

    private final InstantiationManager instantiationManager;

    private Rectangle selectionRect;

    private View view;

    private final MessageDispatcher<EventType> messageDispatcher;

    // D&D
    private boolean dragInProgress = false;

    /*
     * Previous mouse location, not scaled for zoom factor.
     */
    private Point previousDragPoint = null;

    private Project preDragProject = null;

    private int dragAction;

    private Point previousScaledPoint;

    public Presenter() {
        messageDispatcher = new MessageDispatcher<EventType>(true);
        drawingManager = new DrawingManager(getMessageDispatcher());
        projectFileManager = new ProjectFileManager(getMessageDispatcher());
        instantiationManager = new InstantiationManager();
    }

    public void installPlugin(IPlugIn plugIn) {
        LOG.trace(String.format("installPlugin(%s)", plugIn.getClass().getSimpleName()));
        plugIns.add(plugIn);
        plugIn.connect(this);
        getMessageDispatcher().registerListener(plugIn);
        getMessageDispatcher().dispatchMessage(EventType.SPLASH_UPDATE, "Installing plugin " + plugIn.getClass().getName());
    }

    public void dispose() {
        for (IPlugIn plugIn : plugIns) {
            getMessageDispatcher().unregisterListener(plugIn);
        }
    }

    @Override
    public Double[] getAvailableZoomLevels() {
        return new Double[] { 0.25d, 0.3333d, 0.5d, 0.6667d, 0.75d, 1d, 1.25d, 1.5d, 2d };
    }

    @Override
    public double getZoomLevel() {
        return getDrawingManager().getZoomLevel();
    }

    @Override
    public void setZoomLevel(double zoomLevel) {
        LOG.trace(String.format("setZoomLevel(%s)", zoomLevel));
        if (getDrawingManager().getZoomLevel() == zoomLevel) {
            return;
        }
        getDrawingManager().setZoomLevel(zoomLevel);
    }

    @Override
    public Cursor getCursorAt(Point point) {
        /*
         * Only change the cursor if we're not making a new component.
         */
        if (instantiationManager.getComponentTypeSlot() == null) {
            /*
             * Scale point to remove zoom factor.
             */
            Point2D scaledPoint = scalePoint(point);
            if (controlPointMap != null && !controlPointMap.isEmpty()) {
                return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            }
            for (IDIYComponent component : getModel().getProject().getComponents()) {
                if (!getModel().isComponentLocked(component)) {
                    Area area = getDrawingManager().getComponentArea(component);
                    if (area != null && area.contains(scaledPoint)) {
                        return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                    }
                }
            }
        }

        return Cursor.getDefaultCursor();
    }

    @Override
    public Dimension getCanvasDimensions(boolean useZoom) {
        return getDrawingManager().calculateCanvasDimensions(getModel().getProject(), getDrawingManager().getZoomLevel(), useZoom);
    }

    @Override
    public void loadProject(Project project, boolean freshStart) {
        LOG.trace(String.format("loadProject(%s, %s)", project.getTitle(), freshStart));
        getModel().setProject(project);
        getDrawingManager().clearComponentAreaMap();
        getView().clearSelection();
        getMessageDispatcher().dispatchMessage(EventType.PROJECT_LOADED, project, freshStart);
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
        getMessageDispatcher().dispatchMessage(EventType.LAYER_STATE_CHANGED, getModel().getProject().getLockedLayers());
    }

    @Override
    public void loadProjectFromFile(Path path) throws Exception {
        LOG.trace(String.format("loadProjectFromFile(%s)", path.toAbsolutePath()));
        List<String> warnings = new ArrayList<String>();
        Project project = (Project) getProjectFileManager().deserializeProjectFromFile(path, warnings);
        loadProject(project, true);
        getProjectFileManager().fireFileStatusChanged();
        if (!warnings.isEmpty()) {
            StringBuilder builder = new StringBuilder("<html>File was opened, but there were some issues with it:<br><br>");
            for (String warning : warnings) {
                builder.append(warning);
                builder.append("<br>");
            }
            builder.append("</html");
            getView().showMessage(builder.toString(), "Warning", View.WARNING_MESSAGE);
        }
    }

    @Override
    public boolean allowFileAction() {
        boolean response = true;

        if (getProjectFileManager().isModified()) {
            int dialogResponse = getView().showConfirmDialog("There are unsaved changes. Would you like to save them?", "Warning",
                    View.YES_NO_CANCEL_OPTION, View.WARNING_MESSAGE);

            if (dialogResponse == View.YES_OPTION) {
                if (getModel().getCurrentFile() == null) {
                    Path path = getView().promptFileSave();

                    if (path == null) {
                        response = false;
                    } else {
                        getModel().saveProjectToFile(path, false);
                    }
                } else {
                    getModel().saveProjectToFile(getModel().getCurrentFile(), false);
                }
            } else {
                response = dialogResponse != View.CANCEL_OPTION && dialogResponse != View.CLOSED_OPTION;
            }
        }

        return response;
    }

    @Override
    public void draw(Graphics2D g2d, Set<DrawingOption> drawOptions, IComponentFilter filter) {
        if (getModel().getProject() == null) {
            return;
        }
        Set<IDIYComponent> groupedComponents = new HashSet<IDIYComponent>();

        for (IDIYComponent component : getModel().getProject().getComponents()) {
            /*
             * Only try to draw control points of ungrouped components.
             */
            if (findAllGroupedComponents(component).size() > 1) {
                groupedComponents.add(component);
            }
        }

        /*
         * Don't draw the component in the slot if both control points match.
         */
        List<IDIYComponent> componentSlotToDraw;

        if (instantiationManager.getFirstControlPoint() != null && instantiationManager.getPotentialControlPoint() != null
                && instantiationManager.getFirstControlPoint().equals(instantiationManager.getPotentialControlPoint())) {
            componentSlotToDraw = null;
        } else {
            componentSlotToDraw = instantiationManager.getComponentSlot();
        }

        List<IDIYComponent> failedComponents = new ArrayList<IDIYComponent>();

        if (getModel().getProject() != null) {
            DrawingContext drawingContext = new DrawingContext(g2d, getModel().getProject(), drawOptions, filter, selectionRect, getView()
                    .getSelectedComponents(), getModel().getLockedComponents(), groupedComponents, Arrays.asList(
                    instantiationManager.getFirstControlPoint(), instantiationManager.getPotentialControlPoint()), componentSlotToDraw,
                    dragInProgress);

            failedComponents = getDrawingManager().drawProject(drawingContext);
        }

        List<String> failedComponentNames = new ArrayList<String>();

        for (IDIYComponent component : failedComponents) {
            failedComponentNames.add(component.getName());
        }

        Collections.sort(failedComponentNames);

        if (!failedComponentNames.isEmpty()) {
            getMessageDispatcher().dispatchMessage(
                    EventType.STATUS_MESSAGE_CHANGED,
                    "<html><font color='red'>Failed to draw components: " + StringUtils.toCommaString(failedComponentNames)
                            + "</font></html>");
        } else {
            getMessageDispatcher().dispatchMessage(EventType.STATUS_MESSAGE_CHANGED, "");
        }
    }

    /**
     * Finds all components whose areas include the specified {@link Point}.
     * Point is <b>not</b> scaled by the zoom factor. Components that belong to
     * locked layers are ignored.
     *
     * @return
     */
    private List<IDIYComponent> findComponentsAtScaled(Point point) {
        List<IDIYComponent> components = getDrawingManager().findComponentsAt(point, getModel().getProject());
        Iterator<IDIYComponent> iterator = components.iterator();

        while (iterator.hasNext()) {
            if (getModel().isComponentLocked(iterator.next())) {
                iterator.remove();
            }
        }

        return components;
    }

    @Override
    public void mouseClicked(Point point, MouseButton button, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean metaDown,
            int clickCount) {
        LOG.trace(String.format("mouseClicked(%s, %s, %s, %s, %s)", point, button, ctrlDown, shiftDown, altDown));
        Point scaledPoint = scalePoint(point);

        if (clickCount >= 2) {
            editSelection();
        } else {
            if (instantiationManager.getComponentTypeSlot() != null) {
                /*
                 * Try to rotate the component on right click while creating.
                 */
                if (button != MouseButton.LEFT) {
                    instantiationManager.tryToRotateComponentSlot();
                    getMessageDispatcher().dispatchMessage(EventType.REPAINT);
                    return;
                }

                /*
                 * Keep the reference to component type for later.
                 */
                ComponentType componentTypeSlot = instantiationManager.getComponentTypeSlot();
                Template template = instantiationManager.getTemplate();
                Project oldProject = getModel().getProject().clone();

                switch (componentTypeSlot.getCreationMethod()) {
                case SINGLE_CLICK:
                    try {
                        if (isSnapToGrid()) {
                            CalcUtils.snapPointToGrid(scaledPoint, getModel().getProject().getGridSpacing());
                        }

                        List<IDIYComponent> componentSlot = instantiationManager.getComponentSlot();
                        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();

                        for (IDIYComponent component : componentSlot) {
                            addComponent(component, true);
                            newSelection.add(component);
                        }

                        /*
                         * Select the new component
                         */
                        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
                        getView().updateSelection(newSelection);
                    } catch (Exception e) {
                        LOG.error("Error instatiating component of type: " + componentTypeSlot.getInstanceClass().getName(), e);
                    }

                    if (componentTypeSlot.isAutoEdit() && Configuration.INSTANCE.getAutoEdit()) {
                        editSelection();
                    }
                    if (Configuration.INSTANCE.getContinuousCreation()) {
                        setNewComponentTypeSlot(componentTypeSlot, template);
                    } else {
                        setNewComponentTypeSlot(null, null);
                    }
                    break;
                case POINT_BY_POINT:
                    /*
                     * First click is just to set the controlPointSlot and
                     * componentSlot.
                     */
                    if (isSnapToGrid()) {
                        CalcUtils.snapPointToGrid(scaledPoint, getModel().getProject().getGridSpacing());
                    }
                    if (instantiationManager.getComponentSlot() == null) {
                        try {
                            instantiationManager.instatiatePointByPoint(scaledPoint, getModel().getProject());
                        } catch (Exception e) {
                            getView().showMessage("Could not create component. Check log for details.", "Error", View.ERROR_MESSAGE);
                            LOG.error("Could not create component", e);
                        }
                        getMessageDispatcher().dispatchMessage(EventType.SLOT_CHANGED, componentTypeSlot,
                                instantiationManager.getFirstControlPoint());
                        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
                    } else {
                        /*
                         * On the second click, add the component to the
                         * project.
                         */
                        List<IDIYComponent> componentSlot = instantiationManager.getComponentSlot();
                        componentSlot.get(0).setControlPoint(scaledPoint, 1);
                        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();

                        for (IDIYComponent component : componentSlot) {
                            addComponent(component, true);
                            /*
                             * Select the new component if it's not locked.
                             */
                            if (!getModel().isComponentLocked(component)) {
                                newSelection.add(component);
                            }
                        }

                        getView().updateSelection(newSelection);
                        getMessageDispatcher().dispatchMessage(EventType.REPAINT);

                        if (componentTypeSlot.isAutoEdit() && Configuration.INSTANCE.getAutoEdit()) {
                            editSelection();
                        }
                        if (Configuration.INSTANCE.getContinuousCreation()) {
                            setNewComponentTypeSlot(componentTypeSlot, template);
                        } else {
                            setNewComponentTypeSlot(null, null);
                        }
                    }
                    break;
                default:
                    LOG.error("Unknown creation method: " + componentTypeSlot.getCreationMethod());
                }

                /*
                 * Notify the listeners.
                 */
                if (!oldProject.equals(getModel().getProject())) {
                    getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(),
                            "Add " + componentTypeSlot.getName());
                    getProjectFileManager().notifyFileChange();
                }
            } else {
                List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>(getView().getSelectedComponents());
                List<IDIYComponent> components = findComponentsAtScaled(scaledPoint);

                /*
                 * If there's nothing under mouse cursor deselect all.
                 */
                if (components.isEmpty()) {
                    if (!ctrlDown) {
                        newSelection.clear();
                    }
                } else {
                    IDIYComponent topComponent = components.get(0);
                    /*
                     * If ctrl (Windows) or cmd (Mac) is pressed just toggle the
                     * component under mouse cursor.
                     */
                    if ((ctrlDown && !SystemUtils.isMac()) || (metaDown && SystemUtils.isMac() && button != MouseButton.RIGHT)) {
                        if (newSelection.contains(topComponent)) {
                            newSelection.removeAll(findAllGroupedComponents(topComponent));
                        } else {
                            newSelection.addAll(findAllGroupedComponents(topComponent));
                        }
                    } else {
                        /*
                         * Otherwise just select that one component.
                         */
                        if (button == MouseButton.LEFT || !newSelection.contains(topComponent)) {
                            newSelection.clear();
                        }
                        newSelection.addAll(findAllGroupedComponents(topComponent));
                    }
                }

                getView().updateSelection(newSelection);

                getMessageDispatcher().dispatchMessage(EventType.REPAINT);
            }
        }
    }

    @Override
    public boolean keyPressed(int key, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean metaDown) {
        if (key != VK_DOWN && key != VK_LEFT && key != VK_UP && key != VK_RIGHT) {
            return false;
        }

        LOG.trace(String.format("keyPressed(%s, %s, %s, %s, %s)", key, ctrlDown, shiftDown, altDown, metaDown));
        Map<IDIYComponent, Set<Integer>> controlPointMap = new HashMap<IDIYComponent, Set<Integer>>();

        /*
         * If there aren't any control points, try to add all the selected
         * components with all their control points. That will allow the user to
         * drag the whole components.
         */
        for (IDIYComponent c : getView().getSelectedComponents()) {
            Set<Integer> pointIndices = new HashSet<Integer>();
            if (c.getControlPointCount() > 0) {
                for (int i = 0; i < c.getControlPointCount(); i++) {
                    pointIndices.add(i);
                }
                controlPointMap.put(c, pointIndices);
            }
        }

        if (controlPointMap.isEmpty()) {
            return false;
        }

        boolean snapToGrid = Configuration.INSTANCE.getSnapToGrip();
        if (shiftDown) {
            snapToGrid = !snapToGrid;
        }

        if (altDown) {
            Project oldProject = null;

            if (key == IKeyProcessor.VK_RIGHT) {
                oldProject = getModel().getProject().clone();
                getView().getController().rotateSelection(1);
            } else if (key == IKeyProcessor.VK_LEFT) {
                oldProject = getModel().getProject().clone();
                getView().getController().rotateSelection(-1);
            } else {
                return false;
            }

            getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(),
                    "Rotate Selection");
            getMessageDispatcher().dispatchMessage(EventType.REPAINT);

            return true;
        }

        /*
         * Expand control points to include all stuck components.
         */
        boolean sticky = Configuration.INSTANCE.getStickyPoints();

        if (ctrlDown && !SystemUtils.isMac() || metaDown && SystemUtils.isMac()) {
            sticky = !sticky;
        }

        if (sticky) {
            getView().includeStuckComponents(controlPointMap);
        }

        int d;

        if (snapToGrid) {
            d = (int) getModel().getProject().getGridSpacing().convertToPixels();
        } else {
            d = 1;
        }

        int dx = 0;
        int dy = 0;
        switch (key) {
        case IKeyProcessor.VK_DOWN:
            dy = d;
            break;
        case IKeyProcessor.VK_LEFT:
            dx = -d;
            break;
        case IKeyProcessor.VK_UP:
            dy = -d;
            break;
        case IKeyProcessor.VK_RIGHT:
            dx = d;
            break;
        default:
            return false;
        }

        Project oldProject = getModel().getProject().clone();
        moveComponents(controlPointMap, dx, dy, snapToGrid);
        getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(), "Move Selection");
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);

        return true;
    }

    @Override
    public void editSelection() {
        List<PropertyWrapper> properties = getMutualSelectionProperties();

        if (properties != null && !properties.isEmpty()) {
            Set<PropertyWrapper> defaultedProperties = new HashSet<PropertyWrapper>();
            boolean edited = getView().editProperties(properties, defaultedProperties);

            if (edited) {
                try {
                    applyPropertiesToSelection(properties);
                } catch (Exception e1) {
                    getView().showMessage("Error occured while editing selection. Check the log for details.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    LOG.error("Error applying properties", e1);
                }

                /*
                 * Save default values.
                 */
                for (PropertyWrapper property : defaultedProperties) {
                    if (property.getValue() != null) {
                        setSelectionDefaultPropertyValue(property.getName(), property.getValue());
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(Point point, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean metaDown) {

        if (shiftDown) {
            dragAction = IPlugInPort.DND_TOGGLE_SNAP;
        } else {
            dragAction = 0;
        }

        Map<IDIYComponent, Set<Integer>> components = new HashMap<IDIYComponent, Set<Integer>>();
        this.previousScaledPoint = scalePoint(point);
        if (instantiationManager.getComponentTypeSlot() != null) {
            if (isSnapToGrid()) {
                CalcUtils.snapPointToGrid(previousScaledPoint, getModel().getProject().getGridSpacing());
            }
            boolean refresh = false;
            switch (instantiationManager.getComponentTypeSlot().getCreationMethod()) {
            case POINT_BY_POINT:
                refresh = instantiationManager.updatePointByPoint(previousScaledPoint);
                break;
            case SINGLE_CLICK:
                refresh = instantiationManager.updateSingleClick(previousScaledPoint, isSnapToGrid(), getModel().getProject()
                        .getGridSpacing());
                break;
            }
            if (refresh) {
                getMessageDispatcher().dispatchMessage(EventType.REPAINT);
            }
        } else {
            /*
             * Go backwards so we take the highest z-order components first.
             */
            for (int i = getModel().getProject().getComponents().size() - 1; i >= 0; i--) {
                IDIYComponent component = getModel().getProject().getComponents().get(i);
                ComponentType componentType = ComponentRegistry.INSTANCE.getComponentType(component);

                for (int pointIndex = 0; pointIndex < component.getControlPointCount(); pointIndex++) {
                    Point controlPoint = component.getControlPoint(pointIndex);
                    /*
                     * Only consider selected components that are not grouped.
                     */
                    if (getView().getSelectedComponents().contains(component) && componentType.isStretchable()
                            && findAllGroupedComponents(component).size() == 1) {
                        try {
                            if (previousScaledPoint.distance(controlPoint) < RenderingConstants.CONTROL_POINT_SIZE) {
                                Set<Integer> indices = new HashSet<Integer>();
                                if (componentType.isStretchable()) {
                                    indices.add(pointIndex);
                                } else {
                                    for (int j = 0; j < component.getControlPointCount(); j++) {
                                        indices.add(j);
                                    }
                                }
                                components.put(component, indices);
                                break;
                            }
                        } catch (Exception e) {
                            LOG.warn("Error reading control point for component of type: " + component.getClass().getName());
                        }
                    }
                }
            }
        }

        getMessageDispatcher().dispatchMessage(EventType.MOUSE_MOVED, previousScaledPoint);

        if (!components.equals(controlPointMap)) {
            controlPointMap = components;
            getMessageDispatcher().dispatchMessage(EventType.AVAILABLE_CTRL_POINTS_CHANGED,
                    new HashMap<IDIYComponent, Set<Integer>>(components));
        }
    }

    @Override
    public void dragStarted(Point point, int dragAction) {
        LOG.trace(String.format("dragStarted(%s, %s)", point, dragAction));
        if (instantiationManager.getComponentTypeSlot() != null) {
            LOG.debug("Cannot start drag because a new component is being created.");
            mouseClicked(point, MouseButton.LEFT, dragAction == DnDConstants.ACTION_COPY, dragAction == DnDConstants.ACTION_LINK,
                    dragAction == DnDConstants.ACTION_MOVE, false, 1);
            return;
        }
        this.dragInProgress = true;
        this.dragAction = dragAction;
        this.preDragProject = getModel().getProject().clone();
        Point scaledPoint = scalePoint(point);
        this.previousDragPoint = scaledPoint;
        List<IDIYComponent> components = findComponentsAtScaled(scaledPoint);
        if (!this.controlPointMap.isEmpty()) {
            // If we're dragging control points reset selection.
            getView().updateSelection(new ArrayList<IDIYComponent>(this.controlPointMap.keySet()));
            getMessageDispatcher().dispatchMessage(EventType.REPAINT);
        } else if (components.isEmpty()) {
            // If there are no components are under the cursor, reset selection.
            getView().clearSelection();
            getMessageDispatcher().dispatchMessage(EventType.REPAINT);
        } else {
            // Take the last component, i.e. the top order component.
            IDIYComponent component = components.get(0);
            // If the component under the cursor is not already selected, make
            // it into the only selected component.
            if (!getView().getSelectedComponents().contains(component)) {
                getView().updateSelection(new ArrayList<IDIYComponent>(findAllGroupedComponents(component)));
                getMessageDispatcher().dispatchMessage(EventType.REPAINT);
            }
            // If there aren't any control points, try to add all the selected
            // components with all their control points. That will allow the
            // user to drag the whole components.
            for (IDIYComponent c : getView().getSelectedComponents()) {
                Set<Integer> pointIndices = new HashSet<Integer>();
                if (c.getControlPointCount() > 0) {
                    for (int i = 0; i < c.getControlPointCount(); i++) {
                        pointIndices.add(i);
                    }
                    this.controlPointMap.put(c, pointIndices);
                }
            }
            // Expand control points to include all stuck components.
            boolean sticky = Configuration.INSTANCE.getStickyPoints();
            if (this.dragAction == IPlugInPort.DND_TOGGLE_STICKY) {
                sticky = !sticky;
            }
            if (sticky) {
                getView().includeStuckComponents(controlPointMap);
            }
        }
    }

    @Override
    public void dragActionChanged(int dragAction) {
        LOG.trace("dragActionChanged(" + dragAction + ")");
        this.dragAction = dragAction;
    }

    private boolean isSnapToGrid() {
        boolean snapToGrid = Configuration.INSTANCE.getSnapToGrip();
        if (this.dragAction == IPlugInPort.DND_TOGGLE_SNAP)
            snapToGrid = !snapToGrid;
        return snapToGrid;
    }

    @Override
    public boolean dragOver(Point point) {
        if (point == null) {
            return false;
        }
        Point scaledPoint = scalePoint(point);
        if (!controlPointMap.isEmpty()) {
            // We're dragging control point(s).
            int dx = (scaledPoint.x - previousDragPoint.x);
            int dy = (scaledPoint.y - previousDragPoint.y);

            Point actualD = moveComponents(this.controlPointMap, dx, dy, isSnapToGrid());
            if (actualD == null)
                return true;

            previousDragPoint.translate(actualD.x, actualD.y);
        } else if (getView().getSelectedComponents().isEmpty() && instantiationManager.getComponentTypeSlot() == null) {
            // If there's no selection, the only thing to do is update the
            // selection rectangle and refresh.
            Rectangle oldSelectionRect = selectionRect == null ? null : new Rectangle(selectionRect);
            this.selectionRect = createNormalizedRectangle(scaledPoint, previousDragPoint);
            if (selectionRect.equals(oldSelectionRect)) {
                return true;
            }
            // messageDispatcher.dispatchMessage(EventType.SELECTION_RECT_CHANGED,
            // selectionRect);
        }
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
        return true;
    }

    private Point moveComponents(Map<IDIYComponent, Set<Integer>> controlPointMap, int dx, int dy, boolean snapToGrid) {
        // After we make the transfer and snap to grid, calculate actual dx
        // and dy. We'll use them to translate the previous drag point.
        int actualDx = 0;
        int actualDy = 0;
        // For each component, do a simulation of the move to see if any of
        // them will overlap or go out of bounds.
        int width = (int) getModel().getProject().getWidth().convertToPixels();
        int height = (int) getModel().getProject().getHeight().convertToPixels();

        if (controlPointMap.size() == 1) {
            Map.Entry<IDIYComponent, Set<Integer>> entry = controlPointMap.entrySet().iterator().next();

            Point firstPoint = entry.getKey().getControlPoint(entry.getValue().toArray(new Integer[] {})[0]);
            Point testPoint = new Point(firstPoint);
            testPoint.translate(dx, dy);
            if (snapToGrid) {
                CalcUtils.snapPointToGrid(testPoint, getModel().getProject().getGridSpacing());
            }

            actualDx = testPoint.x - firstPoint.x;
            actualDy = testPoint.y - firstPoint.y;
        } else if (snapToGrid) {
            actualDx = CalcUtils.roundToGrid(dx, getModel().getProject().getGridSpacing());
            actualDy = CalcUtils.roundToGrid(dy, getModel().getProject().getGridSpacing());
        } else {
            actualDx = dx;
            actualDy = dy;
        }

        if (actualDx == 0 && actualDy == 0) {
            // Nothing to move.
            return null;
        }

        // Validate if moving can be done.
        for (Map.Entry<IDIYComponent, Set<Integer>> entry : controlPointMap.entrySet()) {
            IDIYComponent component = entry.getKey();
            Point[] controlPoints = new Point[component.getControlPointCount()];
            for (int index = 0; index < component.getControlPointCount(); index++) {
                controlPoints[index] = new Point(component.getControlPoint(index));
                // When the first point is moved, calculate how much it
                // actually moved after snapping.
                if (entry.getValue().contains(index)) {
                    controlPoints[index].translate(actualDx, actualDy);
                    if (controlPoints[index].x < 0 || controlPoints[index].y < 0 || controlPoints[index].x > width
                            || controlPoints[index].y > height) {
                        // At least one control point went out of bounds.
                        return null;
                    }
                }
                // For control points that may overlap, just write null,
                // we'll ignore them later.
                if (component.canControlPointOverlap(index)) {
                    controlPoints[index] = null;
                }
            }

            for (int i = 0; i < controlPoints.length - 1; i++) {
                for (int j = i + 1; j < controlPoints.length; j++) {
                    if (controlPoints[i] != null && controlPoints[j] != null && controlPoints[i].equals(controlPoints[j])) {
                        // Control points collision detected, cannot make
                        // this move.
                        return null;
                    }
                }
            }
        }

        // Update all points to new location.
        for (Map.Entry<IDIYComponent, Set<Integer>> entry : controlPointMap.entrySet()) {
            IDIYComponent c = entry.getKey();
            getDrawingManager().invalidateComponent(c);
            for (Integer index : entry.getValue()) {
                Point p = new Point(c.getControlPoint(index));
                p.translate(actualDx, actualDy);
                c.setControlPoint(p, index);
            }
        }
        return new Point(actualDx, actualDy);
    }

    @Override
    public void dragEnded(Point point) {
        LOG.trace(String.format("dragEnded(%s)", point));
        if (!dragInProgress) {
            return;
        }
        Point scaledPoint = scalePoint(point);
        if (getView().getSelectedComponents().isEmpty()) {
            // If there's no selection finalize selectionRect and see which
            // components intersect with it.
            if (scaledPoint != null) {
                this.selectionRect = createNormalizedRectangle(scaledPoint, previousDragPoint);
            }
            List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();
            for (IDIYComponent component : getModel().getProject().getComponents()) {
                if (!getModel().isComponentLocked(component)) {
                    Area area = getDrawingManager().getComponentArea(component);
                    if ((area != null) && (selectionRect != null) && area.intersects(selectionRect)) {
                        newSelection.addAll(findAllGroupedComponents(component));
                    }
                }
            }
            selectionRect = null;
            getView().updateSelection(newSelection);
        } else {
            getView().updateSelection(getView().getSelectedComponents());
        }
        // There is selection, so we need to finalize the drag&drop
        // operation.

        if (!preDragProject.equals(getModel().getProject())) {
            getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, preDragProject, getModel().getProject().clone(), "Drag");
            getProjectFileManager().notifyFileChange();
        }
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
        dragInProgress = false;
    }

    @Override
    public void pasteComponents(List<IDIYComponent> components) {
        LOG.trace(String.format("pasteComponents(%s)", components));
        instantiationManager
                .pasteComponents(components, this.previousScaledPoint, isSnapToGrid(), getModel().getProject().getGridSpacing());
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
        getMessageDispatcher().dispatchMessage(EventType.SLOT_CHANGED, instantiationManager.getComponentTypeSlot(),
                instantiationManager.getFirstControlPoint());
    }

    @Override
    public void deleteSelectedComponents() {
        LOG.trace("deleteSelectedComponents()");
        if (getView().getSelectedComponents().isEmpty()) {
            LOG.debug("Nothing to delete");
            return;
        }
        Project oldProject = getModel().getProject().clone();
        // Remove selected components from any groups.
        getView().getController().ungroupSelectedComponents();
        // Remove from area map.
        for (IDIYComponent component : getView().getSelectedComponents()) {
            getDrawingManager().invalidateComponent(component);
        }
        getModel().getProject().getComponents().removeAll(getView().getSelectedComponents());
        getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(), "Delete");
        getProjectFileManager().notifyFileChange();
        getView().clearSelection();
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
    }

    @Override
    public void setSelectionDefaultPropertyValue(String propertyName, Object value) {
        LOG.trace(String.format("setSelectionDefaultPropertyValue(%s, %s)", propertyName, value));
        Map<String, Map<String, Object>> objectProperties = Configuration.INSTANCE.getObjectProperties();
        for (IDIYComponent component : getView().getSelectedComponents()) {
            String className = component.getClass().getName();
            LOG.debug("Default property value set for " + className + ":" + propertyName);
            Map<String, Object> objectValues = objectProperties.get(className);
            if (objectValues != null) {
                objectValues.put(propertyName, value);
            }
        }
        Configuration.INSTANCE.setObjectProperties(objectProperties);
    }

    @Override
    public void setProjectDefaultPropertyValue(String propertyName, Object value) {
        LOG.trace(String.format("setProjectDefaultPropertyValue(%s, %s)", propertyName, value));
        LOG.debug("Default property value set for " + Project.class.getName() + ":" + propertyName);
        Map<String, Object> projectProperties = Configuration.INSTANCE.getProjectProperties();
        projectProperties.put(propertyName, value);
        Configuration.INSTANCE.setProjectProperties(projectProperties);
    }

    @Override
    public void setMetric(boolean isMetric) {
        Configuration.INSTANCE.setMetric(isMetric);
    }

    @Override
    public void setLayerLocked(double layerZOrder, boolean locked) {
        LOG.trace(String.format("setLayerLocked(%s, %s)", layerZOrder, locked));
        if (locked) {
            getModel().getProject().getLockedLayers().add(layerZOrder);
        } else {
            getModel().getProject().getLockedLayers().remove(layerZOrder);
        }
        getView().clearSelection();
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
        getMessageDispatcher().dispatchMessage(EventType.LAYER_STATE_CHANGED, getModel().getProject().getLockedLayers());
    }

    @Override
    public void refresh() {
        LOG.trace("refresh()");
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
    }

    @Override
    public Theme getSelectedTheme() {
        return getDrawingManager().getTheme();
    }

    @Override
    public void setSelectedTheme(Theme theme) {
        getDrawingManager().setTheme(theme);
    }

    @Override
    public void renumberSelectedComponents(final boolean xAxisFirst) {
        LOG.trace("renumberSelectedComponents(" + xAxisFirst + ")");
        if (getView().getSelectedComponents().isEmpty()) {
            return;
        }
        Project oldProject = getModel().getProject().clone();
        List<IDIYComponent> components = new ArrayList<IDIYComponent>(getView().getSelectedComponents());
        // Sort components by their location.
        Collections.sort(components, new Comparator<IDIYComponent>() {

            @Override
            public int compare(IDIYComponent o1, IDIYComponent o2) {
                int sumX1 = 0;
                int sumY1 = 0;
                int sumX2 = 0;
                int sumY2 = 0;
                for (int i = 0; i < o1.getControlPointCount(); i++) {
                    sumX1 += o1.getControlPoint(i).getX();
                    sumY1 += o1.getControlPoint(i).getY();
                }
                for (int i = 0; i < o2.getControlPointCount(); i++) {
                    sumX2 += o2.getControlPoint(i).getX();
                    sumY2 += o2.getControlPoint(i).getY();
                }
                sumX1 /= o1.getControlPointCount();
                sumY1 /= o1.getControlPointCount();
                sumX2 /= o2.getControlPointCount();
                sumY2 /= o2.getControlPointCount();

                if (xAxisFirst) {
                    if (sumY1 < sumY2) {
                        return -1;
                    } else if (sumY1 > sumY2) {
                        return 1;
                    } else {
                        if (sumX1 < sumX2) {
                            return -1;
                        } else if (sumX1 > sumX2) {
                            return 1;
                        }
                    }
                } else {
                    if (sumX1 < sumX2) {
                        return -1;
                    } else if (sumX1 > sumX2) {
                        return 1;
                    } else {
                        if (sumY1 < sumY2) {
                            return -1;
                        } else if (sumY1 > sumY2) {
                            return 1;
                        }
                    }
                }
                return 0;
            }
        });
        // Clear names.
        for (IDIYComponent component : components) {
            component.setName("");
        }
        // Assign new ones.
        for (IDIYComponent component : components) {
            component.setName(instantiationManager.createUniqueName(ComponentRegistry.INSTANCE.getComponentType(component), getModel()
                    .getProject()));
        }

        getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(),
                "Renumber selection");
        getProjectFileManager().notifyFileChange();
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
    }

    /**
     * Finds all components that are grouped with the specified component. This
     * should be called any time components are added or removed from the
     * selection.
     *
     * @param component
     * @return set of all components that belong to the same group with the
     *         specified component. At the minimum, set contains that single
     *         component.
     */
    private Set<IDIYComponent> findAllGroupedComponents(IDIYComponent component) {
        Set<IDIYComponent> components = new HashSet<IDIYComponent>();
        components.add(component);
        for (Set<IDIYComponent> group : getModel().getProject().getGroups()) {
            if (group.contains(component)) {
                components.addAll(group);
                break;
            }
        }
        return components;
    }

    @Override
    public Point2D calculateSelectionDimension() {
        if (getView().getSelectedComponents().isEmpty()) {
            return null;
        }
        boolean metric = Configuration.INSTANCE.getMetric();
        Area area = new Area();
        for (IDIYComponent component : getView().getSelectedComponents()) {
            Area componentArea = getDrawingManager().getComponentArea(component);
            if (componentArea != null) {
                area.add(componentArea);
            } else {
                LOG.warn("No area found for: " + component.getName());
            }
        }
        double width = area.getBounds2D().getWidth();
        double height = area.getBounds2D().getHeight();
        width /= Constants.PIXELS_PER_INCH;
        height /= Constants.PIXELS_PER_INCH;
        if (metric) {
            width *= SizeUnit.in.getFactor() / SizeUnit.cm.getFactor();
            height *= SizeUnit.in.getFactor() / SizeUnit.cm.getFactor();
        }
        Point2D dimension = new Point2D.Double(width, height);
        return dimension;
    }

    /**
     * Adds a component to the project taking z-order into account.
     *
     * @param component
     */
    private void addComponent(IDIYComponent component, boolean canCreatePads) {
        int index = getModel().getProject().getComponents().size();
        while (index > 0
                && ComponentRegistry.INSTANCE.getComponentType(component).getZOrder() < ComponentRegistry.INSTANCE.getComponentType(
                        getModel().getProject().getComponents().get(index - 1)).getZOrder()) {
            index--;
        }
        if (index < getModel().getProject().getComponents().size()) {
            getModel().getProject().getComponents().add(index, component);
        } else {
            getModel().getProject().getComponents().add(component);
        }
        if (canCreatePads && Configuration.INSTANCE.getAutoCreatePads() && !(component instanceof SolderPad)) {
            ComponentType padType = ComponentRegistry.INSTANCE.getComponentType(SolderPad.class);
            for (int i = 0; i < component.getControlPointCount(); i++) {
                if (component.isControlPointSticky(i)) {
                    try {
                        IDIYComponent pad = instantiationManager.instantiateComponent(padType, null, component.getControlPoint(i),
                                getModel().getProject()).get(0);
                        pad.setControlPoint(component.getControlPoint(i), 0);
                        addComponent(pad, false);
                    } catch (Exception e) {
                        LOG.warn("Could not auto-create solder pad", e);
                    }
                    // SolderPad pad = new SolderPad();
                    // pad.setControlPoint(component.getControlPoint(i), 0);
                    // addComponent(pad,
                    // ComponentProcessor.getInstance().extractComponentTypeFrom(
                    // SolderPad.class), false);
                }
            }
        }
    }

    @Override
    public List<PropertyWrapper> getMutualSelectionProperties() {
        try {
            return ComponentProcessor.getInstance().getMutualProperties(getView().getSelectedComponents());
        } catch (Exception e) {
            LOG.error("Could not get mutual selection properties", e);
            return null;
        }
    }

    @Override
    public void applyPropertiesToSelection(List<PropertyWrapper> properties) {
        LOG.trace(String.format("applyPropertiesToSelection(%s)", properties));
        Project oldProject = getModel().getProject().clone();
        try {
            for (IDIYComponent component : getView().getSelectedComponents()) {
                getDrawingManager().invalidateComponent(component);
                for (PropertyWrapper property : properties) {
                    if (property.isChanged()) {
                        property.writeTo(component);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Could not apply selection properties", e);
            getView().showMessage("Could not apply changes to the selection. Check the log for details.", "Error", View.ERROR_MESSAGE);
        } finally {
            // Notify the listeners.
            if (!oldProject.equals(getModel().getProject())) {
                getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(),
                        "Edit Selection");
                getProjectFileManager().notifyFileChange();
            }
        }
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
    }

    @Override
    public void applyPropertyToSelection(PropertyWrapper property) {
        applyPropertiesToSelection(Arrays.asList(new PropertyWrapper[] { property }));
    }

    @Override
    public List<PropertyWrapper> getProjectProperties() {
        List<PropertyWrapper> properties = ComponentProcessor.getInstance().extractProperties(Project.class);
        try {
            for (PropertyWrapper property : properties) {
                property.readFrom(getModel().getProject());
            }
        } catch (Exception e) {
            LOG.error("Could not get project properties", e);
            return null;
        }
        Collections.sort(properties, ComparatorFactory.getInstance().getPropertyNameComparator());
        return properties;
    }

    @Override
    public void applyPropertiesToProject(List<PropertyWrapper> properties) {
        LOG.trace(String.format("applyPropertiesToProject(%s)", properties));
        Project oldProject = getModel().getProject().clone();
        try {
            for (PropertyWrapper property : properties) {
                property.writeTo(getModel().getProject());
            }
        } catch (Exception e) {
            LOG.error("Could not apply project properties", e);
            getView().showMessage("Could not apply changes to the project. Check the log for details.", "Error", View.ERROR_MESSAGE);
        } finally {
            // Notify the listeners.
            if (!oldProject.equals(getModel().getProject())) {
                getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(),
                        "Edit Project");
                getProjectFileManager().notifyFileChange();
            }
            getDrawingManager().fireZoomChanged();
        }
    }

    @Override
    public ComponentType getNewComponentTypeSlot() {
        return instantiationManager.getComponentTypeSlot();
    }

    @Override
    public void setNewComponentTypeSlot(ComponentType componentType, Template template) {
        LOG.trace(String.format("setNewComponentSlot(%s)", componentType == null ? null : componentType.getName()));
        if (componentType != null && componentType.getInstanceClass() == null) {
            LOG.debug("Cannot set new component type slot for type " + componentType.getName());
            setNewComponentTypeSlot(null, null);
            return;
        }
        try {
            instantiationManager.setComponentTypeSlot(componentType, template, getModel().getProject());
            if (componentType != null) {
                getView().clearSelection();
            }
            getMessageDispatcher().dispatchMessage(EventType.REPAINT);
            getMessageDispatcher().dispatchMessage(EventType.SLOT_CHANGED, instantiationManager.getComponentTypeSlot(),
                    instantiationManager.getFirstControlPoint());
        } catch (Exception e) {
            LOG.error("Could not set component type slot", e);
            getView().showMessage("Could not set component type slot. Check log for details.", "Error", View.ERROR_MESSAGE);
        }
    }

    @Override
    public void saveSelectedComponentAsTemplate(String templateName) {
        LOG.trace(String.format("saveSelectedComponentAsTemplate(%s)", templateName));
        if (getView().getSelectedComponents().size() != 1) {
            throw new RuntimeException("Can only save a single component as a template at once.");
        }
        IDIYComponent component = getView().getSelectedComponents().iterator().next();
        ComponentType type = ComponentRegistry.INSTANCE.getComponentType(component);
        Map<String, List<Template>> templateMap = Configuration.INSTANCE.getTemplates();
        if (templateMap == null) {
            templateMap = new HashMap<String, List<Template>>();
        }
        String key = type.getCategory() + "." + type.getName();
        List<Template> templates = templateMap.get(key);
        if (templates == null) {
            templates = new ArrayList<Template>();
            templateMap.put(key, templates);
        }
        List<PropertyWrapper> properties = ComponentProcessor.getInstance().extractProperties(component.getClass());
        Map<String, Object> values = new HashMap<String, Object>();
        for (PropertyWrapper property : properties) {
            if (property.getName().equalsIgnoreCase("name")) {
                continue;
            }
            try {
                property.readFrom(component);
                values.put(property.getName(), property.getValue());
            } catch (Exception e) {
            }
        }
        List<Point> points = new ArrayList<Point>();

        for (int i = 0; i < component.getControlPointCount(); i++) {
            Point p = new Point(component.getControlPoint(i));
            points.add(p);
        }
        int x = points.iterator().next().x;
        int y = points.iterator().next().y;
        for (Point point : points) {
            point.translate(-x, -y);
        }

        Template template = new Template(templateName, values, points);
        boolean exists = false;
        for (Template t : templates) {
            if (t.getName().equalsIgnoreCase(templateName)) {
                exists = true;
                break;
            }
        }

        if (exists) {
            int result = getView().showConfirmDialog("Template with that name already exists. Overwrite?", "Save as Template",
                    View.YES_NO_OPTION, View.WARNING_MESSAGE);
            if (result != View.YES_OPTION) {
                return;
            }
            // Delete the existing template
            Iterator<Template> i = templates.iterator();
            while (i.hasNext()) {
                Template t = i.next();
                if (t.getName().equalsIgnoreCase(templateName)) {
                    i.remove();
                }
            }
        }

        templates.add(template);

        Configuration.INSTANCE.setTemplates(templateMap);
    }

    @Override
    public List<Template> getTemplatesFor(String categoryName, String componentTypeName) {
        Map<String, List<Template>> templateMap = Configuration.INSTANCE.getTemplates();

        if (templateMap != null) {
            return templateMap.get(categoryName + "." + componentTypeName);
        }

        return null;
    }

    @Override
    public List<Template> getTemplatesForSelection() {
        if (getView().getSelectedComponents().isEmpty()) {
            throw new RuntimeException("No components selected");
        }

        ComponentType selectedType = ComponentRegistry.INSTANCE.getComponentType(getView().getSelectedComponents().get(0));

        for (int i = 1; i < getView().getSelectedComponents().size(); i++) {
            ComponentType newType = ComponentRegistry.INSTANCE.getComponentType(getView().getSelectedComponents().get(i));
            if (newType.getInstanceClass() != selectedType.getInstanceClass()) {
                throw new RuntimeException("Template can be applied on multiple components of the same type only");
            }
        }

        return getTemplatesFor(selectedType.getCategory(), selectedType.getName());
    }

    @Override
    public void applyTemplateToSelection(Template template) {
        LOG.trace(String.format("applyTemplateToSelection(%s)", template.getName()));

        Project oldProject = getModel().getProject().clone();

        for (IDIYComponent component : getView().getSelectedComponents()) {
            try {
                getDrawingManager().invalidateComponent(component);
                this.instantiationManager.loadComponentShapeFromTemplate(component, template);
                ModelUtils.fillWithDefaultProperties(component, template);
            } catch (Exception e) {
                LOG.warn("Could not apply templates to " + component.getName(), e);
            }
        }

        // Notify the listeners.
        if (!oldProject.equals(getModel().getProject())) {
            getMessageDispatcher().dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(),
                    "Edit Selection");
            getProjectFileManager().notifyFileChange();
        }
        getMessageDispatcher().dispatchMessage(EventType.REPAINT);
    }

    @Override
    public void deleteTemplate(String categoryName, String componentTypeName, String templateName) {
        Map<String, List<Template>> templateMap = Configuration.INSTANCE.getTemplates();
        if (templateMap != null) {
            List<Template> templates = templateMap.get(categoryName + "." + componentTypeName);
            if (templates != null) {
                Iterator<Template> i = templates.iterator();
                while (i.hasNext()) {
                    Template t = i.next();
                    if (t.getName().equalsIgnoreCase(templateName)) {
                        i.remove();
                    }
                }
            }
        }
    }

    /**
     * Scales point from display base to actual base.
     *
     * @param point
     * @return
     */
    Point scalePoint(Point point) {
        return point == null ? null : new Point((int) (point.x / getDrawingManager().getZoomLevel()), (int) (point.y / getDrawingManager()
                .getZoomLevel()));
    }

    /**
     * Creates a rectangle which opposite corners are lying in the specified
     * points.
     * 
     * @param p1
     * @param p2
     * @return
     */
    private Rectangle createNormalizedRectangle(Point p1, Point p2) {
        int minX = p1.x < p2.x ? p1.x : p2.x;
        int minY = p1.y < p2.y ? p1.y : p2.y;
        int width = Math.abs(p1.x - p2.x);
        int height = Math.abs(p1.y - p2.y);

        return new Rectangle(minX, minY, width, height);
    }

    @Override
    public void sendEvent(EventType eventType, Object... params) {
        getMessageDispatcher().dispatchMessage(eventType, params);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public DrawingManager getDrawingManager() {
        return drawingManager;
    }

    public ProjectFileManager getProjectFileManager() {
        return projectFileManager;
    }

    public MessageDispatcher<EventType> getMessageDispatcher() {
        return messageDispatcher;
    }

}
