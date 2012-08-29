package org.diylc.presenter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.diylc.common.ComponentType;
import org.diylc.common.PropertyWrapper;
import org.diylc.core.CreationMethod;
import org.diylc.core.IDIYComponent;
import org.diylc.core.IPropertyValidator;
import org.diylc.core.annotations.BomPolicy;
import org.diylc.core.annotations.ComponentDescriptor;
import org.diylc.core.annotations.EditableProperty;

/**
 * Utility class with component processing methods.
 * 
 * @author Branislav Stojkovic
 */
public class ComponentProcessor {

	private static final Logger LOG = Logger
			.getLogger(ComponentProcessor.class);

	private static ComponentProcessor instance;

	private Map<String, List<PropertyWrapper>> propertyCache;
	private Map<String, IPropertyValidator> propertyValidatorCache;
	private Map<String, ComponentType> componentTypeMap;

	public static ComponentProcessor getInstance() {
		if (instance == null) {
			instance = new ComponentProcessor();
		}
		return instance;
	}

	private ComponentProcessor() {
		super();
		this.propertyCache = new HashMap<String, List<PropertyWrapper>>();
		this.componentTypeMap = new HashMap<String, ComponentType>();
		this.propertyValidatorCache = new HashMap<String, IPropertyValidator>();
	}

