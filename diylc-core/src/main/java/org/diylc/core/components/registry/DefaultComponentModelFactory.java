package org.diylc.core.components.registry;

import javax.swing.Icon;

import org.diylc.core.ComponentDescriptor;
import org.diylc.core.IDIYComponent;
import org.diylc.core.annotations.BomPolicy;
import org.diylc.core.components.ComponentModel;
import org.diylc.core.components.DefaultComponentModel;
import org.diylc.core.components.CreationMethod;

public class DefaultComponentModelFactory implements ComponentModelFactory {

    @Override
    public ComponentModel newComponentModel(String componentId, IDIYComponent componentInstance) {
        String name;
        String description;
        CreationMethod creationMethod;
        String category;
        String namePrefix;
        String author;
        Icon icon = null;
        double zOrder;
        boolean flexibleZOrder;
        boolean stretchable;
        BomPolicy bomPolicy;
        boolean autoEdit;
        boolean rotatable;
        
        @SuppressWarnings("unchecked")
        Class<? extends IDIYComponent> instanceClass = (Class<IDIYComponent>) componentInstance.getClass();
        
        if (instanceClass.isAnnotationPresent(ComponentDescriptor.class)) {
            ComponentDescriptor annotation = instanceClass.getAnnotation(ComponentDescriptor.class);
            name = annotation.name();
            description = annotation.description();
            creationMethod = annotation.creationMethod();
            category = annotation.category();
            namePrefix = annotation.instanceNamePrefix();
            author = annotation.author();
            zOrder = annotation.zOrder();
            flexibleZOrder = annotation.flexibleZOrder();
            stretchable = annotation.stretchable();
            bomPolicy = annotation.bomPolicy();
            autoEdit = annotation.autoEdit();
            rotatable = annotation.rotatable();
        } else {
            name = instanceClass.getSimpleName();
            description = "";
            creationMethod = CreationMethod.SINGLE_CLICK;
            category = "Uncategorized";
            namePrefix = "Unknown";
            author = "Unknown";
            zOrder = IDIYComponent.COMPONENT;
            flexibleZOrder = false;
            stretchable = true;
            bomPolicy = BomPolicy.SHOW_ALL_NAMES;
            autoEdit = true;
            rotatable = true;
        }
        
        return new DefaultComponentModel(
                componentId,
                name, 
                description, 
                creationMethod,
                category, 
                namePrefix, 
                author, 
                icon,
                instanceClass, 
                zOrder, 
                flexibleZOrder,
                stretchable, 
                bomPolicy, 
                autoEdit, 
                rotatable);
    }

}
