package org.diylc.app.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.components.registry.ComponentProcessor;
import org.diylc.core.IDIYComponent;
import org.diylc.core.Project;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.Template;
import org.diylc.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ModelUtils.class);
    
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
    public static void fillWithDefaultProperties(Object object, Template template)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        // Extract properties.
        List<PropertyWrapper> properties = ComponentProcessor.getInstance()
                .extractProperties(object.getClass());
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
}