	public ComponentType extractComponentTypeFrom(
			Class<? extends IDIYComponent<?>> clazz) {
		if (componentTypeMap.containsKey(clazz.getName())) {
			return componentTypeMap.get(clazz.getName());
		}
		String name;
		String description;
		CreationMethod creationMethod;
		String category;
		String namePrefix;
		String author;
		Icon icon;
		double zOrder;
		boolean flexibleZOrder;
		boolean stretchable;
		BomPolicy bomPolicy;
		boolean autoEdit;
		if (clazz.isAnnotationPresent(ComponentDescriptor.class)) {
			ComponentDescriptor annotation = clazz
					.getAnnotation(ComponentDescriptor.class);
			name = annotation.name();
			description = annotation.description();
			creationMethod = annotation.creationMethod();
			category = annotation.category();
			namePrefix = annotation.instanceNamePrefix();
			author = annotation.author();
			zOrder = annotation.zOrder();
			flexibleZOrder = annotation.flexibleZOrder();
			stretchable = annotation.stretchable();
			bomPolicy = annotation.bomPolicy();
			autoEdit = annotation.autoEdit();
		} else {
			name = clazz.getSimpleName();
			description = "";
			creationMethod = CreationMethod.SINGLE_CLICK;
			category = "Uncategorized";
			namePrefix = "Unknown";
			author = "Unknown";
			zOrder = IDIYComponent.COMPONENT;
			flexibleZOrder = false;
			stretchable = true;
			bomPolicy = BomPolicy.SHOW_ALL_NAMES;
			autoEdit = true;
		}
		icon = null;
		// Draw component icon.
		try {
			IDIYComponent<?> componentInstance = (IDIYComponent<?>) clazz
					.newInstance();
			Image image = new BufferedImage(Presenter.ICON_SIZE,
					Presenter.ICON_SIZE,
					java.awt.image.BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_PURE);
			componentInstance.drawIcon(g2d, Presenter.ICON_SIZE,
					Presenter.ICON_SIZE);
			icon = new ImageIcon(image);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		ComponentType componentType = new ComponentType(name, description,
				creationMethod, category, namePrefix, author, icon, clazz,
				zOrder, flexibleZOrder, stretchable, bomPolicy, autoEdit);
		componentTypeMap.put(clazz.getName(), componentType);
		return componentType;
	}

	/**
	 * Extracts all editable properties from the component class.
	 * 
	 * @param clazz
	 * @return
	 */
	public List<PropertyWrapper> extractProperties(Class<?> clazz) {
		if (propertyCache.containsKey(clazz.getName())) {
			return cloneProperties(propertyCache.get(clazz.getName()));
		}
		List<PropertyWrapper> properties = new ArrayList<PropertyWrapper>();
		for (Method method : clazz.getMethods()) {
			if (method.getName().startsWith("get")) {
				try {
					if (method.isAnnotationPresent(EditableProperty.class)
							&& !method.isAnnotationPresent(Deprecated.class)) {
						EditableProperty annotation = method
								.getAnnotation(EditableProperty.class);
						String name;
						if (annotation.name().equals("")) {
							name = method.getName().substring(3);
						} else {
							name = annotation.name();
						}
						IPropertyValidator validator = getPropertyValidator(annotation
								.validatorClass());
						Method setter = clazz.getMethod("set"
								+ method.getName().substring(3), method
								.getReturnType());
						PropertyWrapper property = new PropertyWrapper(name,
								method.getReturnType(), method.getName(),
								setter.getName(), annotation.defaultable(),
								validator);
						properties.add(property);
					}
				} catch (NoSuchMethodException e) {
					LOG.debug("No matching setter found for \""
							+ method.getName() + "\". Skipping...");
				}
			}
		}

		propertyCache.put(clazz.getName(), properties);
		return cloneProperties(properties);
	}
	
	private List<PropertyWrapper> cloneProperties(List<PropertyWrapper> properties) {
		List<PropertyWrapper> result = new ArrayList<PropertyWrapper>(properties.size());
		for (PropertyWrapper propertyWrapper : properties) {
			try {
				result.add((PropertyWrapper) propertyWrapper.clone());
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * Returns properties that have the same value for all the selected
	 * components.
	 * 
	 * @param selectedComponents
	 * @return
	 */
	public List<PropertyWrapper> getMutualSelectionProperties(
			List<IDIYComponent<?>> selectedComponents) throws Exception {
		if (selectedComponents.isEmpty()) {
			return null;
		}
		List<PropertyWrapper> properties = new ArrayList<PropertyWrapper>();
		IDIYComponent<?> firstComponent = selectedComponents.get(0);
		properties.addAll(extractProperties(firstComponent.getClass()));
		// Initialize values
		for (PropertyWrapper property : properties) {
			property.readFrom(firstComponent);
		}
		for (int i = 1; i < selectedComponents.size(); i++) {
			IDIYComponent<?> component = selectedComponents.get(i);
			List<PropertyWrapper> newProperties = extractProperties(component
					.getClass());
			for (PropertyWrapper property : newProperties) {
				property.readFrom(component);
			}
			properties.retainAll(newProperties);
			// Try to find matching properties in old and new lists and see if
			// their values match.
			for (PropertyWrapper oldProperty : properties) {
				if (newProperties.contains(oldProperty)) {
					PropertyWrapper newProperty = newProperties
							.get(newProperties.indexOf(oldProperty));
					if ((newProperty.getValue() != null && newProperty
							.getValue() == null)
							|| !newProperty.getValue().equals(
									oldProperty.getValue())) {
						// Values don't match, so the property is not unique
						// valued.
						oldProperty.setUnique(false);
					}
				}
			}
			// for (PropertyWrapper property : properties) {
			// try {
			// property.readUniqueFrom(component);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
		}
		Collections.sort(properties, ComparatorFactory.getInstance()
				.getPropertyNameComparator());
		return properties;
	}

	private IPropertyValidator getPropertyValidator(
			Class<? extends IPropertyValidator> clazz) {
		if (propertyValidatorCache.containsKey(clazz.getName())) {
			return propertyValidatorCache.get(clazz.getName());
		}
		IPropertyValidator validator;
		try {
			validator = clazz.newInstance();
		} catch (Exception e) {
			LOG.error("Could not instantiate validator for " + clazz.getName(),
					e);
			return null;
		}
		propertyValidatorCache.put(clazz.getName(), validator);
		return validator;
	}
}
