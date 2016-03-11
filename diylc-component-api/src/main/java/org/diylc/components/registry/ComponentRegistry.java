package org.diylc.components.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.diylc.core.ComponentType;
import org.diylc.core.IDIYComponent;
import org.diylc.specifications.Specification;
import org.diylc.specifications.registry.SpecificationRegistry;

public class ComponentRegistry {

    private final Map<String, ComponentType> components = new HashMap<String, ComponentType>();

    private final ComponentTypes componentTypes;

    private SpecificationRegistry specificationRegistry;

    ComponentRegistry(ComponentTypes componentTypes, SpecificationRegistry specificationRegistry) {
        this.componentTypes = componentTypes;
        this.specificationRegistry = specificationRegistry;
        
        for (ComponentType component : componentTypes.getComponents()) {
            getComponents().put(component.getInstanceClass().getName(), component);
        }
    }
    
    Map<String, ComponentType> getComponents() {
        return components;
    }

    public ComponentTypes getComponentTypes() {
        return componentTypes;
    }

    public ComponentType getComponentType(Class<? extends IDIYComponent> clazz) {
        return getComponentType(clazz.getName());
    }

    public ComponentType getComponentType(IDIYComponent component) {
        return getComponentType(component.getClass().getName());
    }

    public ComponentType getComponentType(String className) {
        return getComponents().get(className);
    }

    public Collection<Specification> getSpecifications(String category) {
        return specificationRegistry.get(category);
    }

}
