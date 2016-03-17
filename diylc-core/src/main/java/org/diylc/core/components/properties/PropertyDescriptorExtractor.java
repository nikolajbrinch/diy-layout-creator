package org.diylc.core.components.properties;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.core.components.SpecificationModel;
import org.diylc.core.components.registry.SpecificationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class with component processing methods.
 * 
 * @author Branislav Stojkovic
 */
public class PropertyDescriptorExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyDescriptorExtractor.class);

    private Map<String, List<PropertyDescriptor>> propertyCache = new HashMap<>();

    private Map<String, IPropertyValidator> propertyValidatorCache = new HashMap<String, IPropertyValidator>();

    private SpecificationRegistry specificationRegistry;

    public PropertyDescriptorExtractor(SpecificationRegistry specificationRegistry) {
        this.specificationRegistry = specificationRegistry;
    }
    
    /**
     * Extracts all editable properties from the component class.
     * 
     * @param clazz
     * @return
     */
    public List<PropertyDescriptor> extractProperties(Class<?> clazz) {
        return extractProperties(clazz, null);
    }

    public List<PropertyDescriptor> extractProperties(Class<?> clazz, String targetName) {
        List<PropertyDescriptor> properties = propertyCache.get(clazz.getName());

        if (properties == null) {
            properties = new ArrayList<PropertyDescriptor>();

            properties.addAll(findOnFields(clazz, targetName));
            properties.addAll(findOnMethods(clazz, targetName));

            propertyCache.put(clazz.getName(), properties);
        }

        return PropertyUtils.cloneProperties(properties);
    }

    private Collection<? extends PropertyDescriptor> findOnFields(Class<?> clazz, String targetName) {
        List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();

        List<Field> fields = getAllFields(clazz);

        for (Field field : fields) {
            if (field.isAnnotationPresent(SpecificationModel.class)) {
                properties.add(createCompositePropertyDescriptor(clazz, field, targetName));
            } else if (field.isAnnotationPresent(EditableProperty.class) && !field.isAnnotationPresent(Deprecated.class)) {
                properties.add(createProperty(clazz, field, targetName));
            }
        }

        return properties;
    }

    private CompositePropertyDescriptor createCompositePropertyDescriptor(Class<?> clazz, Field field, String targetName) {
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        SpecificationModel annotation = field.getAnnotation(SpecificationModel.class);

        CompositePropertyDescriptor compositePropertyDescriptor = new CompositePropertyDescriptor(createProperty(clazz, field, targetName));
        compositePropertyDescriptor.getProperties().addAll(extractProperties(fieldType, targetName != null ? targetName + "." + fieldName : fieldName));

        List<PropertyModel> propertyModels = specificationRegistry.getSpecifications(annotation.category());
        compositePropertyDescriptor.setPropertyModels(propertyModels);
        compositePropertyDescriptor.setSpecificationType(annotation.type());
        compositePropertyDescriptor.setSpecificationEditor(annotation.editor());
        
        return compositePropertyDescriptor;
    }

    private PropertyDescriptor createProperty(Class<?> clazz, Field field, String targetName) {
        PropertyDescriptor property = null;
        
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        EditableProperty annotation = field.getAnnotation(EditableProperty.class);

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

            LOG.debug("Created PropertyDescriptor for property " + propertyName + " on component " + clazz.getName());
            property = new PropertyDescriptor(propertyName, targetName, fieldType, getter.getName(), setter.getName(),
                    annotation.defaultable(), validator);
            
        }   
    
        return property;
    }

    private Collection<? extends PropertyDescriptor> findOnMethods(Class<?> clazz, String targetName) {
        List<PropertyDescriptor> properties = new ArrayList<PropertyDescriptor>();

        for (Method method : clazz.getMethods()) {
            if (method.getName().startsWith("get")) {
                try {
                    String propertyName = method.getName().substring(3);
                    Class<?> propertyType = method.getReturnType();
                    if (method.isAnnotationPresent(SpecificationModel.class)) {
                        properties.addAll(extractProperties(propertyType, targetName != null ? targetName + "." + propertyName : propertyName));
                    } else if (method.isAnnotationPresent(EditableProperty.class) && !method.isAnnotationPresent(Deprecated.class)) {
                        EditableProperty annotation = method.getAnnotation(EditableProperty.class);

                        if (!annotation.name().equals("")) {
                            propertyName = method.getName().substring(3);
                        }
                        
                        IPropertyValidator validator = getPropertyValidator(annotation.validatorClass());
                        Method setter = clazz.getMethod("set" + method.getName().substring(3), method.getReturnType());

                        LOG.debug("Created PropertyDescriptor for property " + propertyName + " on component " + clazz.getName());
                        PropertyDescriptor property = new PropertyDescriptor(propertyName, targetName, propertyType, method.getName(),
                                setter.getName(), annotation.defaultable(), validator);
                        properties.add(property);
                        
                    }
                } catch (NoSuchMethodException e) {
                    LOG.debug("No matching setter found for \"" + method.getName() + "\". Skipping...");
                }
            }
        }

        return properties;
    }
    
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();

        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }

        return fields;
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
