package org.diylc.components.registry;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;
import org.diylc.core.Resource;
import org.diylc.core.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathComponentClassLoader extends AbstractComponentClassLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ClasspathComponentClassLoader.class);

    private ResourceLoader resourceLoader = new ResourceLoader();

    @SuppressWarnings("unchecked")
    Collection<? extends Class<? extends IDIYComponent>> loadCompiledClasses(ClassLoader classLoader, ProgressView progressView)
            throws IOException {
        Set<Class<? extends IDIYComponent>> componentClasses = new HashSet<Class<? extends IDIYComponent>>();

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
                    componentClasses.add((Class<? extends IDIYComponent>) clazz);
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Class was not found", e);
            }
            counter++;
        }

        return componentClasses;
    }

}
