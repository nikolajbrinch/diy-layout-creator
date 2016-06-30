package org.diylc.core.components.registry;

import org.diylc.core.IDIYComponent;
import org.diylc.core.ProgressView;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ComponentLoader {

    private final GroovyComponentClassLoader groovyComponentClassLoader = new GroovyComponentClassLoader();

    private final ClasspathComponentClassLoader classpathComponentClassLoader = new ClasspathComponentClassLoader();

    GroovyComponentClassLoader getGroovyComponentClassLoader() {
        return groovyComponentClassLoader;
    }

    ClasspathComponentClassLoader getClasspathComponentClassLoader() {
        return classpathComponentClassLoader;
    }

    public Map<String, Class<? extends IDIYComponent>> loadComponents(ClassLoader classLoader, Path[] directories, ProgressView progressView) throws IOException {
        Map<String, Class<? extends IDIYComponent>> componentClasses = new LinkedHashMap<>();

         /*
          * Load from directories if there are any
          */
        if (directories.length > 0) {
            componentClasses.putAll(getGroovyComponentClassLoader().loadGroovyClasses(classLoader, directories, progressView));
        }

         /*
          * If nothing got loaded, load from classpath
          */
        if (componentClasses.isEmpty()) {
            componentClasses.putAll(getClasspathComponentClassLoader().loadCompiledClasses(classLoader, progressView));
        }

        return componentClasses;
    }
}
