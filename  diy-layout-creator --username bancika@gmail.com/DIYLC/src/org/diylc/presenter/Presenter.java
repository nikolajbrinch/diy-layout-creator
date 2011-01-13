package org.diylc.presenter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.diylc.common.BadPositionException;
import org.diylc.common.ComponentSelection;
import org.diylc.common.ComponentType;
import org.diylc.common.DrawOption;
import org.diylc.common.EventType;
import org.diylc.common.IComponentFiler;
import org.diylc.common.IPlugIn;
import org.diylc.common.IPlugInPort;
import org.diylc.common.PropertyWrapper;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.measures.SizeUnit;
import org.diylc.gui.IView;
import org.diylc.utils.Constants;

import com.diyfever.gui.miscutils.ConfigurationManager;
import com.diyfever.gui.miscutils.JarScanner;
import com.diyfever.gui.miscutils.Utils;
import com.diyfever.gui.simplemq.MessageDispatcher;
import com.diyfever.gui.update.VersionNumber;
import com.rits.cloning.Cloner;

/**
 * The main presenter class, contains core app logic and drawing routines.
 * 
 * @author Branislav Stojkovic
 */
public class Presenter implements IPlugInPort {

	private static final Logger LOG = Logger.getLogger(Presenter.class);

	public static final VersionNumber CURRENT_VERSION = new VersionNumber(0, 0, 0);
	public static final String DEFAULTS_KEY = "default.";
	public static final String METRIC_KEY = "metric";

	public static final int CONTROL_POINT_SIZE = 5;
	public static final int ICON_SIZE = 32;
	public static boolean ENABLE_ANTIALIASING = true;
	public static boolean DEBUG_COMPONENT_AREAS = false;

	private double zoomLevel = 1;
	private Map<IDIYComponent<?>, Area> componentAreaMap;
	private Project currentProject;
	private Map<String, List<ComponentType>> componentTypes;
	// Maps component class names to ComponentType objects.
	private Map<String, ComponentType> componentTypeMap;
	private List<IPlugIn> plugIns;

	private ComponentSelection selectedComponents;
	// Maps components that have at least one dragged point to set of indices
	// that designate which of their control points are being dragged.
	private Map<IDIYComponent<?>, Set<Integer>> controlPointMap;

	private Cloner cloner;

	private Rectangle selectionRect;

	private final IView view;

	private MessageDispatcher<EventType> messageDispatcher;

	// Layers
	// private Set<ComponentLayer> lockedLayers;
	// private Set<ComponentLayer> visibleLayers;

	// D&D
	private boolean dragInProgress = false;
	// Previous mouse location, not scaled for zoom factor.
	private Point previousDragPoint = null;
	private Project preDragProject = null;

	private ComponentType componentSlot;

	private boolean snapToGrid = true;

	public Presenter(IView view) {
		super();
		this.view = view;
		componentAreaMap = new HashMap<IDIYComponent<?>, Area>();
		plugIns = new ArrayList<IPlugIn>();
		messageDispatcher = new MessageDispatcher<EventType>();
		selectedComponents = new ComponentSelection();
		currentProject = new Project();
		cloner = new Cloner();

		// lockedLayers = EnumSet.noneOf(ComponentLayer.class);
		// visibleLayers = EnumSet.allOf(ComponentLayer.class);
	}

	public void installPlugin(IPlugIn plugIn) {
		LOG.debug(String.format("installPlugin(%s)", plugIn.getClass().getSimpleName()));
		plugIns.add(plugIn);
		plugIn.connect(this);
		messageDispatcher.registerListener(plugIn);
	}

	public void dispose() {
		for (IPlugIn plugIn : plugIns) {
			messageDispatcher.unregisterListener(plugIn);
		}
	}

	// IPlugInPort

	@Override
	public double getZoomLevel() {
		return zoomLevel;
	}

	@Override
	public void setZoomLevel(double zoomLevel) {
		LOG.debug(String.format("setZoomLevel(%s)", zoomLevel));
		this.zoomLevel = zoomLevel;
		messageDispatcher.dispatchMessage(EventType.ZOOM_CHANGED, zoomLevel);
		messageDispatcher.dispatchMessage(EventType.REPAINT);
	}

