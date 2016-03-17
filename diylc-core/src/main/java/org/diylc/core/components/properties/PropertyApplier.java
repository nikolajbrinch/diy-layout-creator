package org.diylc.core.components.properties;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.core.components.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.components.Template;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyApplier {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyApplier.class);

    private final PropertyDescriptorExtractor propertyDescriptorExtractor;

    public PropertyApplier(PropertyDescriptorExtractor propertyDescriptorExtractor) {
        this.propertyDescriptorExtractor = propertyDescriptorExtractor;
    }

    /**
     * Finds any properties that have default values and injects default values.
     * Typically it should be used for {@link IDIYComponent} and {@link Project}
     * objects.
     * 
     * @param component
     * @param template
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public void applyDefaultProperties(Object object, Template template) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        /* 
         * Extract properties.
         */
        List<PropertyDescriptor> properties = propertyDescriptorExtractor.extractProperties(object.getClass());
        
        Map<String, PropertyDescriptor> propertyCache = new HashMap<String, PropertyDescriptor>();
        
        /* 
         * Override with default values if available.
         */
        for (PropertyDescriptor property : properties) {
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
            for (Map.Entry<String, Object> pair : template.getValues().entrySet()) {
                PropertyDescriptor property = propertyCache.get(pair.getKey());
                
                if (property == null) {
                    LOG.warn("Cannot find property " + pair.getKey());
                } else {
                    LOG.debug("Filling value from template for " + pair.getKey());
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
    public void applyTemplateControlPoints(IDIYComponent component, Template template) {
        if (template != null && template.getPoints() != null && template.getPoints().size() >= component.getControlPointCount()) {
            for (int i = 0; i < component.getControlPointCount(); i++) {
                Point p = new Point(component.getControlPoint(0));
                p.translate(template.getPoints().get(i).x, template.getPoints().get(i).y);
                component.setControlPoint(p, i);
            }
        }
    }
}
