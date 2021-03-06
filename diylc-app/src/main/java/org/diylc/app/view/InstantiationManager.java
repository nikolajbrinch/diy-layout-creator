package org.diylc.app.view;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.app.utils.CalcUtils;
import org.diylc.components.registry.ComponentProcessor;
import org.diylc.core.ComponentType;
import org.diylc.core.CreationMethod;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Orientation;
import org.diylc.core.OrientationHV;
import org.diylc.core.Project;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.Template;
import org.diylc.core.config.Configuration;
import org.diylc.core.measures.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages component instantiation.
 * 
 * @author Branislav Stojkovic
 */
public class InstantiationManager {

	private static final Logger LOG = LoggerFactory.getLogger(InstantiationManager.class);

	public static int MAX_RECENT_COMPONENTS = 16;
	
	private ComponentProcessor componentProcessor;
	
	private ComponentType componentTypeSlot;
	
	private Template template;
	
	private List<IDIYComponent> componentSlot;
	
	private Point firstControlPoint;
	
	private Point potentialControlPoint;

	private static final ComponentType clipboardType = new ComponentType(
			"Clipboard contents", "Components from the clipboard",
			CreationMethod.SINGLE_CLICK, "Multi", "", "", null, null, 0, false,
			false, null, false, true);

	public InstantiationManager(ComponentProcessor componentProcessor) {
        this.componentProcessor = componentProcessor;
	}

	public ComponentType getComponentTypeSlot() {
		return componentTypeSlot;
	}

	public Template getTemplate() {
		return template;
	}

	public List<IDIYComponent> getComponentSlot() {
		return componentSlot;
	}

	public Point getFirstControlPoint() {
		return firstControlPoint;
	}

	public Point getPotentialControlPoint() {
		return potentialControlPoint;
	}

	public void setComponentTypeSlot(ComponentType componentTypeSlot,
			Template template, Project currentProject) throws Exception {
		this.componentTypeSlot = componentTypeSlot;
		this.template = template;
		if (componentTypeSlot == null) {
			this.componentSlot = null;
		} else {
			switch (componentTypeSlot.getCreationMethod()) {
			case POINT_BY_POINT:
				this.componentSlot = null;
				break;
			case SINGLE_CLICK:
				this.componentSlot = instantiateComponent(componentTypeSlot,
						template, new Point(0, 0), currentProject);
				break;
			}
		}
		this.firstControlPoint = null;
		this.potentialControlPoint = null;
	}

	public void instatiatePointByPoint(Point scaledPoint, Project currentProject)
			throws Exception {
		firstControlPoint = scaledPoint;
		componentSlot = instantiateComponent(componentTypeSlot, template,
				firstControlPoint, currentProject);

		// Set the other control point to the same location, we'll
		// move it later when mouse moves.
		componentSlot.get(0).setControlPoint(firstControlPoint, 0);
		componentSlot.get(0).setControlPoint(firstControlPoint, 1);
	}

	/**
	 * Updates component in the slot with the new second control point.
	 * 
	 * @param scaledPoint
	 * @return true, if any change is made
	 */
	public boolean updatePointByPoint(Point scaledPoint) {
		boolean changeMade = !scaledPoint.equals(potentialControlPoint);
		potentialControlPoint = scaledPoint;
		if (componentSlot != null && !componentSlot.isEmpty()) {
			componentSlot.get(0).setControlPoint(scaledPoint, 1);
		}
		return changeMade;
	}

