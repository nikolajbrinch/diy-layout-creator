package org.diylc.presenter;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.diylc.common.BadPositionException;
import org.diylc.common.ComponentSelection;
import org.diylc.common.ComponentType;
import org.diylc.common.DrawOption;
import org.diylc.common.EventType;
import org.diylc.common.IPlugIn;
import org.diylc.common.IPlugInPort;
import org.diylc.common.PropertyWrapper;
import org.diylc.core.ComponentLayer;
import org.diylc.core.ComponentState;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.gui.IView;
import org.diylc.utils.Constants;

import com.diyfever.gui.miscutils.ConfigurationManager;
import com.diyfever.gui.miscutils.JarScanner;
import com.diyfever.gui.miscutils.Utils;
import com.diyfever.gui.simplemq.MessageDispatcher;
import com.diyfever.gui.update.VersionNumber;
import com.rits.cloning.Cloner;

/**
 * The main presenter class.
 * 
 * @author Branislav Stojkovic
 */
public class Presenter implements IPlugInPort {

	private static final Logger LOG = Logger.getLogger(Presenter.class);

	public static final VersionNumber CURRENT_VERSION = new VersionNumber(0, 0, 0);
	public static final String DEFAULTS_KEY = "defaults";
	public static final int CONTROL_POINT_SENSITIVITY = 4;
	public static final int ICON_SIZE = 32;
	public static boolean ENABLE_ANTIALIASING = true;

	private double zoomLevel = 1;
	private Map<IDIYComponent<?>, Area> componentAreaMap;
	private Project currentProject;
	private Map<String, List<ComponentType>> componentTypes;
	private List<IPlugIn> plugIns;

	private ComponentSelection selectedComponents;
	// List of components that have at least one of their control points
	// under the last recorded mouse position.
	private Map<IDIYComponent<?>, Integer> componentControlPointMap;

	private Cloner cloner;

	private Rectangle selectionRect;

	private final IView view;

	private MessageDispatcher<EventType> messageDispatcher;

	// Layers
	private Set<ComponentLayer> lockedLayers;
	private Set<ComponentLayer> visibleLayers;

	// D&D
	private boolean dragInProgress = false;
	// Previous mouse location, not scaled for zoom factor.
	private Point previousDragPoint = null;
	private Project preDragProject = null;

	private ComponentType componentSlot;

