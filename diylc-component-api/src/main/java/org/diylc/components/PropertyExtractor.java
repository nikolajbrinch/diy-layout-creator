package org.diylc.components;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.common.PropertyWrapper;
import org.diylc.core.IPropertyValidator;
import org.diylc.core.annotations.EditableProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyExtractor.class);

    private Map<String, List<PropertyWrapper>> propertyCache = new HashMap<String, List<PropertyWrapper>>();

    private Map<String, IPropertyValidator> propertyValidatorCache = new HashMap<String, IPropertyValidator>();

    /**
     * Extracts all editable properties from the component class.
     * 
     * @param clazz
     * @return
     */
    public List<PropertyWrapper> extractProperties(Class<?> clazz) {
        List<PropertyWrapper> properties = propertyCache.get(clazz.getName());

        if (properties == null) {
            properties = new ArrayList<PropertyWrapper>();

            properties.addAll(findOnFields(clazz));
            properties.addAll(findOnMethods(clazz));

            propertyCache.put(clazz.getName(), properties);
        }

        return cloneProperties(properties);
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();

        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }

        return fields;
    }

    private List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methodss = new ArrayList<Method>();

        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            methodss.addAll(Arrays.asList(c.getDeclaredMethods()));
        }

        return methodss;
    }

    private Collection<? extends PropertyWrapper> findOnFields(Class<?> clazz) {
        List<PropertyWrapper> properties = new ArrayList<PropertyWrapper>();
        
        List<Method> methods = getAllMethods(clazz);
        List<Field> fields = getAllFields(clazz);
        
        for (Field field : fields) {
            if (field.isAnnotationPresent(EditableProperty.class) && !field.isAnnotationPresent(Deprecated.class)) {
                EditableProperty annotation = field.getAnnotation(EditableProperty.class);

                String fieldName = field.getName();
                String beanPropertyName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                /*
                 * Resolve property name
                 */
                String propertyName = annotation.name();

                if (propertyName == null || propertyName.isEmpty()) {
                    propertyName = beanPropertyName;
                }

                /*
                 * Resolve setter
                 */
                String setterName = annotation.setter();

                if (setterName == null || setterName.isEmpty()) {
                    setterName = "set" + beanPropertyName;
                }

                Method setter = null;

                try {
                    setter = clazz.getMethod(setterName, field.getType());

                } catch (NoSuchMethodException e) {
                    LOG.debug("No matching setter found for \"" + propertyName + "\". Skipping...");
                }

                if (setter != null && !Modifier.isPublic(setter.getModifiers())) {
                    LOG.debug("Setter for property \"" + propertyName + "\" for class: " + clazz.getName() + " is not public. Skipping.");
                }

                /*
                 * Resolve getter
                 */
                String getterName = annotation.getter();

                if (getterName == null || getterName.isEmpty()) {
                    getterName = "get" + beanPropertyName;
                }

                Method getter = null;

                try {
                    getter = clazz.getMethod(getterName, new Class<?>[0]);
                } catch (NoSuchMethodException e) {
                    LOG.debug("No matching setter found for \"" + propertyName + "\". Skipping...");
                }

                if (getter != null && !Modifier.isPublic(getter.getModifiers())) {
                    throw new IllegalStateException("Getter for property \"" + propertyName + "\" for class: " + clazz.getName()
                            + " is not public");
                }

                if (setter != null && getter != null) {
                    IPropertyValidator validator = getPropertyValidator(annotation.validatorClass());

                    PropertyWrapper property = new PropertyWrapper(propertyName, field.getType(), getter.getName(), setter.getName(),
                            annotation.defaultable(), validator);
                    properties.add(property);
                }
            }
        }

        return properties;
    }

    private Collection<? extends PropertyWrapper> findOnMethods(Class<?> clazz) {
        List<PropertyWrapper> properties = new ArrayList<PropertyWrapper>();

        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    if (method.isAnnotationPresent(EditableProperty.class) && !method.isAnnotationPresent(Deprecated.class)) {
                        EditableProperty annotation = method.getAnnotation(EditableProperty.class);
                        String name;

                        if (annotation.name().equals("")) {
                            name = method.getName().substring(3);
                        } else {
                            name = annotation.name();
                        }

                        IPropertyValidator validator = getPropertyValidator(annotation.validatorClass());
                        Method setter = clazz.getMethod("set" + method.getName().substring(3), method.getReturnType());
                        PropertyWrapper property = new PropertyWrapper(name, method.getReturnType(), method.getName(), setter.getName(),
                                annotation.defaultable(), validator);
                        properties.add(property);
                    }
                } catch (NoSuchMethodException e) {
                    LOG.debug("No matching setter found for \"" + method.getName() + "\". Skipping...");
                }
            }
        }

        return properties;
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

    private IPropertyValidator getPropertyValidator(Class<? extends IPropertyValidator> clazz) {
        IPropertyValidator propertyValidator = propertyValidatorCache.get(clazz.getName());

        if (propertyValidator == null) {
            try {
                propertyValidator = clazz.newInstance();
            } catch (Exception e) {
                LOG.error("Could not instantiate validator for " + clazz.getName(), e);
                return null;
            }
            propertyValidatorCache.put(clazz.getName(), propertyValidator);
        }

        return propertyValidator;
    }

}