	public void pasteComponents(List<IDIYComponent> components,
			Point scaledPoint, boolean snapToGrid, Size gridSpacing) {
		// Adjust location of components so they are centered under the mouse
		// cursor
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
		if (snapToGrid) {
			x = CalcUtils.roundToGrid(x, gridSpacing);
			x = CalcUtils.roundToGrid(x, gridSpacing);
		}
		for (IDIYComponent component : components) {
			for (int i = 0; i < component.getControlPointCount(); i++) {
				Point p = component.getControlPoint(i);
				p.translate(-x, -y);
				component.setControlPoint(p, i);
			}
		}

		// Update component slot
		this.componentSlot = new ArrayList<IDIYComponent>(components);

		// Update the component type slot so the app knows that something's
		// being instantiated.
		this.componentTypeSlot = clipboardType;

		if (snapToGrid) {
			scaledPoint = new Point(scaledPoint);
			CalcUtils.snapPointToGrid(scaledPoint, gridSpacing);
		}
		// Update the location according to mouse location
		updateSingleClick(scaledPoint, snapToGrid, gridSpacing);
	}

	/**
	 * Updates location of component slot based on the new mouse location.
	 * 
	 * @param scaledPoint
	 * @param snapToGrid
	 * @param gridSpacing
	 * @return true if we need to refresh the canvas
	 */
	public boolean updateSingleClick(Point scaledPoint, boolean snapToGrid,
			Size gridSpacing) {
		if (potentialControlPoint == null) {
			potentialControlPoint = new Point(0, 0);
		}
		if (scaledPoint == null) {
			scaledPoint = new Point(0, 0);
		}
		int dx = scaledPoint.x - potentialControlPoint.x;
		int dy = scaledPoint.y - potentialControlPoint.y;
		if (snapToGrid) {
			dx = CalcUtils.roundToGrid(dx, gridSpacing);
			dy = CalcUtils.roundToGrid(dy, gridSpacing);
		}
		// Only repaint if there's an actual change.
		if (dx == 0 && dy == 0) {
			return false;
		}
		potentialControlPoint.translate(dx, dy);
		if (componentSlot == null) {
			LOG.error("Component slot should not be null!");
		} else {
			Point p = new Point();
			for (IDIYComponent component : componentSlot) {
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

	public List<IDIYComponent> instantiateComponent(
			ComponentType componentType, Template template, Point point,
			Project currentProject) throws Exception {
		LOG.trace("Instatiating component of type: "
				+ componentType.getInstanceClass().getName());

		/* 
		 * Instantiate the component.
		 */
		IDIYComponent component = componentType.getInstanceClass()
				.newInstance();
		component.setComponentType(componentType);

		component.setName(createUniqueName(componentType, currentProject));

		// Translate them to the desired location.
		if (point != null) {
			for (int j = 0; j < component.getControlPointCount(); j++) {
				Point controlPoint = new Point(component.getControlPoint(j));
				controlPoint.translate(point.x, point.y);
				// snapPointToGrid(controlPoint);
				component.setControlPoint(controlPoint, j);
			}
		}

		loadComponentShapeFromTemplate(component, template);

		fillWithDefaultProperties(component, template);

		// Write to recent components
		List<String> recentComponentTypes = Configuration.INSTANCE.getRecentComponents();
		String className = componentType.getInstanceClass().getName();
		if (recentComponentTypes.size() == 0
				|| !recentComponentTypes.get(0).equals(className)) {
			// Remove if it's already somewhere in the list.
			recentComponentTypes.remove(className);
			// Add to the end of the list.
			recentComponentTypes.add(0, className);
			// Trim the list if necessary.
			if (recentComponentTypes.size() > MAX_RECENT_COMPONENTS) {
				recentComponentTypes.remove(recentComponentTypes.size() - 1);
			}
			Configuration.INSTANCE.setRecentComponents(recentComponentTypes);
		}

		List<IDIYComponent> list = new ArrayList<IDIYComponent>();
		list.add(component);
		return list;
	}

	public String createUniqueName(ComponentType componentType,
			Project currentProject) {
		boolean exists = true;
		List<IDIYComponent> components = currentProject.getComponents();
		String[] takenNames = new String[components.size()];
		for (int j = 0; j < currentProject.getComponents().size(); j++) {
			takenNames[j] = components.get(j).getName();
		}
		Arrays.sort(takenNames);
		int i = 0;
		while (exists) {
			i++;
			String name = componentType.getNamePrefix() + i;
			exists = false;
			if (Arrays.binarySearch(takenNames, name) >= 0) {
				exists = true;
			}
		}
		return componentType.getNamePrefix() + i;
	}

	/**
	 * Finds any properties that have default values and injects default values.
	 * Typically it should be used for {@link IDIYComponent} and {@link Project}
	 * objects.
	 * 
	 * @param object
	 * @param template
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void fillWithDefaultProperties(Object object, Template template)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException, NoSuchMethodException {
		// Extract properties.
		List<PropertyWrapper> properties = componentProcessor.extractProperties(object.getClass());
		Map<String, PropertyWrapper> propertyCache = new HashMap<String, PropertyWrapper>();
		// Override with default values if available.
		for (PropertyWrapper property : properties) {
			propertyCache.put(property.getName(), property);
			Map<String, Map<String, Object>> objectProperties = Configuration.INSTANCE.getObjectProperties();
			Map<String, Object> objectValues = objectProperties.get(object.getClass().getName());
			Object defaultValue = null;
			if (objectValues != null) {
				defaultValue = objectValues.get(property.getName());
			}
			if (defaultValue != null) {
				property.setValue(defaultValue);
				property.writeTo(object);
			}
		}
		if (template != null) {
			for (Map.Entry<String, Object> pair : template.getValues()
					.entrySet()) {
				PropertyWrapper property = propertyCache.get(pair.getKey());
				if (property == null) {
					LOG.warn("Cannot find property " + pair.getKey());
				} else {
					LOG.debug("Filling value from template for "
							+ pair.getKey());
					property.setValue(pair.getValue());
					property.writeTo(object);
				}
			}
		}
	}

	/**
	 * Uses stored control points from the template to shape component.
	 * 
	 * @param component
	 * @param template
	 */
	public void loadComponentShapeFromTemplate(IDIYComponent component,
			Template template) {
		if (template != null
				&& template.getPoints() != null
				&& template.getPoints().size() >= component
						.getControlPointCount()) {
			for (int i = 0; i < component.getControlPointCount(); i++) {
				Point p = new Point(component.getControlPoint(0));
				p.translate(template.getPoints().get(i).x, template.getPoints()
						.get(i).y);
				component.setControlPoint(p, i);
			}
		}
	}

	public void tryToRotateComponentSlot() {
		if (this.componentSlot == null) {
			LOG.debug("Component slot is empty, cannot rotate");
			return;
		}
		List<PropertyWrapper> properties = componentProcessor.extractProperties(this.componentTypeSlot.getInstanceClass());
		PropertyWrapper angleProperty = null;
		for (PropertyWrapper propertyWrapper : properties) {
			if (propertyWrapper.getType().getName().equals(
					Orientation.class.getName())
					|| propertyWrapper.getType().getName().equals(
							OrientationHV.class.getName())) {
				angleProperty = propertyWrapper;
				break;
			}
		}
		if (angleProperty == null) {
			LOG
					.debug("Component in the slot does not have a property of type Orientation, cannot rotate");
			return;
		}
		try {
			for (IDIYComponent component : this.componentSlot) {
				angleProperty.readFrom(component);
				Object value = angleProperty.getValue();
				if (value instanceof Orientation) {
					angleProperty
							.setValue(Orientation.values()[(((Orientation) value)
									.ordinal() + 1)
									% Orientation.values().length]);
				} else if (value instanceof OrientationHV) {
					angleProperty
							.setValue(OrientationHV.values()[(((OrientationHV) value)
									.ordinal() + 1)
									% OrientationHV.values().length]);
				}
				angleProperty.writeTo(component);
			}
		} catch (Exception e) {
			LOG.warn("Error trying to rotate the component", e);
		}
	}
}
