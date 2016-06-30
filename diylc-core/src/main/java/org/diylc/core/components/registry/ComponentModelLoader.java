package org.diylc.core.components.registry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;
import org.diylc.core.components.ComponentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentModelLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentModelLoader.class);

    private final ComponentModelFactory componentModelFactory = new DefaultComponentModelFactory();

    private final ComponentLoader componentLoader = new ComponentLoader();

    private final ComponentIconLoader iconLoader = new ComponentIconLoader();

    ComponentModelFactory getComponentModelFactory() {
        return componentModelFactory;
    }

    ComponentLoader getComponentLoader() {
        return componentLoader;
    }

    ComponentIconLoader getIconLoader() {
        return iconLoader;
    }

    ComponentModels loadComponentTypes(Path[] directories, ProgressView progressView) throws IOException {
        return loadComponentTypes(Thread.currentThread().getContextClassLoader(), directories, progressView);
    }

    private ComponentModels loadComponentTypes(ClassLoader classLoader, Path[] directories, ProgressView progressView) throws IOException {
        LOG.info("Loading component types.");

        Map<String, Class<? extends IDIYComponent>> componentClasses = getComponentLoader().loadComponents(classLoader, directories, progressView);

        ComponentModels componentModels = new ComponentModels();

        for (Map.Entry<String, Class<? extends IDIYComponent>> componentEntry : componentClasses.entrySet()) {
            String componentId = componentEntry.getKey();
            Class<? extends IDIYComponent> componentClass = componentEntry.getValue();

            ComponentModel componentModel = loadComponentType(componentId, componentClass, progressView);

            componentModels.add(componentModel);
        }


        return componentModels;
    }

    private ComponentModel loadComponentType(String componentId, Class<? extends IDIYComponent> clazz, ProgressView progressView) {
        ComponentModel componentModel = null;

        try {
            progressView.update("Loading component: " + clazz.getName());
            LOG.debug("Loading component: \"" + clazz.getName() + "\"");

            IDIYComponent component = (IDIYComponent) clazz.newInstance();

            componentModel = getComponentModelFactory().newComponentModel(componentId, component);

            componentModel.setIcon(getIconLoader().loadIcon(component));

        } catch (InstantiationException | IllegalAccessException e) {
            LOG.warn("Failure loading \"" + clazz.getName() + "\"", e);
        }

        return componentModel;
    }

//    @SuppressWarnings("unchecked")
//    ComponentType loadComponentType(String className, ProgressView progressView) throws ClassNotFoundException {
//        return loadComponentType((Class<? extends IDIYComponent>) Class.forName(className), progressView);
//
//    }

}
