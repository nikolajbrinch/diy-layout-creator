package org.diylc.core.components.registry;

import java.util.List;

import javax.swing.Icon;

import org.diylc.core.components.BomPolicy;
import org.diylc.core.components.annotations.ComponentAutoEdit;
import org.diylc.core.components.annotations.ComponentBomPolicy;
import org.diylc.core.components.annotations.ComponentCreationMethod;
import org.diylc.core.components.annotations.ComponentDescriptor;
import org.diylc.core.components.annotations.ComponentLayer;
import org.diylc.core.components.annotations.ComponentPads;
import org.diylc.core.components.annotations.ComponentEditOptions;
import org.diylc.core.components.properties.PropertyDescriptor;
import org.diylc.core.components.properties.PropertyDescriptorExtractor;
import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.CreationMethod;
import org.diylc.core.components.IDIYComponent;

public class ComponentModelFactory {

    private final PropertyDescriptorExtractor propertyDescriptorExtractor;

    public ComponentModelFactory(PropertyDescriptorExtractor propertyDescriptorExtractor) {
        this.propertyDescriptorExtractor = propertyDescriptorExtractor;
    }

    public ComponentModel newComponentType(String componentId, IDIYComponent componentInstance) {
        @SuppressWarnings("unchecked")
        Class<? extends IDIYComponent> instanceClass = (Class<IDIYComponent>) componentInstance.getClass();

        String name = instanceClass.getSimpleName();
        String description = "";
        String category = "Uncategorized";
        String namePrefix = "Unknown";
        String author = "Unknown";
        Icon icon = null;
        CreationMethod creationMethod = CreationMethod.SINGLE_CLICK;
        double zOrder = IDIYComponent.COMPONENT;
        boolean flexibleZOrder = false;
        boolean stretchable = true;
        BomPolicy bomPolicy = BomPolicy.SHOW_ALL_NAMES;
        boolean autoEdit = false;
        boolean pads = true;
        boolean rotatable = true;

        if (instanceClass.isAnnotationPresent(ComponentDescriptor.class)) {
            ComponentDescriptor annotation = instanceClass.getAnnotation(ComponentDescriptor.class);
            name = annotation.name();
            description = annotation.description();
            category = annotation.category();
            namePrefix = annotation.instanceNamePrefix();
            author = annotation.author();
        }

        if (instanceClass.isAnnotationPresent(ComponentAutoEdit.class)) {
            ComponentAutoEdit annotation = instanceClass.getAnnotation(ComponentAutoEdit.class);
            autoEdit = annotation.value();
        }

        if (instanceClass.isAnnotationPresent(ComponentPads.class)) {
            ComponentPads annotation = instanceClass.getAnnotation(ComponentPads.class);
            pads = annotation.value();
        }

        if (instanceClass.isAnnotationPresent(ComponentBomPolicy.class)) {
            ComponentBomPolicy annotation = instanceClass.getAnnotation(ComponentBomPolicy.class);
            bomPolicy = annotation.value();
        }

        if (instanceClass.isAnnotationPresent(ComponentEditOptions.class)) {
            ComponentEditOptions annotation = instanceClass.getAnnotation(ComponentEditOptions.class);
            rotatable = annotation.rotatable();
            stretchable = annotation.stretchable();
        }

        if (instanceClass.isAnnotationPresent(ComponentLayer.class)) {
            ComponentLayer annotation = instanceClass.getAnnotation(ComponentLayer.class);
            zOrder = annotation.value();
            flexibleZOrder = annotation.flexible();
        }

        if (instanceClass.isAnnotationPresent(ComponentCreationMethod.class)) {
            ComponentCreationMethod annotation = instanceClass.getAnnotation(ComponentCreationMethod.class);
            creationMethod = annotation.value();
        }

        List<PropertyDescriptor> propertyDescriptors = propertyDescriptorExtractor.extractProperties(instanceClass);

        ComponentModel componentModel = new ComponentModel(componentId, name, description, creationMethod, category, namePrefix, author,
                icon, instanceClass, zOrder, flexibleZOrder, stretchable, bomPolicy, autoEdit, pads, rotatable, propertyDescriptors);

        return componentModel;
    }

}