	public Presenter(IView view) {
		super();
		this.view = view;
		componentAreaMap = new HashMap<IDIYComponent<?>, Area>();
		plugIns = new ArrayList<IPlugIn>();
		messageDispatcher = new MessageDispatcher<EventType>();
		selectedComponents = new ComponentSelection();
		currentProject = new Project();
		cloner = new Cloner();

		lockedLayers = EnumSet.noneOf(ComponentLayer.class);
		visibleLayers = EnumSet.allOf(ComponentLayer.class);
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
			if (componentControlPointMap != null && !componentControlPointMap.isEmpty()) {
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
			List<Class<?>> componentTypeClasses = JarScanner.getInstance().scanFolder("library/",
					IDIYComponent.class);
			for (Class<?> clazz : componentTypeClasses) {
				if (!Modifier.isAbstract(clazz.getModifiers())) {
					ComponentDescriptor annotation = clazz.getAnnotation(ComponentDescriptor.class);
					String name;
					String description;
					String category;
					String namePrefix;
					String author;
					Icon icon;
					Class<? extends IDIYComponent> instanceClass = (Class<? extends IDIYComponent>) clazz;
					ComponentLayer layer;
					if (annotation == null) {
						name = clazz.getSimpleName();
						description = "";
						category = "Uncategorized";
						namePrefix = "Unknown";
						author = "Unknown";
						layer = ComponentLayer.COMPONENT;
					} else {
						name = annotation.name();
						description = annotation.desciption();
						category = annotation.category();
						namePrefix = annotation.instanceNamePrefix();
						author = annotation.author();
						layer = annotation.componentLayer();
					}
					icon = null;
					// Draw component icon.
					try {
						IDIYComponent componentInstance = (IDIYComponent) clazz.newInstance();
						Image image = new BufferedImage(ICON_SIZE, ICON_SIZE,
								java.awt.image.BufferedImage.TYPE_INT_ARGB);
						componentInstance.drawIcon((Graphics2D) image.getGraphics(), ICON_SIZE,
								ICON_SIZE);
						icon = new ImageIcon(image);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					ComponentType componentType = new ComponentType(name, description, category,
							namePrefix, author, icon, instanceClass, layer);
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
	public void draw(Graphics2D g2d, Set<DrawOption> drawOptions) {
		if (currentProject == null) {
			return;
		}
		G2DWrapper g2dWrapper = new G2DWrapper(g2d);

		if (drawOptions.contains(DrawOption.ANTIALIASING) && ENABLE_ANTIALIASING) {
			g2d
					.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
		}

		AffineTransform initialTx = g2d.getTransform();
		Dimension d = getCanvasDimensions(drawOptions.contains(DrawOption.ZOOM));

		g2dWrapper.setColor(Constants.CANVAS_COLOR);
		g2dWrapper.fillRect(0, 0, d.width, d.height);

		if (drawOptions.contains(DrawOption.GRID)) {
			double zoomStep = Constants.GRID * zoomLevel;
			// Point2D p = new Point2D.Double(step, 0);
			// g2d.getTransform().transform(p, p);
			// System.out.println(p);

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

		Composite mainComposite = g2d.getComposite();
		Composite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

		// g2dWrapper.resetTx();

		List<IDIYComponent<?>> components = getCurrentProject().getComponents();
		componentAreaMap.clear();
		if (components != null) {
			for (IDIYComponent<?> component : components) {
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
				// If the component is being dragged, draw it in a separate
				// composite.
				if (state == ComponentState.DRAGGING) {
					g2dWrapper.setComposite(alphaComposite);
				}
				// Draw the component through the g2dWrapper.
				component.draw(g2dWrapper, state, currentProject);
				// Restore the composite if needed.
				if (state == ComponentState.DRAGGING) {
					g2dWrapper.setComposite(mainComposite);
				}
				componentAreaMap.put(component, g2dWrapper.finishedDrawingComponent());

				// Draw control points
				if (drawOptions.contains(DrawOption.CONTROL_POINTS)) {
					for (int i = 0; i < component.getControlPointCount(); i++) {
						Point controlPoint = component.getControlPoint(i);
						try {
							if (shouldShowControlPointsFor(component)) {
								g2d.setColor(Constants.CONTROL_POINT_COLOR);
								g2d.setStroke(new BasicStroke(2));
								g2d.drawOval(controlPoint.x - 2, controlPoint.y - 2, 4, 4);
								// g2d.fillOval(point.getValue().x - 2, point
								// .getValue().y - 2, 5, 5);
							}
						} catch (Exception e) {
							LOG.error("Could not obtain control points for component of type "
									+ component.getClass().getName());
						}
					}
				}
			}
		}

		g2d.setTransform(initialTx);
		if ((drawOptions.contains(DrawOption.ZOOM)) && (Math.abs(1.0 - zoomLevel) > 1e-4)) {
			g2d.scale(zoomLevel, zoomLevel);
		}

		// At the end draw selection rectangle if needed.
		if (drawOptions.contains(DrawOption.SELECTION) && (selectionRect != null)) {
			g2d.setColor(Color.white);
			g2d.draw(selectionRect);
			g2d.setColor(Color.black);
			g2d.setStroke(Constants.dashedStroke);
			g2d.draw(selectionRect);
		}

		// // Draw component area for test
		// g2d.setStroke(new BasicStroke());
		// g2d.setColor(Color.red);
		// for (Area area : componentAreaMap.values()) {
		// g2d.draw(area);
		// }
	}

	@Override
	public void injectGUIComponent(JComponent component, int position) throws BadPositionException {
		view.addComponent(component, position);
	}

	@Override
	public void injectMenuAction(Action action, String menuName) {
		view.addMenuAction(action, menuName);
	}

	// @Override
	// public void setCursorIcon(Icon icon) {
	// view.setCursorIcon(icon);
	// }

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
		return components;
	}

	@Override
	public void mouseClicked(Point point, boolean ctrlDown, boolean shiftDown, boolean altDown) {
		LOG.debug(String
				.format("mouseClicked(%s, %s, %s, %s)", point, ctrlDown, shiftDown, altDown));
		Point scaledPoint = scalePoint(point);
		if (componentSlot != null) {
			try {
				instantiateComponent(componentSlot.getInstanceClass(), componentSlot.getName(),
						componentSlot.getNamePrefix(), scaledPoint);
			} catch (Exception e) {
				LOG.error("Error instatiating component of type: "
						+ componentSlot.getInstanceClass().getName());
			}
			setNewComponentSlot(null);
		} else {
			List<IDIYComponent<?>> components = findComponentsAt(scaledPoint);
			// If there's nothing under mouse cursor deselect all.
			if (components.isEmpty()) {
				selectedComponents.clear();
			} else {
				IDIYComponent<?> component = components.get(0);
				// If ctrl is pressed just toggle the component under mouse
				// cursor.
				if (ctrlDown) {
					if (selectedComponents.contains(component)) {
						selectedComponents.remove(component);
					} else {
						selectedComponents.add(component);
					}
				} else {
					// Otherwise just select that one component.
					selectedComponents.clear();
					selectedComponents.add(component);
				}
			}
			messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
			messageDispatcher.dispatchMessage(EventType.REPAINT);
		}
	}

	@Override
	public void mouseMoved(Point point, boolean ctrlDown, boolean shiftDown, boolean altDown) {
		Map<IDIYComponent<?>, Integer> components = new HashMap<IDIYComponent<?>, Integer>();
		Point scaledPoint = scalePoint(point);
		for (IDIYComponent<?> component : currentProject.getComponents()) {
			for (int i = 0; i < component.getControlPointCount(); i++) {
				Point controlPoint = component.getControlPoint(i);
				if (shouldShowControlPointsFor(component)) {
					try {
						if (scaledPoint.distance(controlPoint) < CONTROL_POINT_SENSITIVITY) {
							components.put(component, i);
							break;
						}
					} catch (Exception e) {
						LOG.warn("Error reading control point for component of type: "
								+ component.getClass().getName());
					}
				}
			}
			// If CTRL is pressed, we only care about the top most component.
			if (altDown && components.size() > 0) {
				break;
			}
		}
		if (!components.equals(componentControlPointMap)) {
			componentControlPointMap = components;
			messageDispatcher.dispatchMessage(EventType.AVAILABLE_CTRL_POINTS_CHANGED, components);
		}
	}

	@Override
	public ComponentSelection getSelectedComponents() {
		return new ComponentSelection(selectedComponents);
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
		if (!componentControlPointMap.isEmpty()) {
			// If we're dragging control points reset selection.
			selectedComponents.clear();
			selectedComponents.addAll(componentControlPointMap.keySet());
			messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
			messageDispatcher.dispatchMessage(EventType.REPAINT);
		} else if (components.isEmpty()) {
			// If there are no components are under the cursor, reset selection.
			selectedComponents.clear();
			messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
			messageDispatcher.dispatchMessage(EventType.REPAINT);
		} else {
			IDIYComponent<?> component = components.get(0);
			// If the component under the cursor is not already selected, make
			// it into the only selected component.
			if (!selectedComponents.contains(component)) {
				selectedComponents.clear();
				selectedComponents.add(component);
				messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
				messageDispatcher.dispatchMessage(EventType.REPAINT);
			}
		}
	}

	@Override
	public boolean dragOver(Point point) {
		if (point == null) {
			return false;
		}
		Point scaledPoint = scalePoint(point);
		if (!componentControlPointMap.isEmpty()) {
			// We're dragging control point(s).
			IDIYComponent<?> firstComponent = componentControlPointMap.keySet().iterator().next();
			Point controlPoint = firstComponent.getControlPoint(componentControlPointMap
					.get(firstComponent));
			if (controlPoint == null) {
				LOG.warn("Control point not found in the map!");
			}
			int x = (int) (Math.round((controlPoint.x + scaledPoint.x - previousDragPoint.x)
					/ Constants.GRID) * Constants.GRID);
			int y = (int) (Math.round((controlPoint.y + scaledPoint.y - previousDragPoint.y)
					/ Constants.GRID) * Constants.GRID);
			previousDragPoint.setLocation(x, y);

			// Update all points.
			for (Map.Entry<IDIYComponent<?>, Integer> entry : componentControlPointMap.entrySet()) {
				Point p = new Point(x, y);
				entry.getKey().setControlPoint(p, entry.getValue());
			}
			// dragStartPoint = point;
		} else if (selectedComponents.isEmpty()) {
			// If there's no selection, the only thing to do is update the
			// selection rectangle and refresh.
			this.selectionRect = Utils.createRectangle(scaledPoint, previousDragPoint);
			// messageDispatcher.dispatchMessage(EventType.SELECTION_RECT_CHANGED,
			// selectionRect);
		} else {
			// If there are components selected translate their control points.
			translateSelectedComponents(previousDragPoint, scaledPoint);
			// dragStartPoint = point;
		}
		messageDispatcher.dispatchMessage(EventType.REPAINT);
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
				if ((area != null) && area.intersects(selectionRect)) {
					selectedComponents.add(component);
				}
			}
			selectionRect = null;
			messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED, selectedComponents);
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

	private void translateSelectedComponents(Point fromPoint, Point toPoint) {
		if (toPoint == null) {
			LOG.debug("Drag ended outside the drawing area.");
			return;
		}
		int dx = (int) (Math.round((toPoint.x - fromPoint.x) / zoomLevel / Constants.GRID) * Constants.GRID);
		int dy = (int) (Math.round((toPoint.y - fromPoint.y) / zoomLevel / Constants.GRID) * Constants.GRID);
		fromPoint.translate(dx, dy);
		for (IDIYComponent<?> component : selectedComponents) {
			for (int i = 0; i < component.getControlPointCount(); i++) {
				Point controlPoint = component.getControlPoint(i);
				translateControlPoint(controlPoint, dx, dy);
				component.setControlPoint(controlPoint, i);
			}
		}
	}

	private void translateControlPoint(Point controlPoint, int dx, int dy) {
		int x = controlPoint.x + dx;
		int y = controlPoint.y + dy;
		controlPoint.setLocation(x, y);
	}

	@Override
	public void addComponents(List<IDIYComponent<?>> components, Point preferredPoint) {
		LOG.debug(String.format("addComponents(%s)", components));
		Project oldProject = cloner.deepClone(currentProject);
		currentProject.getComponents().addAll(components);
		messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, cloner
				.deepClone(currentProject), "Add");
		messageDispatcher.dispatchMessage(EventType.REPAINT);
	}

	public boolean isLayerLocked(ComponentLayer layer) {
		return lockedLayers.contains(layer);
	}

	public void setLayerLocked(ComponentLayer layer, boolean locked) {
		LOG.debug(String.format("setLayerLocked(%s, %s)", layer, locked));
		if (locked) {
			lockedLayers.add(layer);
		} else {
			lockedLayers.remove(layer);
		}
	}

	public boolean isLayerVisible(ComponentLayer layer) {
		return visibleLayers.contains(layer);
	}

	public void setLayerVisible(ComponentLayer layer, boolean visible) {
		LOG.debug(String.format("setLayerVisible(%s, %s)", layer, visible));
		if (visible) {
			visibleLayers.add(layer);
		} else {
			visibleLayers.remove(layer);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	// @Override
	// public Object getDefaultPropertyValue(
	// Class<? extends IDIYComponent> componentClass,
	// String propertyName) {
	// return configuration.getDefaultPropertyValues().get(
	// componentClass.getName() + ":" + propertyName);
	// }
	public void setDefaultPropertyValue(String propertyName, Object value) {
		LOG.debug(String.format("setDefaultPropertyValue(%s, %s)", propertyName, value));
		Map<String, Object> defaultMap = (Map<String, Object>) ConfigurationManager.getInstance()
				.getConfigurationItem(DEFAULTS_KEY);
		if (defaultMap == null) {
			defaultMap = new HashMap<String, Object>();
			ConfigurationManager.getInstance().setConfigurationItem(DEFAULTS_KEY, defaultMap);
		}
		for (IDIYComponent component : selectedComponents) {
			String className = component.getClass().getName();
			LOG.debug("Default property value set for " + className + ":" + propertyName);
			defaultMap.put(className + ":" + propertyName, value);
		}
	}

	@SuppressWarnings("unchecked")
	private void instantiateComponent(Class<? extends IDIYComponent> componentClass,
			String componentTypeName, String namePrefix, Point point) throws Exception {
		LOG.info("Instatiating component of type: " + componentClass.getName());

		Project oldProject = cloner.deepClone(currentProject);

		// Instantiate the component.
		IDIYComponent component = componentClass.newInstance();

		// Find the next available componentName for the component.
		int i = 0;
		boolean exists = true;
		while (exists) {
			i++;
			String name = namePrefix + i;
			exists = false;
			for (IDIYComponent c : currentProject.getComponents()) {
				if (c.getName().equals(name)) {
					exists = true;
					break;
				}
			}
		}
		component.setName(namePrefix + i);

		// Add it to the project.
		currentProject.getComponents().add(component);

		// Translate them to the desired location.
		if (point != null) {
			for (int j = 0; j < component.getControlPointCount(); j++) {
				Point controlPoint = component.getControlPoint(j);
				int x = controlPoint.x + point.x;
				int y = controlPoint.y + point.y;
				x = (int) (Math.round(x / Constants.GRID) * Constants.GRID);
				y = (int) (Math.round(y / Constants.GRID) * Constants.GRID);
				controlPoint.setLocation(x, y);
				component.setControlPoint(controlPoint, j);
			}
		}

		// Extract properties.
		List<PropertyWrapper> properties = ComponentProcessor.getInstance().extractProperties(
				componentClass);
		// Override with default values if available.
		for (PropertyWrapper property : properties) {
			Object defaultValue = null;
			Map<String, Object> defaultMap = (Map<String, Object>) ConfigurationManager
					.getInstance().getConfigurationItem(DEFAULTS_KEY);
			if (defaultMap != null) {
				defaultValue = defaultMap.get(componentClass.getName() + ":" + property.getName());
			}
			if (defaultValue != null) {
				property.setValue(cloner.deepClone(defaultValue));
				property.writeTo(component);
			}
		}

		// Notify the listeners.
		if (!oldProject.equals(currentProject)) {
			messageDispatcher.dispatchMessage(EventType.PROJECT_MODIFIED, oldProject, cloner
					.deepClone(currentProject), "Add " + componentTypeName);
		}
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
		}
	}

	@Override
	public void setNewComponentSlot(ComponentType componentType) {
		LOG.debug(String.format("setNewComponentSlot(%s)", componentType == null ? null
				: componentType.getName()));
		this.componentSlot = componentType;
		selectedComponents.clear();
		messageDispatcher.dispatchMessage(EventType.SELECTION_CHANGED);
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

	private boolean shouldShowControlPointsFor(IDIYComponent<?> component) {
		return selectedComponents.contains(component);
		// return
		// controlPoint.getVisibilityPolicy().equals(VisibilityPolicy.ALWAYS)
		// ||
		// ((controlPoint.getVisibilityPolicy().equals(VisibilityPolicy.WHEN_SELECTED))
		// && (getSelectedComponents()
		// .contains(component)));
	}
}
