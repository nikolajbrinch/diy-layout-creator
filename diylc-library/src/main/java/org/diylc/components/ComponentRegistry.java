package org.diylc.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.common.ComponentType;

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
        return componentTypes;
    }

    public ComponentType getComponentType(String className) {
        return components.get(className);
    }
}
