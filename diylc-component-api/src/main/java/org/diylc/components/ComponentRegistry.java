package org.diylc.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.core.IDIYComponent;

public enum ComponentRegistry {

    INSTANCE;

    ComponentTypeLoader componentTypeLoader = new ComponentTypeLoader();

    Map<String, List<ComponentType>> componentTypes = new HashMap<String, List<ComponentType>>();

    Map<String, ComponentType> components = new HashMap<String, ComponentType>();

    private ComponentRegistry() {
        componentTypes = componentTypeLoader.loadComponentTypes();

        for (String category : componentTypes.keySet()) {
            for (ComponentType component : componentTypes.get(category)) {
                components.put(component.getInstanceClass().getName(), component);
            }
        }
    }

    public Map<String, List<ComponentType>> getComponentTypes() {
        return Collections.unmodifiableMap(componentTypes);
    }

    public ComponentType getComponentType(Class<? extends IDIYComponent<?>> clazz) {
        return getComponentType(clazz.getName());
    }

    public ComponentType getComponentType(IDIYComponent<?> component) {
        return getComponentType(component.getClass().getName());
    }

    public ComponentType getComponentType(String className) {
        return components.get(className);
    }

}
