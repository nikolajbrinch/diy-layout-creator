package org.diylc.app.view;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.diylc.app.ExpansionMode;
import org.diylc.app.IUndoListener;
import org.diylc.app.UndoHandler;
import org.diylc.app.controllers.DrawingController;
import org.diylc.app.model.ProjectFileManager;
import org.diylc.app.utils.CalcUtils;
import org.diylc.app.utils.StringUtils;
import org.diylc.app.view.rendering.DrawingContext;
import org.diylc.app.view.rendering.DrawingOption;
import org.diylc.app.view.rendering.DrawingRenderer;
import org.diylc.app.view.rendering.RenderingConstants;
import org.diylc.appframework.update.Version;
import org.diylc.appframework.update.VersionNumber;
import org.diylc.components.IComponentFilter;
import org.diylc.components.registry.ComparatorFactory;
import org.diylc.core.components.ComponentFactory;
import org.diylc.core.components.ComponentNameCreator;
import org.diylc.core.components.registry.ComponentRegistry;
import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.CreationMethod;
import org.diylc.core.components.CreationState;
import org.diylc.core.components.IDIYComponent;
import org.diylc.core.Orientation;
import org.diylc.core.OrientationHV;
import org.diylc.core.Project;
import org.diylc.core.components.registry.SpecificationRegistry;
import org.diylc.core.components.Template;
import org.diylc.core.Theme;
import org.diylc.core.config.Configuration;
import org.diylc.core.measures.Size;
import org.diylc.core.measures.SizeUnit;
import org.diylc.core.components.properties.PropertyApplier;
import org.diylc.core.components.properties.PropertyDescriptor;
import org.diylc.core.components.properties.PropertyDescriptorExtractor;
import org.diylc.core.components.properties.PropertyUtils;
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

    private final View view;

    private final DrawingController controller;

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

    public static final String DEFAULTS_KEY_PREFIX = "default.";

    public static final List<IDIYComponent> EMPTY_SELECTION = Collections.emptyList();

    public static final int ICON_SIZE = 32;

    private static final String SOLDER_PAD_COMPONENT_ID = "066375ac-eb5b-4535-83d5-53c91b3f1462";

    private static final ComponentModel clipboardType = new ComponentModel(null, "Clipboard contents", "Components from the clipboard",
            CreationMethod.SINGLE_CLICK, "Multi", "", "", null, null, 0, false, false, null, false, true, true, null);

    private final ComponentFactory componentFactory;
    
    private final PropertyApplier propertyApplier;
    
    private final PropertyUtils propertyUtils;

    private final PropertyDescriptorExtractor propertyDescriptorExtractor;
    
    private Project currentProject;

    /*
     * Maps component class names to ComponentType objects.
     */
    private List<IPlugIn> plugIns;

    private List<IDIYComponent> selectedComponents;

    /*
     * Maps components that have at least one dragged point to set of indices
     * that designate which of their control points are being dragged.
     */
    private Map<IDIYComponent, Set<Integer>> controlPointMap;

    /*
     * Utilities
     */
    private DrawingRenderer drawingRenderer;

    private ProjectFileManager projectFileManager;

    private Rectangle selectionRect;

    // D&D
    private boolean dragInProgress = false;

    /*
     * Previous mouse location, not scaled for zoom factor.
     */
    private Point previousDragPoint = null;

    private Project preDragProject = null;

    private int dragAction;

    private Point previousScaledPoint;

    private HashMap<IDIYComponent, Set<Integer>> availableControlPoints;

    private boolean projectModified = false;

    private final UndoHandler<Project> undoHandler;

    private Path path;

    private boolean isSaved;
    
    private CreationState creationState = new CreationState();

    public Presenter(View view, DrawingController controller, Path path, boolean isSaved) {
        this.view = view;
        this.controller = controller;
        this.path = path;
        this.isSaved = isSaved;
        this.plugIns = new ArrayList<IPlugIn>();
        this.selectedComponents = new ArrayList<IDIYComponent>();
        this.currentProject = new Project();
        this.drawingRenderer = new DrawingRenderer(getView());
        this.projectFileManager = new ProjectFileManager(getComponentRegistry());
        this.propertyDescriptorExtractor = new PropertyDescriptorExtractor(getSpecificationRegistry());
        this.propertyApplier = new PropertyApplier(propertyDescriptorExtractor);
        this.propertyUtils = new PropertyUtils(getComponentRegistry());
        this.componentFactory = new ComponentFactory(propertyApplier);

        this.undoHandler = new UndoHandler<Project>(new IUndoListener<Project>() {

            @Override
            public void actionPerformed(Project currentState) {
                loadProject(currentState, false);
            }
        });

    }

    public void configure() {

    }

    public void installPlugin(IPlugIn plugIn) {
        LOG.trace(String.format("installPlugin(%s)", plugIn.getClass().getSimpleName()));
        plugIns.add(plugIn);
        plugIn.connect(this);
    }

    public void dispose() {
    }

    @Override
    public Double[] getAvailableZoomLevels() {
        return new Double[] { 0.25d, 0.3333d, 0.5d, 0.6667d, 0.75d, 1d, 1.25d, 1.5d, 2d };
    }

    @Override
    public double getZoomLevel() {
        return drawingRenderer.getZoomLevel();
    }

    @Override
    public void setZoomLevel(double zoomLevel) {
        LOG.trace(String.format("setZoomLevel(%s)", zoomLevel));
        if (drawingRenderer.getZoomLevel() == zoomLevel) {
            return;
        }
        drawingRenderer.setZoomLevel(zoomLevel);
    }

    @Override
    public Cursor getCursorAt(Point point) {
        /*
         * Only change the cursor if we're not making a new component.
         */
        if (creationState.getComponentModelSlot() == null) {
            /*
             * Scale point to remove zoom factor.
             */
            Point2D scaledPoint = scalePoint(point);
            if (controlPointMap != null && !controlPointMap.isEmpty()) {
                return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            }
            for (IDIYComponent component : currentProject.getComponents()) {
                if (!isComponentLocked(component)) {
                    Area area = drawingRenderer.getComponentArea(component);
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
        return drawingRenderer.calculateCanvasDimensions(currentProject, drawingRenderer.getZoomLevel(), useZoom);
    }

    @Override
    public Project getCurrentProject() {
        return currentProject;
    }

    @Override
    public void createNewProject() {
        LOG.trace("createNewFile()");
        try {
            Project project = new Project();
            propertyApplier.applyDefaultProperties(project, null);
            loadProject(project, true);
        } catch (Exception e) {
            LOG.error("Could not create new file", e);
            view.showMessage("Could not create a new file. Check the log for details.", "Error", View.ERROR_MESSAGE);
        }
    }

    @Override
    public void loadProject(Project project, boolean freshStart) {
        LOG.trace(String.format("loadProject(%s, %s)", project.getTitle(), freshStart));
        this.currentProject = project;
        drawingRenderer.clearComponentAreaMap();
        updateSelection(EMPTY_SELECTION);
        getView().initCanvas(project, freshStart);
        getView().repaintCanvas();
        getView().updateLockedLayers();
    }

    @Override
    public boolean allowFileAction() {
        boolean response = true;

        if (isProjectModified()) {
            int dialogResponse = view.showConfirmDialog("There are unsaved changes. Would you like to save them?", "Warning",
                    View.YES_NO_CANCEL_OPTION, View.WARNING_MESSAGE);

            if (dialogResponse == View.YES_OPTION) {
                if (this.getCurrentFile() == null) {
                    Path path = view.promptFileSave();

                    if (path == null) {
                        response = false;
                    } else {
                        saveProjectToFile(path, false);
                    }
                } else {
                    saveProjectToFile(this.getCurrentFile(), false);
                }
            } else {
                response = dialogResponse != View.CANCEL_OPTION && dialogResponse != View.CLOSED_OPTION;
            }
        }

        return response;
    }

    @Override
    public void saveProjectToFile(Path path, boolean isBackup) {
        LOG.trace(String.format("saveProjectToFile(%s)", path.toAbsolutePath()));
        try {
            currentProject.setFileVersion(CURRENT_VERSION);
            projectFileManager.serializeProjectToFile(currentProject, path);
            setProjectModified(false);
            this.path = path;
            setSaved(true);
            if (!isBackup) {
                Configuration.INSTANCE.setLastPath(path.getParent());
            }
            getView().updateTitle();
        } catch (Exception ex) {
            LOG.error("Could not save file", ex);
            if (!isBackup) {
                view.showMessage("Could not save file " + path.toAbsolutePath() + ". Check the log for details.", "Error",
                        View.ERROR_MESSAGE);
            }
        }
    }

    private void setSaved(boolean saved) {
        this.isSaved = saved;
    }

    @Override
    public Path getCurrentFile() {
        return path;
    }

    @Override
    public boolean isProjectModified() {
        return projectModified;
    }

    @Override
    public void draw(Graphics2D g2d, Set<DrawingOption> drawOptions, IComponentFilter filter) {
        if (currentProject == null) {
            return;
        }
        Set<IDIYComponent> groupedComponents = new HashSet<IDIYComponent>();

        for (IDIYComponent component : currentProject.getComponents()) {
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

        if (creationState.getFirstControlPoint() != null && creationState.getPotentialControlPoint() != null
                && creationState.getFirstControlPoint().equals(creationState.getPotentialControlPoint())) {
            componentSlotToDraw = null;
        } else {
            componentSlotToDraw = creationState.getComponentSlot();
        }

        List<IDIYComponent> failedComponents = new ArrayList<IDIYComponent>();

        if (currentProject != null) {
            DrawingContext drawingContext = new DrawingContext(g2d, currentProject, drawOptions, filter, selectionRect, selectedComponents,
                    getLockedComponents(), groupedComponents, Arrays.asList(creationState.getFirstControlPoint(),
                            creationState.getPotentialControlPoint()), componentSlotToDraw, dragInProgress);

            failedComponents = drawingRenderer.drawProject(drawingContext);
        }

        List<String> failedComponentNames = new ArrayList<String>();

        for (IDIYComponent component : failedComponents) {
            failedComponentNames.add(component.getName());
        }

        Collections.sort(failedComponentNames);

        if (!failedComponentNames.isEmpty()) {
            getView().updateStatusBar(
                    "<html><font color='red'>Failed to draw components: " + StringUtils.toCommaString(failedComponentNames)
                            + "</font></html>");
        } else {
            getView().updateStatusBar("");
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
        List<IDIYComponent> components = drawingRenderer.findComponentsAt(point, currentProject);
        Iterator<IDIYComponent> iterator = components.iterator();

        while (iterator.hasNext()) {
            if (isComponentLocked(iterator.next())) {
                iterator.remove();
            }
        }

        return components;
    }

    @Override
    public List<IDIYComponent> findComponentsAt(Point point) {
        Point scaledPoint = scalePoint(point);
        List<IDIYComponent> components = findComponentsAtScaled(scaledPoint);

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
            if (creationState.getComponentModelSlot() != null) {
                /*
                 * Try to rotate the component on right click while creating.
                 */
                if (button != MouseButton.LEFT) {
                    if (creationState.getComponentSlot() == null) {
                        LOG.debug("Component slot is empty, cannot rotate");
                        return;
                    }
                    
                    List<PropertyDescriptor> properties = creationState.getComponentModelSlot().getPropertyDescriptors();
                    PropertyDescriptor angleProperty = null;
                    for (PropertyDescriptor propertyWrapper : properties) {
                        if (propertyWrapper.getType().getName().equals(Orientation.class.getName())
                                || propertyWrapper.getType().getName().equals(OrientationHV.class.getName())) {
                            angleProperty = propertyWrapper;
                            break;
                        }
                    }
                    if (angleProperty == null) {
                        LOG.debug("Component in the slot does not have a property of type Orientation, cannot rotate");
                        return;
                    }
                    try {
                        for (IDIYComponent component : creationState.getComponentSlot()) {
                            angleProperty.readFrom(component);
                            Object value = angleProperty.getValue();
                            if (value instanceof Orientation) {
                                angleProperty.setValue(Orientation.values()[(((Orientation) value).ordinal() + 1) % Orientation.values().length]);
                            } else if (value instanceof OrientationHV) {
                                angleProperty.setValue(OrientationHV.values()[(((OrientationHV) value).ordinal() + 1) % OrientationHV.values().length]);
                            }
                            angleProperty.writeTo(component);
                        }
                    } catch (Exception e) {
                        LOG.warn("Error trying to rotate the component", e);
                    }                    
                    
                    getView().repaintCanvas();
                    return;
                }

                /*
                 * Keep the reference to component type for later.
                 */
                ComponentModel componentModelSlot = creationState.getComponentModelSlot();
                Template template = creationState.getTemplate();
                Project oldProject = currentProject.clone();

                switch (componentModelSlot.getCreationMethod()) {
                case SINGLE_CLICK:
                    try {
                        if (isSnapToGrid()) {
                            CalcUtils.snapPointToGrid(scaledPoint, currentProject.getGridSpacing());
                        }

                        List<IDIYComponent> componentSlot = creationState.getComponentSlot();
                        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();

                        for (IDIYComponent component : componentSlot) {
                            addComponent(component, true);
                            newSelection.add(component);
                        }

                        /*
                         * Select the new component
                         */
                        getView().repaintCanvas();
                        updateSelection(newSelection);
                    } catch (Exception e) {
                        LOG.error("Error instatiating component of type: " + componentModelSlot.getComponentClass().getName(), e);
                    }

                    if (componentModelSlot.isAutoEdit() && Configuration.INSTANCE.getAutoEdit()) {
                        editSelection();
                    }
                    if (Configuration.INSTANCE.getContinuousCreation()) {
                        setNewComponentTypeSlot(componentModelSlot, template);
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
                        CalcUtils.snapPointToGrid(scaledPoint, currentProject.getGridSpacing());
                    }
                    if (creationState.getComponentSlot() == null) {
                        try {
                            componentFactory.createComponentPointByPoint(creationState, scaledPoint, currentProject);
                        } catch (Exception e) {
                            view.showMessage("Could not create component. Check log for details.", "Error", View.ERROR_MESSAGE);
                            LOG.error("Could not create component", e);
                        }
                        getView().updateStatusBar();
                        getView().repaintCanvas();
                    } else {
                        /*
                         * On the second click, add the component to the
                         * project.
                         */
                        List<IDIYComponent> componentSlot = creationState.getComponentSlot();
                        componentSlot.get(0).setControlPoint(scaledPoint, 1);
                        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();

                        for (IDIYComponent component : componentSlot) {
                            addComponent(component, true);
                            /*
                             * Select the new component if it's not locked.
                             */
                            if (!isComponentLocked(component)) {
                                newSelection.add(component);
                            }
                        }

                        updateSelection(newSelection);
                        getView().repaintCanvas();

                        if (componentModelSlot.isAutoEdit() && Configuration.INSTANCE.getAutoEdit()) {
                            editSelection();
                        }
                        if (Configuration.INSTANCE.getContinuousCreation()) {
                            setNewComponentTypeSlot(componentModelSlot, template);
                        } else {
                            setNewComponentTypeSlot(null, null);
                        }
                    }
                    break;
                default:
                    LOG.error("Unknown creation method: " + componentModelSlot.getCreationMethod());
                }

                /*
                 * Notify the listeners.
                 */
                if (!oldProject.equals(currentProject)) {
                    registerProjectModification(oldProject, currentProject.clone(), "Add " + componentModelSlot.getName());
                }
            } else {
                List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>(selectedComponents);
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

                updateSelection(newSelection);

                getView().repaintCanvas();
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
        for (IDIYComponent c : selectedComponents) {
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
                oldProject = currentProject.clone();
                rotateComponents(this.selectedComponents, 1, snapToGrid);
            } else if (key == IKeyProcessor.VK_LEFT) {
                oldProject = currentProject.clone();
                rotateComponents(this.selectedComponents, -1, snapToGrid);
            } else {
                return false;
            }

            registerProjectModification(oldProject, currentProject.clone(), "Rotate Selection");
            getView().repaintCanvas();

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
            includeStuckComponents(controlPointMap);
        }

        int d;

        if (snapToGrid) {
            d = (int) currentProject.getGridSpacing().convertToPixels();
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

        Project oldProject = currentProject.clone();
        moveComponents(controlPointMap, dx, dy, snapToGrid);
        registerProjectModification(oldProject, currentProject.clone(), "Move Selection");
        getView().repaintCanvas();

        return true;
    }

    @Override
    public void editSelection() {
        List<PropertyDescriptor> properties = getMutualSelectionProperties();

        if (properties != null && !properties.isEmpty()) {
            Set<PropertyDescriptor> defaultedProperties = new HashSet<PropertyDescriptor>();
            boolean edited = view.editProperties(properties, defaultedProperties);

            if (edited) {
                try {
                    applyPropertiesToSelection(properties);
                } catch (Exception e1) {
                    view.showMessage("Error occured while editing selection. Check the log for details.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    LOG.error("Error applying properties", e1);
                }

                /*
                 * Save default values.
                 */
                for (PropertyDescriptor property : defaultedProperties) {
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
        if (creationState.getComponentModelSlot() != null) {
            if (isSnapToGrid()) {
                CalcUtils.snapPointToGrid(previousScaledPoint, currentProject.getGridSpacing());
            }
            boolean refresh = false;
            switch (creationState.getComponentModelSlot().getCreationMethod()) {
            case POINT_BY_POINT:
                refresh =  !previousScaledPoint.equals(creationState.getPotentialControlPoint());
                creationState.setPotentialControlPoint(previousScaledPoint);
                if (creationState.getComponentSlot() != null && !creationState.getComponentSlot().isEmpty()) {
                    creationState.getComponentSlot().get(0).setControlPoint(previousScaledPoint, 1);
                }
                break;
            case SINGLE_CLICK:
                refresh = updateSingleClick(previousScaledPoint, isSnapToGrid(), currentProject.getGridSpacing());
                break;
            }
            if (refresh) {
                getView().repaintCanvas();
            }
        } else {
            /*
             * Go backwards so we take the highest z-order components first.
             */
            for (int i = currentProject.getComponents().size() - 1; i >= 0; i--) {
                IDIYComponent component = currentProject.getComponents().get(i);
                ComponentModel componentModel = component.getComponentModel();

                for (int pointIndex = 0; pointIndex < component.getControlPointCount(); pointIndex++) {
                    Point controlPoint = component.getControlPoint(pointIndex);
                    /*
                     * Only consider selected components that are not grouped.
                     */
                    if (selectedComponents.contains(component) && componentModel.isStretchable()
                            && findAllGroupedComponents(component).size() == 1) {
                        try {
                            if (previousScaledPoint.distance(controlPoint) < RenderingConstants.CONTROL_POINT_SIZE) {
                                Set<Integer> indices = new HashSet<Integer>();
                                if (componentModel.isStretchable()) {
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

        if (!components.equals(controlPointMap)) {
            controlPointMap = components;
            availableControlPoints = new HashMap<IDIYComponent, Set<Integer>>(components);
            getView().updateStatusBar();
        }
    }
    
    public ComponentRegistry getComponentRegistry() {
        return view.getController().getComponentRegistry();
    }

    public SpecificationRegistry getSpecificationRegistry() {
        return view.getController().getSpecificationRegistry();
    }

    @Override
    public List<IDIYComponent> getSelectedComponents() {
        return selectedComponents;
    }

    @Override
    public void selectAll(double layer) {
        LOG.trace("selectAll()");
        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>(currentProject.getComponents());
        newSelection.removeAll(getLockedComponents());
        if (layer > 0) {
            Iterator<IDIYComponent> iterator = newSelection.iterator();
            while (iterator.hasNext()) {
                IDIYComponent component = iterator.next();
                ComponentModel type = component.getComponentModel();
                if ((double) type.getZOrder() != layer)
                    iterator.remove();
            }
        }
        updateSelection(newSelection);
        getView().repaintCanvas();
    }

    @Override
    public VersionNumber getCurrentVersionNumber() {
        return CURRENT_VERSION;
    }

    @Override
    public void dragStarted(Point point, int dragAction) {
        LOG.trace(String.format("dragStarted(%s, %s)", point, dragAction));
        if (creationState.getComponentModelSlot() != null) {
            LOG.debug("Cannot start drag because a new component is being created.");
            mouseClicked(point, MouseButton.LEFT, dragAction == DnDConstants.ACTION_COPY, dragAction == DnDConstants.ACTION_LINK,
                    dragAction == DnDConstants.ACTION_MOVE, false, 1);
            return;
        }
        this.dragInProgress = true;
        this.dragAction = dragAction;
        this.preDragProject = currentProject.clone();
        Point scaledPoint = scalePoint(point);
        this.previousDragPoint = scaledPoint;
        List<IDIYComponent> components = findComponentsAtScaled(scaledPoint);
        if (!this.controlPointMap.isEmpty()) {
            // If we're dragging control points reset selection.
            updateSelection(new ArrayList<IDIYComponent>(this.controlPointMap.keySet()));
            getView().repaintCanvas();
        } else if (components.isEmpty()) {
            // If there are no components are under the cursor, reset selection.
            updateSelection(EMPTY_SELECTION);
            getView().repaintCanvas();
        } else {
            // Take the last component, i.e. the top order component.
            IDIYComponent component = components.get(0);
            // If the component under the cursor is not already selected, make
            // it into the only selected component.
            if (!selectedComponents.contains(component)) {
                updateSelection(new ArrayList<IDIYComponent>(findAllGroupedComponents(component)));
                getView().repaintCanvas();
            }
            // If there aren't any control points, try to add all the selected
            // components with all their control points. That will allow the
            // user to drag the whole components.
            for (IDIYComponent c : selectedComponents) {
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
                includeStuckComponents(controlPointMap);
            }
        }
    }

    @Override
    public void dragActionChanged(int dragAction) {
        LOG.trace("dragActionChanged(" + dragAction + ")");
        this.dragAction = dragAction;
    }

    /**
     * Finds any components that are stuck to one of the components already in
     * the map.
     *
     * @param controlPointMap
     */
    private void includeStuckComponents(Map<IDIYComponent, Set<Integer>> controlPointMap) {
        int oldSize = controlPointMap.size();
        LOG.trace("Expanding selected component map");
        for (IDIYComponent component : currentProject.getComponents()) {
            ComponentModel componentModel = component.getComponentModel();

            // Check if there's a control point in the current selection
            // that matches with one of its control points.
            for (int i = 0; i < component.getControlPointCount(); i++) {
                // Do not process a control point if it's already in the map and
                // if it's locked.
                if ((!controlPointMap.containsKey(component) || !controlPointMap.get(component).contains(i))
                        && !isComponentLocked(component)) {
                    if (component.isControlPointSticky(i)) {
                        boolean componentMatches = false;
                        for (Map.Entry<IDIYComponent, Set<Integer>> entry : controlPointMap.entrySet()) {
                            if (componentMatches) {
                                break;
                            }
                            for (Integer j : entry.getValue()) {
                                Point firstPoint = component.getControlPoint(i);
                                if (entry.getKey().isControlPointSticky(j)) {
                                    Point secondPoint = entry.getKey().getControlPoint(j);
                                    // If they are close enough we can consider
                                    // them matched.
                                    if (firstPoint.distance(secondPoint) < RenderingConstants.CONTROL_POINT_SIZE) {
                                        componentMatches = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (componentMatches) {
                            LOG.trace("Including component: " + component);
                            Set<Integer> indices = new HashSet<Integer>();
                            // For stretchable components just add the
                            // matching component. Otherwise, add all control
                            // points.
                            if (componentModel.isStretchable()) {
                                indices.add(i);
                            } else {
                                for (int k = 0; k < component.getControlPointCount(); k++) {
                                    indices.add(k);
                                }
                            }
                            if (controlPointMap.containsKey(component)) {
                                controlPointMap.get(component).addAll(indices);
                            } else {
                                controlPointMap.put(component, indices);
                            }
                        }
                    }
                }
            }
        }
        int newSize = controlPointMap.size();
        // As long as we're adding new components, do another iteration.
        if (newSize > oldSize) {
            LOG.trace("Component count changed, trying one more time.");
            includeStuckComponents(controlPointMap);
        } else {
            LOG.trace("Component count didn't change, done with expanding.");
        }
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
        } else if (selectedComponents.isEmpty() && creationState.getComponentModelSlot() == null) {
            // If there's no selection, the only thing to do is update the
            // selection rectangle and refresh.
            Rectangle oldSelectionRect = selectionRect == null ? null : new Rectangle(selectionRect);
            this.selectionRect = createNormalizedRectangle(scaledPoint, previousDragPoint);
            if (selectionRect.equals(oldSelectionRect)) {
                return true;
            }
        }
        getView().repaintCanvas();
        return true;
    }

    private Point moveComponents(Map<IDIYComponent, Set<Integer>> controlPointMap, int dx, int dy, boolean snapToGrid) {
        // After we make the transfer and snap to grid, calculate actual dx
        // and dy. We'll use them to translate the previous drag point.
        int actualDx = 0;
        int actualDy = 0;
        // For each component, do a simulation of the move to see if any of
        // them will overlap or go out of bounds.
        int width = (int) currentProject.getWidth().convertToPixels();
        int height = (int) currentProject.getHeight().convertToPixels();

        if (controlPointMap.size() == 1) {
            Map.Entry<IDIYComponent, Set<Integer>> entry = controlPointMap.entrySet().iterator().next();

            Point firstPoint = entry.getKey().getControlPoint(entry.getValue().toArray(new Integer[] {})[0]);
            Point testPoint = new Point(firstPoint);
            testPoint.translate(dx, dy);
            if (snapToGrid) {
                CalcUtils.snapPointToGrid(testPoint, currentProject.getGridSpacing());
            }

            actualDx = testPoint.x - firstPoint.x;
            actualDy = testPoint.y - firstPoint.y;
        } else if (snapToGrid) {
            actualDx = CalcUtils.roundToGrid(dx, currentProject.getGridSpacing());
            actualDy = CalcUtils.roundToGrid(dy, currentProject.getGridSpacing());
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
            drawingRenderer.invalidateComponent(c);
            for (Integer index : entry.getValue()) {
                Point p = new Point(c.getControlPoint(index));
                p.translate(actualDx, actualDy);
                c.setControlPoint(p, index);
            }
        }
        return new Point(actualDx, actualDy);
    }

    @Override
    public void rotateSelection(int direction) {
        if (!selectedComponents.isEmpty()) {
            Project oldProject = currentProject.clone();
            rotateComponents(this.selectedComponents, direction, isSnapToGrid());
            registerProjectModification(oldProject, currentProject.clone(), "Rotate Selection");
            getView().repaintCanvas();
        }
    }

    /**
     * @param direction
     *            1 for clockwise, -1 for counter-clockwise
     */
    private void rotateComponents(List<IDIYComponent> components, int direction, boolean snapToGrid) {
        Point center = getCenterOf(components, snapToGrid);

        AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 2 * direction, center.x, center.y);

        // Update all points to new location.
        for (IDIYComponent component : components) {
            drawingRenderer.invalidateComponent(component);
            ComponentModel type = component.getComponentModel();
            if (type.isRotatable()) {
                for (int index = 0; index < component.getControlPointCount(); index++) {
                    Point p = new Point(component.getControlPoint(index));
                    rotate.transform(p, p);
                    component.setControlPoint(p, index);
                }
                // If component has orientation, change it too
                List<PropertyDescriptor> newProperties = type.getPropertyDescriptors();
                for (PropertyDescriptor property : newProperties) {
                    if (property.getType() == Orientation.class) {
                        try {
                            property.readFrom(component);
                            Orientation orientation = (Orientation) property.getValue();
                            Orientation[] values = Orientation.values();
                            int newIndex = orientation.ordinal() + direction;
                            if (newIndex < 0)
                                newIndex = values.length - 1;
                            else if (newIndex >= values.length)
                                newIndex = 0;
                            property.setValue(values[newIndex]);
                            property.writeTo(component);
                        } catch (Exception e) {
                            LOG.error("Could not change component orientation for " + component.getName(), e);
                        }
                    } else if (property.getType() == OrientationHV.class) {
                        try {
                            property.readFrom(component);
                            OrientationHV orientation = (OrientationHV) property.getValue();
                            property.setValue(OrientationHV.values()[1 - orientation.ordinal()]);
                            property.writeTo(component);
                        } catch (Exception e) {
                            LOG.error("Could not change component orientation for " + component.getName(), e);
                        }
                    }
                }
            } else {
                // Non-rotatable
                Point componentCenter = getCenterOf(Arrays.asList(new IDIYComponent[] { component }), false);
                Point rotatedComponentCenter = new Point();
                rotate.transform(componentCenter, rotatedComponentCenter);
                for (int index = 0; index < component.getControlPointCount(); index++) {
                    Point p = new Point(component.getControlPoint(index));
                    p.translate(rotatedComponentCenter.x - componentCenter.x, rotatedComponentCenter.y - componentCenter.y);
                    component.setControlPoint(p, index);
                }
            }
        }
    }

    private Point getCenterOf(List<IDIYComponent> components, boolean snapToGrid) {
        // Determine center of rotation
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (IDIYComponent component : components) {
            for (int i = 0; i < component.getControlPointCount(); i++) {
                Point p = component.getControlPoint(i);
                if (minX > p.x) {
                    minX = p.x;
                }
                if (maxX < p.x) {
                    maxX = p.x;
                }
                if (minY > p.y) {
                    minY = p.y;
                }
                if (maxY < p.y) {
                    maxY = p.y;
                }
            }
        }
        int centerX = (maxX + minX) / 2;
        int centerY = (maxY + minY) / 2;

        if (snapToGrid) {
            CalcUtils.roundToGrid(centerX, this.currentProject.getGridSpacing());
            CalcUtils.roundToGrid(centerY, this.currentProject.getGridSpacing());
        }

        return new Point(centerX, centerY);
    }

    @Override
    public void dragEnded(Point point) {
        LOG.trace(String.format("dragEnded(%s)", point));
        if (!dragInProgress) {
            return;
        }
        Point scaledPoint = scalePoint(point);
        if (selectedComponents.isEmpty()) {
            /*
             * If there's no selection finalize selectionRect and see which
             * components intersect with it.
             */
            if (scaledPoint != null) {
                this.selectionRect = createNormalizedRectangle(scaledPoint, previousDragPoint);
            }
            List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>();
            for (IDIYComponent component : currentProject.getComponents()) {
                if (!isComponentLocked(component)) {
                    Area area = drawingRenderer.getComponentArea(component);
                    if ((area != null) && (selectionRect != null) && area.intersects(selectionRect)) {
                        newSelection.addAll(findAllGroupedComponents(component));
                    }
                }
            }
            selectionRect = null;
            updateSelection(newSelection);
        } else {
            updateSelection(selectedComponents);
        }
        /*
         * There is selection, so we need to finalize the drag&drop operation.
         */
        if (!preDragProject.equals(currentProject)) {
            registerProjectModification(preDragProject, currentProject.clone(), "Drag");
        }
        getView().repaintCanvas();
        dragInProgress = false;
    }

    @Override
    public void pasteComponents(List<IDIYComponent> components) {
        LOG.trace(String.format("pasteComponents(%s)", components));

        /*
         * Adjust location of components so they are centered under the mouse
         * cursor
         */
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (IDIYComponent component : components) {
            for (int i = 0; i < component.getControlPointCount(); i++) {
                Point p = component.getControlPoint(i);
                if (p.x > maxX) {
                    maxX = p.x;
                }
                if (p.x < minX) {
                    minX = p.x;
                }
                if (p.y > maxY) {
                    maxY = p.y;
                }
                if (p.y < minY) {
                    minY = p.y;
                }
            }
        }
        int x = minX;
        int y = minY;
        if (isSnapToGrid()) {
            x = CalcUtils.roundToGrid(x, currentProject.getGridSpacing());
            x = CalcUtils.roundToGrid(x, currentProject.getGridSpacing());
        }
        for (IDIYComponent component : components) {
            for (int i = 0; i < component.getControlPointCount(); i++) {
                Point p = component.getControlPoint(i);
                p.translate(-x, -y);
                component.setControlPoint(p, i);
            }
        }

        /*
         * Update component slot
         */
        creationState.setComponentSlot(new ArrayList<IDIYComponent>(components));

        /*
         * Update the component type slot so the app knows that something's
         * being instantiated.
         */
        creationState.setComponentModelSlot(clipboardType);

        if (isSnapToGrid()) {
            this.previousScaledPoint = new Point(this.previousScaledPoint);
            CalcUtils.snapPointToGrid(this.previousScaledPoint, currentProject.getGridSpacing());
        }
        /*
         * Update the location according to mouse location
         */
        updateSingleClick(this.previousScaledPoint, isSnapToGrid(), currentProject.getGridSpacing());        
        
        getView().repaintCanvas();
        getView().updateStatusBar();
    }

    /**
     * Updates location of component slot based on the new mouse location.
     * 
     * @param scaledPoint
     * @param snapToGrid
     * @param gridSpacing
     * @return true if we need to refresh the canvas
     */
    private boolean updateSingleClick(Point scaledPoint, boolean snapToGrid, Size gridSpacing) {
        if (creationState.getPotentialControlPoint() == null) {
            creationState.setPotentialControlPoint(new Point(0, 0));
        }
        if (scaledPoint == null) {
            scaledPoint = new Point(0, 0);
        }
        int dx = scaledPoint.x - creationState.getPotentialControlPoint().x;
        int dy = scaledPoint.y - creationState.getPotentialControlPoint().y;
        if (snapToGrid) {
            dx = CalcUtils.roundToGrid(dx, gridSpacing);
            dy = CalcUtils.roundToGrid(dy, gridSpacing);
        }
        
        /* 
         * Only repaint if there's an actual change.
         */
        if (dx == 0 && dy == 0) {
            return false;
        }
        creationState.getPotentialControlPoint().translate(dx, dy);
        if (creationState.getComponentSlot() == null) {
            LOG.error("Component slot should not be null!");
        } else {
            Point p = new Point();
            for (IDIYComponent component : creationState.getComponentSlot()) {
                for (int i = 0; i < component.getControlPointCount(); i++) {
                    p.setLocation(component.getControlPoint(i));
                    p.translate(dx, dy);
                    if (snapToGrid) {
                        CalcUtils.snapPointToGrid(p, gridSpacing);
                    }
                    component.setControlPoint(p, i);
                }
            }
        }
        return true;
    }
    
    @Override
    public Point getFirstControlPoint() {
        return creationState.getFirstControlPoint();
    }

    @Override
    public void deleteSelectedComponents() {
        LOG.trace("deleteSelectedComponents()");
        if (selectedComponents.isEmpty()) {
            LOG.debug("Nothing to delete");
            return;
        }
        Project oldProject = currentProject.clone();

        /*
         * Remove selected components from any groups.
         */
        ungroupComponents(selectedComponents);

        /*
         * Remove from area map.
         */
        for (IDIYComponent component : selectedComponents) {
            drawingRenderer.invalidateComponent(component);
        }
        currentProject.getComponents().removeAll(selectedComponents);
        registerProjectModification(oldProject, currentProject.clone(), "Delete");
        updateSelection(EMPTY_SELECTION);
        getView().repaintCanvas();
    }

    @Override
    public void setSelectionDefaultPropertyValue(String propertyName, Object value) {
        LOG.trace(String.format("setSelectionDefaultPropertyValue(%s, %s)", propertyName, value));
        Map<String, Map<String, Object>> objectProperties = Configuration.INSTANCE.getObjectProperties();
        for (IDIYComponent component : selectedComponents) {
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
    public void groupSelectedComponents() {
        LOG.trace("groupSelectedComponents()");
        Project oldProject = currentProject.clone();
        /*
         * First remove the selected components from other groups.
         */
        ungroupComponents(selectedComponents);
        /*
         * Then group them together.
         */
        currentProject.getGroups().add(new HashSet<IDIYComponent>(selectedComponents));

        /*
         * Notify the listeners.
         */
        getView().repaintCanvas();
        if (!oldProject.equals(currentProject)) {
            registerProjectModification(oldProject, currentProject.clone(), "Group");
        }
    }

    @Override
    public void ungroupSelectedComponents() {
        LOG.trace("ungroupSelectedComponents()");
        Project oldProject = currentProject.clone();
        ungroupComponents(selectedComponents);

        /*
         * Notify the listeners.
         */
        getView().repaintCanvas();
        if (!oldProject.equals(currentProject)) {
            registerProjectModification(oldProject, currentProject.clone(), "Ungroup");
        }
    }

    @Override
    public void setLayerLocked(double layerZOrder, boolean locked) {
        LOG.trace(String.format("setLayerLocked(%s, %s)", layerZOrder, locked));
        if (locked) {
            currentProject.getLockedLayers().add(layerZOrder);
        } else {
            currentProject.getLockedLayers().remove(layerZOrder);
        }
        updateSelection(EMPTY_SELECTION);
        getView().repaintCanvas();
        getView().updateLockedLayers();
    }

    @Override
    public void sendSelectionToBack() {
        LOG.trace("sendSelectionToBack()");
        Project oldProject = currentProject.clone();
        for (IDIYComponent component : selectedComponents) {
            ComponentModel componentModel = component.getComponentModel();
            int index = currentProject.getComponents().indexOf(component);
            if (index < 0) {
                LOG.warn("Component not found in the project: " + component.getName());
            } else
                while (index > 0) {
                    IDIYComponent componentBefore = currentProject.getComponents().get(index - 1);
                    ComponentModel componentBeforeType = componentBefore.getComponentModel();
                    if (!componentModel.isFlexibleZOrder() && componentBeforeType.getZOrder() < componentModel.getZOrder())
                        break;
                    Collections.swap(currentProject.getComponents(), index, index - 1);
                    index--;
                }
        }
        if (!oldProject.equals(currentProject)) {
            registerProjectModification(oldProject, currentProject.clone(), "Send to Back");
            getView().repaintCanvas();
        }
    }

    @Override
    public void bringSelectionToFront() {
        LOG.trace("bringSelectionToFront()");
        Project oldProject = currentProject.clone();
        for (IDIYComponent component : selectedComponents) {
            ComponentModel componentModel = component.getComponentModel();
            int index = currentProject.getComponents().indexOf(component);
            if (index < 0) {
                LOG.warn("Component not found in the project: " + component.getName());
            } else
                while (index < currentProject.getComponents().size() - 1) {
                    IDIYComponent componentAfter = currentProject.getComponents().get(index + 1);
                    ComponentModel componentAfterType = componentAfter.getComponentModel();
                    if (!componentModel.isFlexibleZOrder() && componentAfterType.getZOrder() > componentModel.getZOrder())
                        break;
                    Collections.swap(currentProject.getComponents(), index, index + 1);
                    index++;
                }
        }
        if (!oldProject.equals(currentProject)) {
            registerProjectModification(oldProject, currentProject.clone(), "Bring to Front");
            getView().repaintCanvas();
        }
    }

    @Override
    public void refresh() {
        LOG.trace("refresh()");
        getView().repaintCanvas();
    }

    @Override
    public Theme getSelectedTheme() {
        return drawingRenderer.getTheme();
    }

    @Override
    public void setSelectedTheme(Theme theme) {
        drawingRenderer.setTheme(theme);
    }

    @Override
    public void renumberSelectedComponents(final boolean xAxisFirst) {
        LOG.trace("renumberSelectedComponents(" + xAxisFirst + ")");
        if (getSelectedComponents().isEmpty()) {
            return;
        }
        Project oldProject = currentProject.clone();
        List<IDIYComponent> components = new ArrayList<IDIYComponent>(getSelectedComponents());
        /*
         * Sort components by their location.
         */
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
        /*
         * Clear names.
         */
        for (IDIYComponent component : components) {
            component.setName("");
        }
        /*
         * Assign new ones.
         */
        for (IDIYComponent component : components) {
            component.setName(ComponentNameCreator.createComponentName(component.getComponentModel(), currentProject));
        }

        registerProjectModification(oldProject, currentProject.clone(), "Renumber selection");
        getView().repaintCanvas();
    }

    public void updateSelection(List<IDIYComponent> newSelection) {
        this.selectedComponents = newSelection;
        Map<IDIYComponent, Set<Integer>> controlPointMap = new HashMap<IDIYComponent, Set<Integer>>();
        
        for (IDIYComponent component : selectedComponents) {
            Set<Integer> indices = new HashSet<Integer>();
            
            for (int i = 0; i < component.getControlPointCount(); i++) {
                indices.add(i);
            }
            
            controlPointMap.put(component, indices);
        }
        
        if (Configuration.INSTANCE.getStickyPoints()) {
            includeStuckComponents(controlPointMap);
        }
        
        getView().selectionStateChanged(selectedComponents, controlPointMap.keySet());
    }

    @Override
    public void expandSelection(ExpansionMode expansionMode) {
        LOG.trace(String.format("expandSelection(%s)", expansionMode));
        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>(this.selectedComponents);

        /*
         * Find control points of all selected components and all types
         */
        Set<String> selectedNamePrefixes = new HashSet<String>();

        if (expansionMode == ExpansionMode.SAME_TYPE) {
            for (IDIYComponent component : getSelectedComponents()) {
                selectedNamePrefixes.add(component.getComponentModel().getNamePrefix());
            }
        }

        /*
         * Now try to find components that intersect with at least one component
         * in the pool.
         */
        for (IDIYComponent component : getCurrentProject().getComponents()) {
            /*
             * Skip already selected components or ones that cannot be stuck to
             * other components.
             */
            Area area = drawingRenderer.getComponentArea(component);
            if (newSelection.contains(component) || !component.isControlPointSticky(0) || area == null) {
                continue;
            }
            
            boolean matches = false;

            for (IDIYComponent selectedComponent : this.selectedComponents) {
                Area selectedArea = drawingRenderer.getComponentArea(selectedComponent);
                if (selectedArea == null) {
                    continue;
                }

                Area intersection = new Area(area);
                intersection.intersect(selectedArea);

                if (!intersection.isEmpty()) {
                    matches = true;
                    break;
                }
            }

            if (matches) {
                switch (expansionMode) {
                case ALL:
                case IMMEDIATE:
                    newSelection.add(component);
                    break;
                case SAME_TYPE:
                    if (selectedNamePrefixes.contains(component.getComponentModel().getNamePrefix())) {
                        newSelection.add(component);
                    }
                    break;
                }
            }
        }

        int oldSize = this.getSelectedComponents().size();
        updateSelection(newSelection);

        /*
         * Go deeper if possible.
         */
        if (newSelection.size() > oldSize && expansionMode != ExpansionMode.IMMEDIATE) {
            expandSelection(expansionMode);
        }

        getView().repaintCanvas();
    }

    /**
     * Removes all the groups that contain at least one of the specified
     * components.
     *
     * @param components
     */
    private void ungroupComponents(Collection<IDIYComponent> components) {
        Iterator<Set<IDIYComponent>> groupIterator = currentProject.getGroups().iterator();
        while (groupIterator.hasNext()) {
            Set<IDIYComponent> group = groupIterator.next();
            group.removeAll(components);
            if (group.isEmpty()) {
                groupIterator.remove();
            }
        }
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
        for (Set<IDIYComponent> group : currentProject.getGroups()) {
            if (group.contains(component)) {
                components.addAll(group);
                break;
            }
        }
        return components;
    }

    @Override
    public Point2D calculateSelectionDimension() {
        if (selectedComponents.isEmpty()) {
            return null;
        }
        boolean metric = Configuration.INSTANCE.getMetric();
        Area area = new Area();
        for (IDIYComponent component : selectedComponents) {
            Area componentArea = drawingRenderer.getComponentArea(component);
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
        int index = currentProject.getComponents().size();
        while (index > 0
                && component.getComponentModel().getZOrder() < 
                        currentProject.getComponents().get(index - 1).getComponentModel().getZOrder()) {
            index--;
        }
        if (index < currentProject.getComponents().size()) {
            currentProject.getComponents().add(index, component);
        } else {
            currentProject.getComponents().add(component);
        }
        if (canCreatePads && Configuration.INSTANCE.getAutoCreatePads() && (component.getComponentModel().hasPads())) { 
            ComponentModel padType = getComponentRegistry().getComponentModel(SOLDER_PAD_COMPONENT_ID);
            for (int i = 0; i < component.getControlPointCount(); i++) {
                if (component.isControlPointSticky(i)) {
                    try {
                        IDIYComponent pad = componentFactory.createComponent(new CreationState(padType, component.getControlPoint(i)), currentProject).getComponentSlot().get(0);
                        pad.setControlPoint(component.getControlPoint(i), 0);
                        addComponent(pad, false);
                    } catch (Exception e) {
                        LOG.warn("Could not auto-create solder pad", e);
                    }
                }
            }
        }
    }

    @Override
    public List<PropertyDescriptor> getMutualSelectionProperties() {
        try {
            return propertyUtils.extractMutualProperties(selectedComponents);
        } catch (Exception e) {
            LOG.error("Could not get mutual selection properties", e);
            return null;
        }
    }

    @Override
    public void applyPropertiesToSelection(List<PropertyDescriptor> properties) {
        LOG.trace(String.format("applyPropertiesToSelection(%s)", properties));
        Project oldProject = currentProject.clone();
        try {
            for (IDIYComponent component : selectedComponents) {
                drawingRenderer.invalidateComponent(component);
                for (PropertyDescriptor property : properties) {
                    if (property.isChanged()) {
                        property.writeTo(component);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Could not apply selection properties", e);
            view.showMessage("Could not apply changes to the selection. Check the log for details.", "Error", View.ERROR_MESSAGE);
        } finally {
            if (!oldProject.equals(currentProject)) {
                registerProjectModification(oldProject, currentProject.clone(), "Edit Selection");
            }
        }
        getView().repaintCanvas();
    }

    @Override
    public void applyPropertyToSelection(PropertyDescriptor property) {
        applyPropertiesToSelection(Arrays.asList(new PropertyDescriptor[] { property }));
    }

    @Override
    public List<PropertyDescriptor> getProjectProperties() {
        List<PropertyDescriptor> properties = propertyDescriptorExtractor.extractProperties(Project.class);
        
        try {
            for (PropertyDescriptor property : properties) {
                property.readFrom(currentProject);
            }
        } catch (Exception e) {
            LOG.error("Could not get project properties", e);
            return null;
        }
        
        Collections.sort(properties, ComparatorFactory.getInstance().getPropertyNameComparator());
        
        return properties;
    }

    @Override
    public void applyPropertiesToProject(List<PropertyDescriptor> properties) {
        LOG.trace(String.format("applyPropertiesToProject(%s)", properties));
        Project oldProject = currentProject.clone();
        try {
            for (PropertyDescriptor property : properties) {
                property.writeTo(currentProject);
            }
        } catch (Exception e) {
            LOG.error("Could not apply project properties", e);
            view.showMessage("Could not apply changes to the project. Check the log for details.", "Error", View.ERROR_MESSAGE);
        } finally {
            if (!oldProject.equals(currentProject)) {
                registerProjectModification(oldProject, currentProject.clone(), "Edit Project");
            }
        }
    }

    @Override
    public ComponentModel getNewComponentTypeSlot() {
        return creationState.getComponentModelSlot();
    }

    @Override
    public void setNewComponentTypeSlot(ComponentModel componentModel, Template template) {
        LOG.trace(String.format("setNewComponentSlot(%s)", componentModel == null ? null : componentModel.getName()));
        if (componentModel != null && componentModel.getComponentClass() == null) {
            LOG.debug("Cannot set new component type slot for type " + componentModel.getName());
            setNewComponentTypeSlot(null, null);
            return;
        }
        try {
            if (componentModel == null) {
                creationState = new CreationState();
            } else {
                switch (componentModel.getCreationMethod()) {
                case POINT_BY_POINT:
                    creationState = new CreationState(componentModel, template, null);
                    break;
                case SINGLE_CLICK:
                    creationState = componentFactory.createComponent(new CreationState(componentModel, template, new Point(0, 0)), currentProject);
                    break;
                }
            }
            if (componentModel != null) {
                updateSelection(EMPTY_SELECTION);
            }
            getView().repaintCanvas();
            getView().updateStatusBar();
        } catch (Exception e) {
            LOG.error("Could not set component type slot", e);
            view.showMessage("Could not set component type slot. Check log for details.", "Error", View.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void saveSelectedComponentAsTemplate(String templateName) {
        LOG.trace(String.format("saveSelectedComponentAsTemplate(%s)", templateName));
        if (selectedComponents.size() != 1) {
            throw new RuntimeException("Can only save a single component as a template at once.");
        }
        IDIYComponent component = selectedComponents.iterator().next();
        ComponentModel type = component.getComponentModel();
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
        List<PropertyDescriptor> properties = type.getPropertyDescriptors();
        Map<String, Object> values = new HashMap<String, Object>();
        for (PropertyDescriptor property : properties) {
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
            int result = view.showConfirmDialog("Template with that name already exists. Overwrite?", "Save as Template",
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
        if (this.selectedComponents.isEmpty()) {
            throw new RuntimeException("No components selected");
        }

        ComponentModel selectedType = this.selectedComponents.get(0).getComponentModel();

        for (int i = 1; i < this.selectedComponents.size(); i++) {
            ComponentModel newType = this.selectedComponents.get(i).getComponentModel();
            if (newType.getComponentClass() != selectedType.getComponentClass()) {
                throw new RuntimeException("Template can be applied on multiple components of the same type only");
            }
        }

        return getTemplatesFor(selectedType.getCategory(), selectedType.getName());
    }

    @Override
    public void applyTemplateToSelection(Template template) {
        LOG.trace(String.format("applyTemplateToSelection(%s)", template.getName()));

        Project oldProject = currentProject.clone();

        for (IDIYComponent component : this.selectedComponents) {
            try {
                drawingRenderer.invalidateComponent(component);
                propertyApplier.applyTemplateControlPoints(component, template);
                propertyApplier.applyDefaultProperties(component, template);
            } catch (Exception e) {
                LOG.warn("Could not apply templates to " + component.getName(), e);
            }
        }

        if (!oldProject.equals(currentProject)) {
            registerProjectModification(oldProject, currentProject.clone(), "Edit Selection");
        }
        getView().repaintCanvas();
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

    private Set<IDIYComponent> getLockedComponents() {
        Set<IDIYComponent> lockedComponents = new HashSet<IDIYComponent>();

        for (IDIYComponent component : currentProject.getComponents()) {
            if (isComponentLocked(component)) {
                lockedComponents.add(component);
            }
        }

        return lockedComponents;
    }

    private boolean isComponentLocked(IDIYComponent component) {
        ComponentModel componentModel = component.getComponentModel();

        return currentProject.getLockedLayers().contains((double) Math.round(componentModel.getZOrder()));
    }

    /**
     * Scales point from display base to actual base.
     *
     * @param point
     * @return
     */
    private Point scalePoint(Point point) {
        return point == null ? null : new Point((int) (point.x / drawingRenderer.getZoomLevel()),
                (int) (point.y / drawingRenderer.getZoomLevel()));
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

    public View getView() {
        return view;
    }

    @Override
    public Map<IDIYComponent, Set<Integer>> getAvailableControlPoints() {
        return availableControlPoints;
    }

    public Set<Double> getLockedLayers() {
        return getCurrentProject().getLockedLayers();
    }

    public void setProjectModified(boolean projectModified) {
        this.projectModified = projectModified;
        getView().updateTitle();
    }

    private void registerProjectModification(Project before, Project after, String actionDescription) {
        getUndoHandler().stateChanged(before, after, actionDescription);
        getController().autoSave(after);
        setProjectModified(true);
    }

    @Override
    public UndoHandler<Project> getUndoHandler() {
        return undoHandler;
    }

    public DrawingController getController() {
        return controller;
    }

    public void setCurrentFile(Path currentPath) {
        this.path = currentPath;
    }

    @Override
    public boolean isSaved() {
        return isSaved;
    }
}
