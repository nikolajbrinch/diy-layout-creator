package org.diylc.components.registry;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;
import org.diylc.core.config.Configuration;

public enum ComponentRegistry {

    INSTANCE;

    private final ComponentTypeLoader componentTypeLoader = new ComponentTypeLoader();

    private final Map<String, ComponentType> components = new HashMap<String, ComponentType>();

    private Map<String, List<ComponentType>> componentTypes;

    private ComponentRegistry() {
    }
    
    public void init(ProgressView progressView) throws IOException {
        componentTypes = getComponentTypeLoader().loadComponentTypes(Configuration.INSTANCE.getComponentDirectories(), progressView);

        for (String category : getComponentTypes().keySet()) {
            for (ComponentType component : getComponentTypes().get(category)) {
                getComponents().put(component.getInstanceClass().getName(), component);
            }
        }
    }

    Map<String, ComponentType> getComponents() {
        return components;
    }

    ComponentTypeLoader getComponentTypeLoader() {
        return componentTypeLoader;
    }

    public Map<String, List<ComponentType>> getComponentTypes() {
        return Collections.unmodifiableMap(componentTypes);
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

}
