package org.diylc.app.controllers;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.diylc.app.Drawing;
import org.diylc.app.ExpansionMode;
import org.diylc.app.io.ProjectFileManager;
import org.diylc.app.model.DrawingModel;
import org.diylc.app.model.Model;
import org.diylc.app.view.DrawingView;
import org.diylc.app.view.ISelectionProcessor;
import org.diylc.app.view.rendering.DrawingManager;
import org.diylc.components.registry.ComponentProcessor;
import org.diylc.components.registry.ComponentRegistry;
import org.diylc.components.registry.ComponentType;
import org.diylc.core.EventType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Orientation;
import org.diylc.core.OrientationHV;
import org.diylc.core.Project;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.events.MessageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DrawingController implements ISelectionProcessor {

    static final Logger LOG = LoggerFactory.getLogger(DrawingController.class);

    private final ApplicationController applicationController;

    private final DrawingView view;

    private final Model model;

    private final MessageDispatcher<EventType> messageDispatcher;

    private final Clipboard clipboard;

    private boolean closed = false;

    public DrawingController(ApplicationController applicationController, DrawingView view, DrawingModel model) {
        this.applicationController = applicationController;
        this.view = view;
        this.model = model;
        this.messageDispatcher = new MessageDispatcher<EventType>(true);

        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.clipboard.addFlavorListener(new FlavorListener() {

            @Override
            public void flavorsChanged(FlavorEvent e) {
                getView().refreshActions();
            }
        });

    }

    public ApplicationController getApplicationController() {
        return applicationController;
    }

    public DrawingView getView() {
        return view;
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public synchronized boolean close() {
        if (!closed) {
            if (getDrawing().allowFileAction()) {
                getDrawing().dispose();
                closed = true;
            }
        }

        return closed;
    }

    private Drawing getDrawing() {
        return getView().getDrawing();
    }

    @Override
    public void rotateSelection(int direction) {
        if (!getView().getSelectedComponents().isEmpty()) {
            Project oldProject = getModel().getProject().clone();
            rotateComponents(getView().getSelectedComponents(), direction, getModel().isSnapToGrid());
            messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(), "Rotate Selection");
            messageDispatcher.dispatchMessage(EventType.REPAINT);
        }
    }

    @Override
    public void selectAll(double layer) {
        LOG.trace("selectAll()");
        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>(getModel().getProject().getComponents());
        newSelection.removeAll(getModel().getLockedComponents());
        if (layer > 0) {
            Iterator<IDIYComponent> i = newSelection.iterator();
            while (i.hasNext()) {
                IDIYComponent c = i.next();
                ComponentType type = ComponentRegistry.INSTANCE.getComponentType(c);
                if ((double) type.getZOrder() != layer)
                    i.remove();
            }
        }
        getView().updateSelection(newSelection);
        messageDispatcher.dispatchMessage(EventType.REPAINT);
    }

    @Override
    public void expandSelection(ExpansionMode expansionMode) {
        LOG.trace(String.format("expandSelection(%s)", expansionMode));
        List<IDIYComponent> newSelection = new ArrayList<IDIYComponent>(getView().getSelectedComponents());
        // Find control points of all selected components and all types
        Set<String> selectedNamePrefixes = new HashSet<String>();
        if (expansionMode == ExpansionMode.SAME_TYPE) {
            for (IDIYComponent component : getView().getSelectedComponents()) {
                selectedNamePrefixes.add(ComponentRegistry.INSTANCE.getComponentType(component).getNamePrefix());
            }
        }
        // Now try to find components that intersect with at least one component
        // in the pool.
        for (IDIYComponent component : getModel().getProject().getComponents()) {
            // Skip already selected components or ones that cannot be stuck to
            // other components.
            Area area = getDrawingManager().getComponentArea(component);
            if (newSelection.contains(component) || !component.isControlPointSticky(0) || area == null)
                continue;
            boolean matches = false;
            for (IDIYComponent selectedComponent : getView().getSelectedComponents()) {
                Area selectedArea = getDrawingManager().getComponentArea(selectedComponent);
                if (selectedArea == null)
                    continue;
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
                    if (selectedNamePrefixes.contains(ComponentRegistry.INSTANCE.getComponentType(component).getNamePrefix())) {
                        newSelection.add(component);
                    }
                    break;
                }
            }
        }

        int oldSize = getView().getSelectedComponents().size();
        getView().updateSelection(newSelection);
        // Go deeper if possible.
        if (newSelection.size() > oldSize && expansionMode != ExpansionMode.IMMEDIATE) {
            expandSelection(expansionMode);
        }
        messageDispatcher.dispatchMessage(EventType.REPAINT);
    }

    public Model getModel() {
        return model;
    }

    /**
     * @param direction
     *            1 for clockwise, -1 for counter-clockwise
     */
    private void rotateComponents(List<IDIYComponent> components, int direction, boolean snapToGrid) {
        Point center = getModel().getCenterOf(components, snapToGrid);

        AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 2 * direction, center.x, center.y);

        // Update all points to new location.
        for (IDIYComponent component : components) {
            getDrawingManager().invalidateComponent(component);
            ComponentType type = ComponentRegistry.INSTANCE.getComponentType(component);
            if (type.isRotatable()) {
                for (int index = 0; index < component.getControlPointCount(); index++) {
                    Point p = new Point(component.getControlPoint(index));
                    rotate.transform(p, p);
                    component.setControlPoint(p, index);
                }
                // If component has orientation, change it too
                List<PropertyWrapper> newProperties = ComponentProcessor.getInstance().extractProperties(component.getClass());
                for (PropertyWrapper property : newProperties) {
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
                Point componentCenter = getModel().getCenterOf(Arrays.asList(new IDIYComponent[] { component }), false);
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

    public void groupSelectedComponents() {
        LOG.trace("groupSelectedComponents()");
        Project oldProject = getModel().getProject().clone();
        // First remove the selected components from other groups.
        ungroupComponents(getView().getSelectedComponents());
        // Then group them together.
        getModel().getProject().getGroups().add(new HashSet<IDIYComponent>(getView().getSelectedComponents()));
        // Notify the listeners.
        messageDispatcher.dispatchMessage(EventType.REPAINT);
        if (!oldProject.equals(getModel().getProject())) {
            messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(), "Group");
            getProjectFileManager().notifyFileChange();
        }
    }

    public void ungroupSelectedComponents() {
        LOG.trace("ungroupSelectedComponents()");
        Project oldProject = getModel().getProject().clone();
        ungroupComponents(getView().getSelectedComponents());
        // Notify the listeners.
        messageDispatcher.dispatchMessage(EventType.REPAINT);
        if (!oldProject.equals(getModel().getProject())) {
            messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(), "Ungroup");
            getProjectFileManager().notifyFileChange();
        }
    }

    /**
     * Removes all the groups that contain at least one of the specified
     * components.
     *
     * @param components
     */
    private void ungroupComponents(Collection<IDIYComponent> components) {
        Iterator<Set<IDIYComponent>> groupIterator = getModel().getProject().getGroups().iterator();
        while (groupIterator.hasNext()) {
            Set<IDIYComponent> group = groupIterator.next();
            group.removeAll(components);
            if (group.isEmpty()) {
                groupIterator.remove();
            }
        }
    }

    public void sendSelectionToBack() {
        LOG.trace("sendSelectionToBack()");
        Project oldProject = getModel().getProject().clone();
        for (IDIYComponent component : getView().getSelectedComponents()) {
            ComponentType componentType = ComponentRegistry.INSTANCE.getComponentType(component);
            int index = getModel().getProject().getComponents().indexOf(component);
            if (index < 0) {
                LOG.warn("Component not found in the project: " + component.getName());
            } else
                while (index > 0) {
                    IDIYComponent componentBefore = getModel().getProject().getComponents().get(index - 1);
                    ComponentType componentBeforeType = ComponentRegistry.INSTANCE.getComponentType(componentBefore);
                    if (!componentType.isFlexibleZOrder() && componentBeforeType.getZOrder() < componentType.getZOrder())
                        break;
                    Collections.swap(getModel().getProject().getComponents(), index, index - 1);
                    index--;
                }
        }
        if (!oldProject.equals(getModel().getProject())) {
            messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(), "Send to Back");
            getProjectFileManager().notifyFileChange();
            messageDispatcher.dispatchMessage(EventType.REPAINT);
        }
    }

    public void bringSelectionToFront() {
        LOG.trace("bringSelectionToFront()");
        Project oldProject = getModel().getProject().clone();
        for (IDIYComponent component : getView().getSelectedComponents()) {
            ComponentType componentType = ComponentRegistry.INSTANCE.getComponentType(component);
            int index = getModel().getProject().getComponents().indexOf(component);
            if (index < 0) {
                LOG.warn("Component not found in the project: " + component.getName());
            } else
                while (index < getModel().getProject().getComponents().size() - 1) {
                    IDIYComponent componentAfter = getModel().getProject().getComponents().get(index + 1);
                    ComponentType componentAfterType = ComponentRegistry.INSTANCE.getComponentType(componentAfter);
                    if (!componentType.isFlexibleZOrder() && componentAfterType.getZOrder() > componentType.getZOrder())
                        break;
                    Collections.swap(getModel().getProject().getComponents(), index, index + 1);
                    index++;
                }
        }
        if (!oldProject.equals(getModel().getProject())) {
            messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, getModel().getProject().clone(), "Bring to Front");
            getProjectFileManager().notifyFileChange();
            messageDispatcher.dispatchMessage(EventType.REPAINT);
        }
    }
    
    /**
     * XXX: Must go away!
     */
    @Deprecated
    DrawingManager getDrawingManager() {
        return getView().getDrawingManager();
    }


    /**
     * XXX: Must go away!
     */
    @Deprecated
    ProjectFileManager getProjectFileManager() {
        return getView().getProjectFileManager();
    }

    public void sendEvent(EventType eventType, Object... params) {
        messageDispatcher.dispatchMessage(eventType, params);
    }
    
}
