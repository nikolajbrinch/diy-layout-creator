package org.diylc.components.registry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import org.diylc.core.ComponentType;
import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentTypeLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentTypeLoader.class);

    private final ComponentTypeFactory componentTypeFactory = new ComponentTypeFactory();

    private final ComponentLoader componentLoader = new ComponentLoader();

    private final ComponentIconLoader iconLoader = new ComponentIconLoader();

    ComponentTypeFactory getComponentTypeFactory() {
        return componentTypeFactory;
    }

    ComponentLoader getComponentLoader() {
        return componentLoader;
    }

    ComponentIconLoader getIconLoader() {
        return iconLoader;
    }

    ComponentTypes loadComponentTypes(Path[] directories, ProgressView progressView) throws IOException {
        return loadComponentTypes(Thread.currentThread().getContextClassLoader(), directories, progressView);
    }

    private ComponentTypes loadComponentTypes(ClassLoader classLoader, Path[] directories, ProgressView progressView) throws IOException {
        LOG.debug("Loading component types.");

        Set<Class<? extends IDIYComponent>> componentClasses = getComponentLoader().loadComponents(classLoader, directories, progressView);

        ComponentTypes componentTypes = new ComponentTypes();

        for (Class<? extends IDIYComponent> componentClass : componentClasses) {
            ComponentType componentType = loadComponentType((Class<? extends IDIYComponent>) componentClass, progressView);

            componentTypes.add(componentType);
        }

        return componentTypes;
    }

    private ComponentType loadComponentType(Class<? extends IDIYComponent> clazz, ProgressView progressView) {
        ComponentType componentType = null;

        try {
            progressView.update("Loading component: " + clazz.getName());
            LOG.trace("Loading component: \"" + clazz.getName() + "\"");

            IDIYComponent component = (IDIYComponent) clazz.newInstance();

            componentType = getComponentTypeFactory().newComponentType(component);

            componentType.setIcon(getIconLoader().loadIcon(component));

        } catch (InstantiationException | IllegalAccessException e) {
            LOG.warn("Failure loading \"" + clazz.getName() + "\"", e);
        }

        return componentType;
    }

//    @SuppressWarnings("unchecked")
//    ComponentType loadComponentType(String className, ProgressView progressView) throws ClassNotFoundException {
//        return loadComponentType((Class<? extends IDIYComponent>) Class.forName(className), progressView);
//
//    }

}
