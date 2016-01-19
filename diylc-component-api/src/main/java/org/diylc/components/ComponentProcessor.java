package org.diylc.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.diylc.core.IDIYComponent;
import org.diylc.core.PropertyWrapper;

/**
 * Utility class with component processing methods.
 * 
 * @author Branislav Stojkovic
 */
public class ComponentProcessor {

    private PropertyExtractor propertyExtractor = new PropertyExtractor();

    private static ComponentProcessor instance = new ComponentProcessor();

    public static ComponentProcessor getInstance() {
        return instance;
    }
    
    public List<PropertyWrapper> extractProperties(Class<?> clazz) {
        return propertyExtractor.extractProperties(clazz);
    }

    /**
     * Returns properties that have the same value for all the selected
     * components.
     * 
     * @param selectedComponents
     * @return
     */
    public List<PropertyWrapper> getMutualSelectionProperties(List<IDIYComponent> selectedComponents) throws Exception {
        if (selectedComponents.isEmpty()) {
            return null;
        }
        List<PropertyWrapper> properties = new ArrayList<PropertyWrapper>();
        IDIYComponent firstComponent = selectedComponents.get(0);
        properties.addAll(extractProperties(firstComponent.getClass()));

        for (PropertyWrapper property : properties) {
            property.readFrom(firstComponent);
        }

        for (IDIYComponent component : selectedComponents) {
            List<PropertyWrapper> newProperties = extractProperties(component.getClass());

            for (PropertyWrapper property : newProperties) {
                property.readFrom(component);
            }
            properties.retainAll(newProperties);

            /*
             * Try to find matching properties in old and new lists and see if
             * their values match.
             */
            for (PropertyWrapper oldProperty : properties) {
                if (newProperties.contains(oldProperty)) {
                    PropertyWrapper newProperty = newProperties.get(newProperties.indexOf(oldProperty));
                    if (newProperty.getValue() != null && newProperty.getValue() != null) {
                        if (!newProperty.getValue().equals(oldProperty.getValue()))
                            /*
                             * Values don't match, so the property is not unique
                             * valued.
                             */
                            oldProperty.setUnique(false);
                    } else if ((newProperty.getValue() == null && oldProperty.getValue() != null)
                            || (newProperty.getValue() != null && oldProperty.getValue() == null)) {
                        oldProperty.setUnique(false);
                    }
                }
            }
        }

        Collections.sort(properties, ComparatorFactory.getInstance().getPropertyNameComparator());

        return properties;
    }

}
