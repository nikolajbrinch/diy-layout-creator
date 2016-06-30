package org.diylc.core.components.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.diylc.core.ComparatorFactory;
import org.diylc.core.IDIYComponent;
import org.diylc.core.PropertyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class with component processing methods.
 * 
 * @author Branislav Stojkovic
 */
public class ComponentProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentProcessor.class);

    private final PropertyExtractor propertyExtractor;

    public ComponentProcessor(ComponentRegistry componentRegistry) {
        propertyExtractor = new PropertyExtractor(componentRegistry);
    }

    public PropertyExtractor getPropertyExtractor() {
        return propertyExtractor;
    }

    /*
     * XXX: This method should go away! This is responsibility of
     * PropertyExtractor - no proxy method should exist.
     */
    public List<PropertyWrapper> extractProperties(Class<?> clazz) {
        return getPropertyExtractor().extractProperties(clazz);
    }

    /**
     * Returns properties that have the same value for the passed in components.
     * 
     * @param components
     * @return
     */
    public List<PropertyWrapper> getMutualProperties(List<IDIYComponent> components) throws Exception {
        if (components.isEmpty()) {
            return null;
        }

        List<IDIYComponent> copyComponents = new ArrayList<>(components);

        List<PropertyWrapper> properties = new ArrayList<PropertyWrapper>();

        IDIYComponent firstComponent = copyComponents.remove(0);

        properties.addAll(getPropertyExtractor().extractProperties(firstComponent.getClass()));

        readProperties(properties, firstComponent);

        for (IDIYComponent component : copyComponents) {
            List<PropertyWrapper> newProperties = getPropertyExtractor().extractProperties(component.getClass());

            readProperties(newProperties, component);

            properties.retainAll(newProperties);

            LOG.debug("Mutual properties for \n\t"
                    + String.join(",\n\t", components.stream().map((c) -> c.getName()).collect(Collectors.toList())) + " : \n\t\t"
                    + String.join(",\n\t\t", properties.stream().map((p) -> p.getName()).collect(Collectors.toList())));

            /*
             * Try to find matching properties in old and new lists and see if
             * their values match.
             */
            for (PropertyWrapper oldProperty : properties) {
                if (newProperties.contains(oldProperty)) {
                    PropertyWrapper newProperty = newProperties.get(newProperties.indexOf(oldProperty));

                    oldProperty.setUnique(isUnique(newProperty, oldProperty));
                }
            }
        }

        Collections.sort(properties, ComparatorFactory.getInstance().getPropertyNameComparator());

        return properties;
    }

    private void readProperties(List<PropertyWrapper> properties, IDIYComponent component) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        for (PropertyWrapper property : properties) {
            property.readFrom(component);
        }
    }

    private boolean isUnique(PropertyWrapper newProperty, PropertyWrapper oldProperty) {
        boolean isUnique = true;

        if (newProperty.getValue() != null && newProperty.getValue() != null) {
            if (!newProperty.getValue().equals(oldProperty.getValue()))
                /*
                 * Values don't match, so the property is not unique valued.
                 */
                isUnique = false;
        } else if ((newProperty.getValue() == null && oldProperty.getValue() != null)
                || (newProperty.getValue() != null && oldProperty.getValue() == null)) {
            isUnique = false;
        }

        return isUnique;
    }

}