	@Override
	public Cursor getCursorAt(Point point) {
		// Only change the cursor if we're not making a new component.
		if (componentSlot == null) {
			// Scale point to remove zoom factor.
			Point2D scaledPoint = scalePoint(point);
			for (Map.Entry<IDIYComponent<?>, Area> entry : componentAreaMap.entrySet()) {
				if (entry.getValue().contains(scaledPoint)) {
					return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				}
			}
			if (controlPointMap != null && !controlPointMap.isEmpty()) {
				return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			}
		}
		return Cursor.getDefaultCursor();
	}

	@Override
	public Dimension getCanvasDimensions(boolean useZoom) {
		double width = currentProject.getWidth().convertToPixels();
		int height = currentProject.getHeight().convertToPixels();
		if (useZoom) {
			width *= zoomLevel;
			height *= zoomLevel;
		}
		return new Dimension((int) width, (int) height);
	}

	@Override
	public Project getCurrentProject() {
		return currentProject;
	}

	@Override
	public void loadProject(Project project, boolean freshStart) {
		LOG.info("Loading project: " + project.getTitle());
		this.currentProject = project;
		selectedComponents.clear();
		messageDispatcher.dispatchMessage(EventType.PROJECT_LOADED, project, freshStart);
		messageDispatcher.dispatchMessage(EventType.REPAINT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<ComponentType>> getComponentTypes() {
		if (componentTypes == null) {
			LOG.info("Loading component types.");
			componentTypes = new HashMap<String, List<ComponentType>>();
			componentTypeMap = new HashMap<String, ComponentType>();
			List<Class<?>> componentTypeClasses = JarScanner.getInstance().scanFolder("library/",
					IDIYComponent.class);
			for (Class<?> clazz : componentTypeClasses) {
				if (!Modifier.isAbstract(clazz.getModifiers())) {
					ComponentType componentType = ComponentProcessor.getInstance()
							.createComponentTypeFrom((Class<? extends IDIYComponent<?>>) clazz);
					componentTypeMap.put(componentType.getInstanceClass().getName(), componentType);
					List<ComponentType> nestedList;
					if (componentTypes.containsKey(componentType.getCategory())) {
						nestedList = componentTypes.get(componentType.getCategory());
					} else {
						nestedList = new ArrayList<ComponentType>();
						componentTypes.put(componentType.getCategory(), nestedList);
					}
					nestedList.add(componentType);
				}
			}
		}
		return componentTypes;
	}

	@Override
	public void draw(Graphics2D g2d, Set<DrawOption> drawOptions, IComponentFiler filter) {
		if (currentProject == null) {
			return;
		}
		G2DWrapper g2dWrapper = new G2DWrapper(g2d);

		if (drawOptions.contains(DrawOption.ANTIALIASING) && ENABLE_ANTIALIASING) {
			g2d
					.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
		}

		// AffineTransform initialTx = g2d.getTransform();
		Dimension d = getCanvasDimensions(drawOptions.contains(DrawOption.ZOOM));

		g2dWrapper.setColor(Constants.CANVAS_COLOR);
		g2dWrapper.fillRect(0, 0, d.width, d.height);

		if (drawOptions.contains(DrawOption.GRID)) {
			double zoomStep = Constants.GRID * zoomLevel;

			g2dWrapper.setColor(Constants.GRID_COLOR);
			for (double i = zoomStep; i < d.width; i += zoomStep) {
				g2dWrapper.drawLine((int) i, 0, (int) i, d.height - 1);
			}
			for (double j = zoomStep; j < d.height; j += zoomStep) {
				g2dWrapper.drawLine(0, (int) j, d.width - 1, (int) j);
			}
		}

		if ((drawOptions.contains(DrawOption.ZOOM)) && (Math.abs(1.0 - zoomLevel) > 1e-4)) {
			g2dWrapper.scale(zoomLevel, zoomLevel);
		}

		// Composite mainComposite = g2d.getComposite();
		// Composite alphaComposite =
		// AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

		// g2dWrapper.resetTx();

		List<IDIYComponent<?>> components = getCurrentProject().getComponents();
		componentAreaMap.clear();
		if (components != null) {
			for (IDIYComponent<?> component : components) {
				// Do not draw the component if it's filtered out.
				if (filter != null && !filter.testComponent(component)) {
					continue;
				}
				g2dWrapper.startedDrawingComponent();
				ComponentState state = ComponentState.NORMAL;
				if (drawOptions.contains(DrawOption.SELECTION)
						&& selectedComponents.contains(component)) {
					if (dragInProgress) {
						state = ComponentState.DRAGGING;
					} else {
						state = ComponentState.SELECTED;
					}
				}
				// Draw the component through the g2dWrapper.
				component.draw(g2dWrapper, state, currentProject);
				componentAreaMap.put(component, g2dWrapper.finishedDrawingComponent());
			}
			// Draw control points.
			for (IDIYComponent<?> component : components) {
				if (drawOptions.contains(DrawOption.CONTROL_POINTS)) {
					for (int i = 0; i < component.getControlPointCount(); i++) {
						Point controlPoint = component.getControlPoint(i);
						try {
							if (shouldShowControlPointsFor(component)) {
								g2dWrapper.setColor(Constants.CONTROL_POINT_COLOR);
								g2dWrapper.setStroke(new BasicStroke(2));
								// g2d.drawOval(controlPoint.x - 2,
								// controlPoint.y - 2, 4, 4);
								g2dWrapper.fillOval(controlPoint.x - CONTROL_POINT_SIZE / 2,
										controlPoint.y - CONTROL_POINT_SIZE / 2,
										CONTROL_POINT_SIZE, CONTROL_POINT_SIZE);
							}
						} catch (Exception e) {
							LOG.error("Could not obtain control points for component of type "
									+ component.getClass().getName());
						}
					}
				}
			}
		}

		// Go back to the original transformation and zoom in to draw the
		// selection rectangle and other similar elements.
		// g2d.setTransform(initialTx);
		// if ((drawOptions.contains(DrawOption.ZOOM)) && (Math.abs(1.0 -
		// zoomLevel) > 1e-4)) {
		// g2d.scale(zoomLevel, zoomLevel);
		// }

		// At the end draw selection rectangle if needed.
		if (drawOptions.contains(DrawOption.SELECTION) && (selectionRect != null)) {
			g2d.setColor(Color.white);
			g2d.draw(selectionRect);
			g2d.setColor(Color.black);
			g2d.setStroke(Constants.dashedStroke);
			g2d.draw(selectionRect);
		}

		// Draw component area for test
		if (DEBUG_COMPONENT_AREAS) {
			g2d.setStroke(new BasicStroke());
			g2d.setColor(Color.red);
			for (Area area : componentAreaMap.values()) {
				g2d.draw(area);
			}
		}
	}

	@Override
	public void injectGUIComponent(JComponent component, int position) throws BadPositionException {
		LOG.info(String.format("injectGUIComponent(%s, %s)", component.toString(), position));
		view.addComponent(component, position);
	}

	@Override
	public void injectMenuAction(Action action, String menuName) {
		LOG.info(String.format("injectMenuAction(%s, %s)", action == null ? "Separator" : action
				.getValue(Action.NAME), menuName));
		view.addMenuAction(action, menuName);
	}

	/**
	 * Finds all components whose areas include the specified {@link Point}.
	 * Point is <b>not</b> scaled by the zoom factor.
	 * 
	 * @return
	 */
	private List<IDIYComponent<?>> findComponentsAt(Point point) {
		List<IDIYComponent<?>> components = new ArrayList<IDIYComponent<?>>();
		for (Map.Entry<IDIYComponent<?>, Area> entry : componentAreaMap.entrySet()) {
			if (entry.getValue().contains(point)) {
				components.add(entry.getKey());
			}
		}
		// Sort by z-order.
		Collections.sort(components, ComparatorFactory.getInstance().getComponentZOrderComparator(
				currentProject.getComponents()));
		return components;
	}

	@Override
	public void mouseClicked(Point point, boolean ctrlDown, boolean shiftDown, boolean altDown) {
		LOG.debug(String
				.format("mouseClicked(%s, %s, %s, %s)", point, ctrlDown, shiftDown, altDown));
		Point scaledPoint = scalePoint(point);
		if (componentSlot != null) {
			try {
				instantiateComponent(componentSlot, scaledPoint);
			} catch (Exception e) {
				LOG.error("Error instatiating component of type: "
						+ componentSlot.getInstanceClass().getName(), e);
			}
			setNewComponentSlot(null);
		} else {
			List<IDIYComponent<?>> components = findComponentsAt(scaledPoint);
			// If there's nothing under mouse cursor deselect all.
			if (components.isEmpty()) {
				selectedComponents.clear();
			} else {
				IDIYComponent<?> component = components.get(components.size() - 1);
				// If ctrl is pressed just toggle the component under mouse
				// cursor.
				if (ctrlDown) {
					if (selectedComponents.contains(component)) {
						selectedComponents.removeAll(findAllGroupedComponents(component));
					} else {
						selectedComponents.addAll(findAllGroupedComponents(component));
					}
				} else {
					// Otherwise just select that one component.
					selectedComponents.clear();
					selectedComponents.addAll(findAllGroupedComponents(component));
				}
			}
			messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
			messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
					calculateSelectionDimension());
			messageDispatcher.dispatchMessage(EventType.REPAINT);
		}
	}

	@Override
	public void mouseMoved(Point point, boolean ctrlDown, boolean shiftDown, boolean altDown) {
		Map<IDIYComponent<?>, Set<Integer>> components = new HashMap<IDIYComponent<?>, Set<Integer>>();
		Point scaledPoint = scalePoint(point);
		// Go backwards so we take the highest z-order components first.
		for (int i = currentProject.getComponents().size() - 1; i >= 0; i--) {
			IDIYComponent<?> component = currentProject.getComponents().get(i);
			for (int pointIndex = 0; pointIndex < component.getControlPointCount(); pointIndex++) {
				Point controlPoint = component.getControlPoint(pointIndex);
				// Only consider selected components that are not grouped.
				if (selectedComponents.contains(component)
						&& findAllGroupedComponents(component).size() == 1) {
					try {
						if (scaledPoint.distance(controlPoint) < CONTROL_POINT_SIZE) {
							Set<Integer> indices = new HashSet<Integer>();
							ComponentType componentType = componentTypeMap.get(component.getClass()
									.getName());
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
						LOG.warn("Error reading control point for component of type: "
								+ component.getClass().getName());
					}
				}
			}
			// // If CTRL is pressed, we only care about the top most component.
			// if (altDown && components.size() > 0) {
			// break;
			// }
		}

		messageDispatcher.dispatchMessage(EventType.MOUSE_MOVED, scaledPoint);

		if (!components.equals(controlPointMap)) {
			controlPointMap = components;
			messageDispatcher.dispatchMessage(EventType.AVAILABLE_CTRL_POINTS_CHANGED,
					new HashMap<IDIYComponent<?>, Set<Integer>>(components));
		}
	}

	@Override
	public ComponentSelection getSelectedComponents() {
		return new ComponentSelection(selectedComponents);
	}

	@Override
	public void selectAll() {
		LOG.info("selectAll()");
		this.selectedComponents = new ComponentSelection(currentProject.getComponents());
		messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
		messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
				calculateSelectionDimension());
		messageDispatcher.dispatchMessage(EventType.REPAINT);
	}

	@Override
	public Area getComponentArea(IDIYComponent<?> component) {
		return componentAreaMap.get(component);
	}

	@Override
	public VersionNumber getCurrentVersionNumber() {
		return CURRENT_VERSION;
	}

	@Override
	public void dragStarted(Point point) {
		LOG.debug(String.format("dragStarted(%s)", point));
		dragInProgress = true;
		preDragProject = cloner.deepClone(currentProject);
		Point scaledPoint = scalePoint(point);
		previousDragPoint = scaledPoint;
		List<IDIYComponent<?>> components = findComponentsAt(scaledPoint);
		if (!controlPointMap.isEmpty()) {
			// If we're dragging control points reset selection.
			selectedComponents.clear();
			selectedComponents.addAll(controlPointMap.keySet());
			messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
			messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
					calculateSelectionDimension());
			messageDispatcher.dispatchMessage(EventType.REPAINT);
		} else if (components.isEmpty()) {
			// If there are no components are under the cursor, reset selection.
			selectedComponents.clear();
			messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
			messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
					calculateSelectionDimension());
			messageDispatcher.dispatchMessage(EventType.REPAINT);
		} else {
			// Take the last component, i.e. the top order component.
			IDIYComponent<?> component = components.get(components.size() - 1);
			// If the component under the cursor is not already selected, make
			// it into the only selected component.
			if (!selectedComponents.contains(component)) {
				selectedComponents.clear();
				selectedComponents.addAll(findAllGroupedComponents(component));
				messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
				messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
						calculateSelectionDimension());
				messageDispatcher.dispatchMessage(EventType.REPAINT);
			}
			// If there aren't any control points, try to add all the selected
			// components with all their control points. That will allow the
			// user to drag the whole components.
			for (IDIYComponent<?> c : selectedComponents) {
				Set<Integer> pointIndices = new HashSet<Integer>();
				if (c.getControlPointCount() > 0) {
					for (int i = 0; i < c.getControlPointCount(); i++) {
						pointIndices.add(i);
					}
					controlPointMap.put(c, pointIndices);
				}
			}
			// Expand control points to include all stuck components.
			includeStuckComponents(controlPointMap);
			LOG.debug(controlPointMap);
		}
	}

	/**
	 * Finds any components that are stuck to one of the components already in
	 * the map.
	 * 
	 * @param controlPointMap
	 */
	private void includeStuckComponents(Map<IDIYComponent<?>, Set<Integer>> controlPointMap) {
		int oldSize = controlPointMap.size();
		LOG.debug("Expanding selected component map");
		for (IDIYComponent<?> component : currentProject.getComponents()) {
			// Do not process a component if it's already in the map.
			ComponentType componentType = componentTypeMap.get(component.getClass().getName());
			if (!controlPointMap.containsKey(component) && componentType.isSticky()) {
				// Check if there's a control point in the current selection
				// that matches with one of its control points.
				for (int i = 0; i < component.getControlPointCount(); i++) {
					if (controlPointMap.containsKey(component)) {
						break;
					}
					boolean componentMatches = false;
					for (Map.Entry<IDIYComponent<?>, Set<Integer>> entry : controlPointMap
							.entrySet()) {
						if (componentMatches) {
							break;
						}
						for (Integer j : entry.getValue()) {
							Point firstPoint = component.getControlPoint(i);
							Point secondPoint = entry.getKey().getControlPoint(j);
							// If they are close enough we can consider them
							// matched.
							if (firstPoint.distance(secondPoint) < CONTROL_POINT_SIZE) {
								componentMatches = true;
								break;
							}
						}
					}
					if (componentMatches) {
						LOG.debug("Including component: " + component);
						Set<Integer> indices = new HashSet<Integer>();
						// For stretchable components just add the
						// matching component.
						// Otherwise, add all control points.
						if (componentType.isStretchable()) {
							indices.add(i);
						} else {
							for (int k = 0; k < component.getControlPointCount(); k++) {
								indices.add(k);
							}
						}
						controlPointMap.put(component, indices);
					}
				}
			}
		}
		int newSize = controlPointMap.size();
		// As long as we're adding new components, do another iteration.
		if (newSize > oldSize) {
			LOG.debug("Component count changed, trying one more time.");
			includeStuckComponents(controlPointMap);
		} else {
			LOG.debug("Component count didn't change, done with expanding.");
		}
	}

	@Override
	public boolean dragOver(Point point) {
		if (point == null) {
			return false;
		}
		Point scaledPoint = scalePoint(point);
		boolean repaint = false;
		if (!controlPointMap.isEmpty()) {
			// We're dragging control point(s).
			int dx = (scaledPoint.x - previousDragPoint.x);
			int dy = (scaledPoint.y - previousDragPoint.y);
			if (snapToGrid) {
				dx = (int) (Math.round(dx / Constants.GRID) * Constants.GRID);
				dy = (int) (Math.round(dy / Constants.GRID) * Constants.GRID);
			}
			// Only repaint if there's an actual change.
			repaint = dx != 0 || dy != 0;

			previousDragPoint.translate(dx, dy);

			// Update all points.
			for (Map.Entry<IDIYComponent<?>, Set<Integer>> entry : controlPointMap.entrySet()) {
				IDIYComponent<?> c = entry.getKey();
				for (Integer index : entry.getValue()) {
					Point p = new Point(c.getControlPoint(index));
					p.translate(dx, dy);
					c.setControlPoint(p, index);
				}
			}
			// dragStartPoint = point;
		} else if (selectedComponents.isEmpty()) {
			// If there's no selection, the only thing to do is update the
			// selection rectangle and refresh.
			this.selectionRect = Utils.createRectangle(scaledPoint, previousDragPoint);
			repaint = true;
			// messageDispatcher.dispatchMessage(EventType.SELECTION_RECT_CHANGED,
			// selectionRect);
		}
		if (repaint) {
			messageDispatcher.dispatchMessage(EventType.REPAINT);
		}
		messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
				calculateSelectionDimension());
		return true;
	}

	@Override
	public void dragEnded(Point point) {
		LOG.debug(String.format("dragEnded(%s)", point));
		if (!dragInProgress) {
			return;
		}
		Point scaledPoint = scalePoint(point);
		if (selectedComponents.isEmpty()) {
			// If there's no selection finalize selectionRect and see which
			// components intersect with it.
			if (scaledPoint != null) {
				this.selectionRect = Utils.createRectangle(scaledPoint, previousDragPoint);
			}
			selectedComponents.clear();
			for (IDIYComponent<?> component : currentProject.getComponents()) {
				Area area = componentAreaMap.get(component);
				if ((area != null) && (selectionRect != null) && area.intersects(selectionRect)) {
					selectedComponents.addAll(findAllGroupedComponents(component));
				}
			}
			selectionRect = null;
			messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
			messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
					calculateSelectionDimension());
		}
		// There is selection, so we need to finalize the drag&drop
		// operation.

		if (!preDragProject.equals(currentProject)) {
			messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, preDragProject, cloner
					.deepClone(currentProject), "Move");
		}
		messageDispatcher.dispatchMessage(EventType.REPAINT);
		dragInProgress = false;
	}

	@Override
	public void addComponents(List<IDIYComponent<?>> components, Point preferredPoint) {
		LOG.debug(String.format("addComponents(%s)", components));
		Project oldProject = cloner.deepClone(currentProject);
		for (IDIYComponent<?> component : components) {
			addComponent(component, componentTypeMap.get(component.getClass().getName()));
		}
		messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, cloner
				.deepClone(currentProject), "Add");
		messageDispatcher.dispatchMessage(EventType.REPAINT);
	}

	@Override
	public void deleteSelectedComponents() {
		LOG.debug("deleteSelectedComponents()");
		Project oldProject = cloner.deepClone(currentProject);
		// Remove selected components from any groups.
		ungroupComponents(selectedComponents);
		currentProject.getComponents().removeAll(selectedComponents);
		messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, cloner
				.deepClone(currentProject), "Delete");
		messageDispatcher.dispatchMessage(EventType.REPAINT);
	}

	// public boolean isLayerLocked(ComponentLayer layer) {
	// return lockedLayers.contains(layer);
	// }
	//
	// public void setLayerLocked(ComponentLayer layer, boolean locked) {
	// LOG.debug(String.format("setLayerLocked(%s, %s)", layer, locked));
	// if (locked) {
	// lockedLayers.add(layer);
	// } else {
	// lockedLayers.remove(layer);
	// }
	// }
	//
	// public boolean isLayerVisible(ComponentLayer layer) {
	// return visibleLayers.contains(layer);
	// }
	//
	// public void setLayerVisible(ComponentLayer layer, boolean visible) {
	// LOG.debug(String.format("setLayerVisible(%s, %s)", layer, visible));
	// if (visible) {
	// visibleLayers.add(layer);
	// } else {
	// visibleLayers.remove(layer);
	// }
	// }

	@SuppressWarnings("unchecked")
	@Override
	public void setDefaultPropertyValue(String propertyName, Object value) {
		LOG.debug(String.format("setDefaultPropertyValue(%s, %s)", propertyName, value));
		for (IDIYComponent component : selectedComponents) {
			String className = component.getClass().getName();
			LOG.debug("Default property value set for " + className + ":" + propertyName);
			ConfigurationManager.getInstance().setConfigurationItem(
					DEFAULTS_KEY + className + ":" + propertyName, value);
		}
	}

	@Override
	public void setMetric(boolean isMetric) {
		ConfigurationManager.getInstance().setConfigurationItem(Presenter.METRIC_KEY, isMetric);
		messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
				calculateSelectionDimension());
	}

	@Override
	public void groupSelectedComponents() {
		LOG.debug("groupSelectedComponents()");
		Project oldProject = cloner.deepClone(currentProject);
		// First remove the selected components from other groups.
		ungroupComponents(selectedComponents);
		// Then group them together
		currentProject.getGroups().add(new HashSet<IDIYComponent<?>>(selectedComponents));
		// Notify the listeners.
		if (!oldProject.equals(currentProject)) {
			messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, cloner
					.deepClone(currentProject), "Group");
		}
	}

	@Override
	public void ungroupSelectedComponents() {
		LOG.debug("ungroupSelectedComponents()");
		Project oldProject = cloner.deepClone(currentProject);
		ungroupComponents(selectedComponents);
		// Notify the listeners.
		if (!oldProject.equals(currentProject)) {
			messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, cloner
					.deepClone(currentProject), "Group");
		}
	}

	/**
	 * Removes all the groups that contain at least one of the specified
	 * components.
	 * 
	 * @param components
	 */
	private void ungroupComponents(Collection<IDIYComponent<?>> components) {
		Iterator<Set<IDIYComponent<?>>> groupIterator = currentProject.getGroups().iterator();
		while (groupIterator.hasNext()) {
			Set<IDIYComponent<?>> group = groupIterator.next();
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
	private Set<IDIYComponent<?>> findAllGroupedComponents(IDIYComponent<?> component) {
		Set<IDIYComponent<?>> components = new HashSet<IDIYComponent<?>>();
		components.add(component);
		for (Set<IDIYComponent<?>> group : currentProject.getGroups()) {
			if (group.contains(component)) {
				components.addAll(group);
				break;
			}
		}
		return components;
	}

	/**
	 * @return selection size expressed in either inches or centimeters, based
	 *         on the user preference.
	 */
	private Point2D calculateSelectionDimension() {
		if (selectedComponents.isEmpty()) {
			return null;
		}
		Boolean isMetric = (Boolean) ConfigurationManager.getInstance().getConfigurationItem(
				METRIC_KEY);
		if (isMetric == null) {
			isMetric = true;
		}
		Area area = new Area();
		for (IDIYComponent<?> component : selectedComponents) {
			Area componentArea = componentAreaMap.get(component);
			if (componentArea != null) {
				area.add(componentArea);
			}
		}
		double width = area.getBounds2D().getWidth();
		double height = area.getBounds2D().getHeight();
		width /= Constants.GRID * Constants.GRIDS_PER_INCH;
		height /= Constants.GRID * Constants.GRIDS_PER_INCH;
		if (isMetric) {
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
	 * @param componentType
	 */
	private void addComponent(IDIYComponent<?> component, ComponentType componentType) {
		int index = 0;
		while (index < currentProject.getComponents().size()
				&& componentType.getZOrder() >= componentTypeMap.get(
						currentProject.getComponents().get(index).getClass().getName()).getZOrder()) {
			index++;
		}
		if (index < currentProject.getComponents().size()) {
			currentProject.getComponents().add(index, component);
		} else {
			currentProject.getComponents().add(component);
		}
	}

	@SuppressWarnings("unchecked")
	private void instantiateComponent(ComponentType componentType, Point point) throws Exception {
		LOG.info("Instatiating component of type: " + componentType.getInstanceClass().getName());

		Project oldProject = cloner.deepClone(currentProject);

		// Instantiate the component.
		IDIYComponent component = componentType.getInstanceClass().newInstance();

		// Find the next available componentName for the component.
		int i = 0;
		boolean exists = true;
		while (exists) {
			i++;
			String name = componentType.getNamePrefix() + i;
			exists = false;
			for (IDIYComponent c : currentProject.getComponents()) {
				if (c.getName().equals(name)) {
					exists = true;
					break;
				}
			}
		}
		component.setName(componentType.getNamePrefix() + i);

		// Add it to the project taking z-order into account.
		addComponent(component, componentType);
		// Select the new component
		selectedComponents.clear();
		selectedComponents.add(component);

		// Translate them to the desired location.
		if (point != null) {
			for (int j = 0; j < component.getControlPointCount(); j++) {
				Point controlPoint = new Point(component.getControlPoint(j));
				int x = controlPoint.x + point.x;
				int y = controlPoint.y + point.y;
				if (snapToGrid) {
					x = (int) (Math.round(x / Constants.GRID) * Constants.GRID);
					y = (int) (Math.round(y / Constants.GRID) * Constants.GRID);
				}
				controlPoint.setLocation(x, y);
				component.setControlPoint(controlPoint, j);
			}
		}

		// Extract properties.
		List<PropertyWrapper> properties = ComponentProcessor.getInstance().extractProperties(
				componentType.getInstanceClass());
		// Override with default values if available.
		for (PropertyWrapper property : properties) {
			Object defaultValue = ConfigurationManager.getInstance().getConfigurationItem(
					DEFAULTS_KEY + componentType.getInstanceClass().getName() + ":"
							+ property.getName());
			if (defaultValue != null) {
				property.setValue(cloner.deepClone(defaultValue));
				property.writeTo(component);
			}
		}

		// Notify the listeners.
		if (!oldProject.equals(currentProject)) {
			messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, cloner
					.deepClone(currentProject), "Add " + componentType.getName());
		}
		messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
		messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
				calculateSelectionDimension());
		messageDispatcher.dispatchMessage(EventType.REPAINT);
	}

	@Override
	public List<PropertyWrapper> getMutualSelectionProperties() {
		return ComponentProcessor.getInstance().getMutualSelectionProperties(selectedComponents);
	}

	@Override
	public void applyPropertiesToSelection(List<PropertyWrapper> properties) throws Exception {
		LOG.debug(String.format("applyPropertiesToSelection(%s)", properties));
		Project oldProject = cloner.deepClone(currentProject);
		try {
			for (IDIYComponent<?> component : selectedComponents) {
				for (PropertyWrapper property : properties) {
					property.writeTo(component);
				}
			}
		} finally {
			// Notify the listeners.
			if (!oldProject.equals(currentProject)) {
				messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, cloner
						.deepClone(currentProject), "Edit");
			}
			messageDispatcher.dispatchMessage(EventType.REPAINT);
			messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
					calculateSelectionDimension());
		}
	}

	@Override
	public void setNewComponentSlot(ComponentType componentType) {
		LOG.debug(String.format("setNewComponentSlot(%s)", componentType == null ? null
				: componentType.getName()));
		this.componentSlot = componentType;
		selectedComponents.clear();
		messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
		messageDispatcher.dispatchMessage(EventType.SELECTION_SIZE_CHANGED,
				calculateSelectionDimension());
		messageDispatcher.dispatchMessage(EventType.SLOT_CHANGED, componentSlot);
	}

	/**
	 * Scales point from display base to actual base.
	 * 
	 * @param point
	 * @return
	 */
	private Point scalePoint(Point point) {
		return point == null ? null : new Point((int) (point.x / zoomLevel),
				(int) (point.y / zoomLevel));
	}

	/**
	 * @param component
	 * @return true if control points should be rendered for the specified
	 *         component.
	 */
	private boolean shouldShowControlPointsFor(IDIYComponent<?> component) {
		ComponentType componentType = componentTypeMap.get(component.getClass().getName());
		// Do not show control points for non-stretchable components.
		if (!componentType.isStretchable()) {
			return false;
		}
		if (findAllGroupedComponents(component).size() > 1) {
			return false;
		}
		return selectedComponents.contains(component);
		// return
		// controlPoint.getVisibilityPolicy().equals(VisibilityPolicy.ALWAYS)
		// ||
		// ((controlPoint.getVisibilityPolicy().equals(VisibilityPolicy.WHEN_SELECTED))
		// && (getSelectedComponents()
		// .contains(component)));
	}
}
