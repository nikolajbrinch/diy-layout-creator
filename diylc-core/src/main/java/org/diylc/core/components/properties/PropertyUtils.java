package org.diylc.core.components.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.IDIYComponent;
import org.diylc.core.components.registry.ComponentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertyUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(PropertyUtils.class);

    private ComponentRegistry componentRegistry;
    
    public PropertyUtils(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }
    
    /**
     * Returns properties that have the same value for the passed in components.
     * 
     * @param components
     * @return
     */
    public List<PropertyDescriptor> extractMutualProperties(List<IDIYComponent> components) throws Exception {
        if (components.isEmpty()) {
            return null;
        }

        List<IDIYComponent> copyComponents = new ArrayList<>(components);

        List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();

        IDIYComponent firstComponent = copyComponents.remove(0);

        properties.addAll(getPropertyDescriptorsForComponent(firstComponent));

        readProperties(properties, firstComponent);

        for (IDIYComponent component : copyComponents) {
            List<PropertyDescriptor> newProperties = getPropertyDescriptorsForComponent(component);

            readProperties(newProperties, component);

            properties.retainAll(newProperties);

            LOG.debug("Mutual properties for \n\t"
                    + String.join(",\n\t", components.stream().map((c) -> c.getName()).collect(Collectors.toList())) + " : \n\t\t"
                    + String.join(",\n\t\t", properties.stream().map((p) -> p.getName()).collect(Collectors.toList())));

            /*
             * Try to find matching properties in old and new lists and see if
             * their values match.
             */
            for (PropertyDescriptor oldProperty : properties) {
                if (newProperties.contains(oldProperty)) {
                    PropertyDescriptor newProperty = newProperties.get(newProperties.indexOf(oldProperty));

                    oldProperty.setUnique(isUnique(newProperty, oldProperty));
                }
            }
        }

        Collections.sort(properties, new Comparator<PropertyDescriptor>() {

            @Override
            public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }

        });

        return properties;
    }
    
    private List<PropertyDescriptor> getPropertyDescriptorsForComponent(IDIYComponent component) {
        ComponentModel componentModel = component.getComponentModel();
        
        return componentModel.getPropertyDescriptors();
    }

    private void readProperties(List<PropertyDescriptor> properties, IDIYComponent component) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        for (PropertyDescriptor property : properties) {
            property.readFrom(component);
        }
    }

    private boolean isUnique(PropertyValue newProperty, PropertyValue oldProperty) {
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

    public static List<PropertyDescriptor> cloneProperties(List<PropertyDescriptor> properties) {
        List<PropertyDescriptor> result = new ArrayList<PropertyDescriptor>(properties.size());

        for (PropertyDescriptor propertyDescriptor : properties) {
            try {
                result.add((PropertyDescriptor) propertyDescriptor.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

}
