package org.diylc.core.components.registry;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;
import org.diylc.core.Resource;
import org.diylc.core.ResourceLoader;
import org.diylc.core.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathComponentClassLoader extends AbstractComponentClassLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ClasspathComponentClassLoader.class);

    private ResourceLoader resourceLoader = new ResourceLoader();

    @SuppressWarnings("unchecked")
    Map<String, Class<? extends IDIYComponent>> loadCompiledClasses(ClassLoader classLoader, ProgressView progressView)
            throws IOException {
        Map<String, Class<? extends IDIYComponent>> componentClasses = new LinkedHashMap<String, Class<? extends IDIYComponent>>();

        Set<Resource> classResources = resourceLoader.getResources(classLoader, COMPONENT_PACKAGE_NAME, CLASS_EXTENSION);

        double count = classResources.size();
        double counter = 1;
        for (Resource resource : classResources) {
            progressView.update("Loading component class: " + Math.round(counter / count * 100d) + "%");
            LOG.debug("Loading component class: \"" + resource + "\"");
            try {
                String filename = resource.getFilename();
                Class<?> clazz = Class.forName(filename.substring(0, filename.lastIndexOf('.')).replace('/', '.'));

                if (isValidComponentClass(clazz)) {
                    String id = (String) ReflectionUtils.getStaticProperty(clazz, "id");

                    if (id == null || id.length() <= 0) {
                        LOG.warn("No id defined for component class: " + clazz.getName() + ", component not loaded");
                    } else {
                        try {
                            @SuppressWarnings("unused")
                            UUID uuid = UUID.fromString(id);

                            Class<? extends IDIYComponent> alreadyDefinedComponentClass = componentClasses.get(id);

                            if (alreadyDefinedComponentClass != null) {
                                LOG.warn("Id defined for component class: " + clazz.getName() + ", has already been used by component: " + alreadyDefinedComponentClass.getName() + ", component not loaded");
                            } else {
                                componentClasses.put(id, (Class<? extends IDIYComponent>) clazz);
                            }
                        } catch (Exception e) {
                            LOG.warn("Id defined for component class: " + clazz.getName() + " is not a UUID, component not loaded");
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Class was not found", e);
            }
            counter++;
        }

        return componentClasses;
    }

}
