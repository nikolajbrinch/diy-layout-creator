package org.diylc.components.registry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.diylc.core.EventType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.events.EventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentTypeLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTypeLoader.class);

    private final EventDispatcher<EventType> eventDispatcher = new EventDispatcher<>();

    private final ComponentTypeFactory componentTypeFactory = new ComponentTypeFactory();

    private final ComponentLoader componentLoader = new ComponentLoader();

    private final ComponentIconLoader iconLoader = new ComponentIconLoader();

    EventDispatcher<EventType> getEventDispatcher() {
        return eventDispatcher;
    }

    ComponentTypeFactory getComponentTypeFactory() {
        return componentTypeFactory;
    }

    ComponentLoader getComponentLoader() {
        return componentLoader;
    }

    ComponentIconLoader getIconLoader() {
        return iconLoader;
    }

    Map<String, List<ComponentType>> loadComponentTypes(Path[] directories) {
        return loadComponentTypes(Thread.currentThread().getContextClassLoader(), directories);
    }

    private Map<String, List<ComponentType>> loadComponentTypes(ClassLoader classLoader, Path[] directories) {
        LOG.info("Loading component types.");

        Set<Class<? extends IDIYComponent>> componentClasses = getComponentLoader().loadComponents(classLoader, directories);

        Map<String, List<ComponentType>> componentTypes = new HashMap<String, List<ComponentType>>();

        for (Class<? extends IDIYComponent> componentClass : componentClasses) {
            ComponentType componentType = loadComponentType((Class<? extends IDIYComponent>) componentClass);

            if (componentType != null) {
                List<ComponentType> components = componentTypes.get(componentType.getCategory());

                if (components == null) {
                    components = new ArrayList<ComponentType>();
                    componentTypes.put(componentType.getCategory(), components);
                }

                components.add(componentType);
            }
        }

        return componentTypes;
    }

    private ComponentType loadComponentType(Class<? extends IDIYComponent> clazz) {
        ComponentType componentType = null;

        try {
            getEventDispatcher().sendEvent(EventType.SPLASH_UPDATE, "Loading component: " + clazz.getName());
            LOG.debug("Loading component: \"" + clazz.getName() + "\"");

            IDIYComponent component = (IDIYComponent) clazz.newInstance();

            componentType = getComponentTypeFactory().newComponentType(component);

            componentType.setIcon(getIconLoader().loadIcon(component));

        } catch (InstantiationException | IllegalAccessException e) {
            LOG.warn("Failure loading \"" + clazz.getName() + "\"", e);
        }

        return componentType;
    }

    @SuppressWarnings("unchecked")
    ComponentType loadComponentType(String className) throws ClassNotFoundException {
        return loadComponentType((Class<? extends IDIYComponent>) Class.forName(className));

    }

}
