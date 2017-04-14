package org.diylc.components.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.diylc.core.IPropertyValidator;
import org.diylc.core.PropertyWrapper;
import org.diylc.core.annotations.EditableProperty;
import org.diylc.specifications.CustomSpecification;
import org.diylc.specifications.Specification;
import org.diylc.specifications.SpecificationModel;
import org.diylc.specifications.SpecificationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyExtractor.class);

    private Map<String, List<PropertyWrapper>> propertyCache = new HashMap<String, List<PropertyWrapper>>();

    private Map<String, IPropertyValidator> propertyValidatorCache = new HashMap<String, IPropertyValidator>();

    private final ComponentRegistry componentRegistry;

    public PropertyExtractor(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    /**
     * Extracts all editable properties from the component class.
     * 
     * @param clazz
     * @return
     */
    public List<PropertyWrapper> extractProperties(Class<?> clazz) {
        return extractProperties(clazz, null);
    }

    public List<PropertyWrapper> extractProperties(Class<?> clazz, String targetName) {
        List<PropertyWrapper> properties = propertyCache.get(clazz.getName());

        if (properties == null) {
            properties = new ArrayList<PropertyWrapper>();

            properties.addAll(findOnFields(clazz, targetName));
            properties.addAll(findOnMethods(clazz, targetName));

            propertyCache.put(clazz.getName(), properties);
        }

        return cloneProperties(properties);
    }

    public List<PropertyWrapper> cloneProperties(List<PropertyWrapper> properties) {
        return properties.parallelStream().map((t) -> t.copy()).collect(Collectors.toList());
    }

    private PropertyWrapper createFieldProperty(Class<?> clazz, Field field, String targetName) {
        PropertyWrapper property = null;
        
        if (field.isAnnotationPresent(SpecificationModel.class)) {
            property = createSpecificationProperty(clazz, field, targetName);
        } else if (field.isAnnotationPresent(EditableProperty.class) && !field.isAnnotationPresent(Deprecated.class)) {
            property = createProperty(clazz, field, targetName);
        }
        
        return property;
    }
    
    private Collection<? extends PropertyWrapper> findOnFields(Class<?> clazz, String targetName) {
        return getAllFields(clazz).parallelStream()
            .map((f) -> createFieldProperty(clazz, f, targetName))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private SpecificationProperty createSpecificationProperty(Class<?> clazz, Field field, String targetName) {
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        SpecificationModel annotation = field.getAnnotation(SpecificationModel.class);

        SpecificationProperty specificationProperty = new SpecificationProperty(createProperty(clazz, field, targetName));
        specificationProperty.getProperties()
                .addAll(extractProperties(fieldType, targetName != null ? targetName + "." + fieldName : fieldName));

        LinkedList<Specification> specifications = new LinkedList<>(componentRegistry.getSpecifications(annotation.category()));
        specifications.sort(ComparatorFactory.getInstance().getSpecificationNameComparator());

        specifications.addFirst(new CustomSpecification());

        specificationProperty.setSpecifications(specifications);
        specificationProperty.setSpecificationType(annotation.type());
        specificationProperty.setSpecificationEditor(annotation.editor());

        return specificationProperty;
    }

    private PropertyWrapper createProperty(Class<?> clazz, Field field, String targetName) {
        PropertyWrapper property = null;

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
            throw new IllegalStateException(
                    "Getter for property \"" + propertyName + "\" for class: " + clazz.getName() + " is not public");
        }

        if (setter != null && getter != null) {
            IPropertyValidator validator = getPropertyValidator(annotation.validatorClass());

            LOG.debug("Created PropertyWrapper for property " + propertyName + " on component " + clazz.getName());
            property = new PropertyWrapper(propertyName, targetName, fieldType, getter.getName(), setter.getName(),
                    annotation.defaultable(), validator);

        }

        return property;
    }

    private List<PropertyWrapper> createMethodProperty(Class<?> clazz, Method method, String targetName) {
        List<PropertyWrapper> properties = new ArrayList<>();
        
        try {
            String propertyName = method.getName().substring(3);
            Class<?> propertyType = method.getReturnType();
            if (method.isAnnotationPresent(SpecificationModel.class)) {
                properties.addAll(
                        extractProperties(propertyType, targetName != null ? targetName + "." + propertyName : propertyName));
            } else if (method.isAnnotationPresent(EditableProperty.class) && !method.isAnnotationPresent(Deprecated.class)) {
                EditableProperty annotation = method.getAnnotation(EditableProperty.class);

                if (!annotation.name().equals("")) {
                    propertyName = method.getName().substring(3);
                }

                IPropertyValidator validator = getPropertyValidator(annotation.validatorClass());
                Method setter = clazz.getMethod("set" + method.getName().substring(3), method.getReturnType());

                LOG.debug("Created PropertyWrapper for property " + propertyName + " on component " + clazz.getName());
                PropertyWrapper property = new PropertyWrapper(propertyName, targetName, propertyType, method.getName(),
                        setter.getName(), annotation.defaultable(), validator);
                properties.add(property);

            }
        } catch (NoSuchMethodException e) {
            LOG.debug("No matching setter found for \"" + method.getName() + "\". Skipping...");
        }
        
        return properties;
    }
    
    private Collection<? extends PropertyWrapper> findOnMethods(Class<?> clazz, String targetName) {
        return Arrays.stream(clazz.getMethods())
            .parallel()
            .filter((m) -> m.getName().startsWith("get"))
            .flatMap((m) -> createMethodProperty(clazz, m, targetName).parallelStream())
            .collect(Collectors.toList());
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
