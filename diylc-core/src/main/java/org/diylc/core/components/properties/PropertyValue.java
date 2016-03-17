package org.diylc.core.components.properties;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyValue {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyValue.class);

    private final PropertyDescriptor propertyDescriptor;

    private final Object property;

    private Object value;

    public PropertyValue(PropertyDescriptor propertyDescriptor, Object property) {
        this.propertyDescriptor = propertyDescriptor;
        this.property = property;
        try {
            this.value = readValue();
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | SecurityException | NoSuchMethodException e) {
            LOG.error("Error reading property value", e);
        }
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public Object getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void writeValue(Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        Object target = getTarget(property);
        target.getClass().getMethod(propertyDescriptor.getSetter(), propertyDescriptor.getType()).invoke(target, value);
    }

    private Object readValue() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException,
            NoSuchMethodException {
        Object target = getTarget(property);
        return target.getClass().getMethod(propertyDescriptor.getGetter()).invoke(target);
    }

    private Object getTarget(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        Object target = object;

        if (propertyDescriptor.getTargetName() != null) {
            target = object
                    .getClass()
                    .getMethod(
                            "get" + propertyDescriptor.getTargetName().substring(0, 1).toUpperCase()
                                    + propertyDescriptor.getTargetName().substring(1)).invoke(object);
        }

        return target;
    }

}
